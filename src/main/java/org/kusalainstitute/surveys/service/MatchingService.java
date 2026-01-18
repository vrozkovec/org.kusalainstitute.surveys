package org.kusalainstitute.surveys.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jdbi.v3.core.Jdbi;
import org.kusalainstitute.surveys.dao.MatchDao;
import org.kusalainstitute.surveys.dao.PersonDao;
import org.kusalainstitute.surveys.dao.PostSurveyDao;
import org.kusalainstitute.surveys.dao.PreSurveyDao;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.PersonMatch;
import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;
import org.kusalainstitute.surveys.pojo.enums.MatchType;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;
import org.kusalainstitute.surveys.wicket.model.MatchRowData;
import org.kusalainstitute.surveys.wicket.model.UnmatchedPersonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Service for matching pre-survey and post-survey respondents. Matching is done within cohorts
 * using email and name similarity.
 */
@Singleton
public class MatchingService
{

	private static final Logger LOG = LoggerFactory.getLogger(MatchingService.class);
	private static final double NAME_SIMILARITY_THRESHOLD = 0.8;
	private static final String WILDCARD_COHORT = "all?";

	private final Jdbi jdbi;
	private final LevenshteinDistance levenshtein;
	private final ManualMatchPersistenceService manualMatchPersistence;

	/**
	 * Creates a new MatchingService with injected dependencies.
	 *
	 * @param jdbi
	 *            the JDBI instance for database access
	 * @param manualMatchPersistence
	 *            service for persisting manual matches
	 */
	@Inject
	public MatchingService(Jdbi jdbi, ManualMatchPersistenceService manualMatchPersistence)
	{
		this.jdbi = jdbi;
		this.manualMatchPersistence = manualMatchPersistence;
		this.levenshtein = new LevenshteinDistance();
	}

	/**
	 * Runs automatic matching on all unmatched persons.
	 * <p>
	 * Handles special cases:
	 * <ul>
	 * <li>"all?" cohort: PRE responses with cohort="all?" are matched against ALL non-"all?" POST cohorts</li>
	 * <li>Shared emails: Emails used by multiple people with different names are matched by name only</li>
	 * </ul>
	 *
	 * @return number of new matches created
	 */
	public MatchResult runAutoMatch()
	{
		LOG.info("Running automatic matching...");

		return jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			MatchDao matchDao = handle.attach(MatchDao.class);

			List<Person> prePeople = personDao.findUnmatchedPre();
			List<Person> postPeople = personDao.findUnmatchedPost();

			LOG.info("Found {} unmatched pre-survey and {} unmatched post-survey persons", prePeople.size(), postPeople.size());

			// Identify shared emails (same email, different names) - these should match by name only
			Set<String> sharedEmails = findSharedEmails(prePeople);
			if (!sharedEmails.isEmpty())
			{
				LOG.info("Found {} shared emails that will be matched by name only", sharedEmails.size());
			}

			// Group by cohort for efficient matching
			Map<String, List<Person>> preByCohort = groupByCohort(prePeople);
			Map<String, List<Person>> postByCohort = groupByCohort(postPeople);

			// Collect all non-wildcard POST cohorts for "all?" matching
			List<Person> allNonWildcardPost = postByCohort.entrySet().stream()
				.filter(e -> !WILDCARD_COHORT.equals(e.getKey()))
				.flatMap(e -> e.getValue().stream())
				.toList();

			int emailMatches = 0;
			int nameMatches = 0;

			// Track globally matched POST people (for "all?" cohort matching across all cohorts)
			Set<Long> matchedPostIds = new HashSet<>();

			for (String preCohort : preByCohort.keySet())
			{
				List<Person> preInCohort = preByCohort.get(preCohort);

				// Determine target POST people: "all?" matches against all non-wildcard cohorts
				List<Person> targetPostPeople;
				if (WILDCARD_COHORT.equals(preCohort))
				{
					targetPostPeople = allNonWildcardPost;
					LOG.info("Processing '{}' cohort PRE people ({}) against all POST cohorts ({})",
						preCohort, preInCohort.size(), targetPostPeople.size());
				}
				else
				{
					targetPostPeople = postByCohort.getOrDefault(preCohort, List.of());
				}

				if (targetPostPeople.isEmpty())
				{
					continue;
				}

				// Filter out already matched POST people
				List<Person> unmatchedPre = new ArrayList<>(preInCohort);
				List<Person> unmatchedPost = new ArrayList<>(targetPostPeople.stream()
					.filter(p -> !matchedPostIds.contains(p.getId()))
					.toList());

				// First pass: exact email matching (skip people with shared emails)
				for (Person pre : new ArrayList<>(unmatchedPre))
				{
					if (pre.isRequiresManualMatch() || StringUtils.isBlank(pre.getNormalizedEmail()))
					{
						continue;
					}

					// Skip email matching for shared emails - these should match by name only
					if (sharedEmails.contains(pre.getNormalizedEmail()))
					{
						LOG.debug("Skipping email match for {} - shared email {}", pre.getName(), pre.getNormalizedEmail());
						continue;
					}

					for (Person post : new ArrayList<>(unmatchedPost))
					{
						if (post.isRequiresManualMatch())
						{
							continue;
						}

						if (pre.getNormalizedEmail().equals(post.getNormalizedEmail()))
						{
							// Use POST person's cohort for match record (especially important for "all?" PRE)
							String matchCohort = WILDCARD_COHORT.equals(preCohort) ? post.getCohort() : preCohort;
							createMatch(matchDao, matchCohort, pre, post, MatchType.AUTO_EMAIL, BigDecimal.ONE);
							unmatchedPre.remove(pre);
							unmatchedPost.remove(post);
							matchedPostIds.add(post.getId());
							emailMatches++;
							break;
						}
					}
				}

				// Second pass: fuzzy name matching
				for (Person pre : new ArrayList<>(unmatchedPre))
				{
					if (pre.isRequiresManualMatch() || StringUtils.isBlank(pre.getName()))
					{
						continue;
					}

					Person bestMatch = null;
					double bestSimilarity = 0;

					for (Person post : unmatchedPost)
					{
						if (post.isRequiresManualMatch() || StringUtils.isBlank(post.getName()))
						{
							continue;
						}

						double similarity = calculateNameSimilarity(pre.getName(), post.getName());
						if (similarity >= NAME_SIMILARITY_THRESHOLD && similarity > bestSimilarity)
						{
							bestSimilarity = similarity;
							bestMatch = post;
						}
					}

					if (bestMatch != null)
					{
						// Use POST person's cohort for match record (especially important for "all?" PRE)
						String matchCohort = WILDCARD_COHORT.equals(preCohort) ? bestMatch.getCohort() : preCohort;
						createMatch(matchDao, matchCohort, pre, bestMatch, MatchType.AUTO_NAME, BigDecimal.valueOf(bestSimilarity));
						unmatchedPre.remove(pre);
						unmatchedPost.remove(bestMatch);
						matchedPostIds.add(bestMatch.getId());
						nameMatches++;
					}
				}
			}

			LOG.info("Created {} email matches and {} name matches", emailMatches, nameMatches);
			return new MatchResult(emailMatches, nameMatches);
		});
	}

	/**
	 * Creates a manual match between a pre and post survey person.
	 *
	 * @param prePersonId
	 *            ID of the pre-survey person
	 * @param postPersonId
	 *            ID of the post-survey person
	 * @param matchedBy
	 *            username of person creating the match
	 * @param notes
	 *            optional notes about the match
	 * @return the created match
	 */
	public PersonMatch createManualMatch(Long prePersonId, Long postPersonId, String matchedBy, String notes)
	{
		return jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			MatchDao matchDao = handle.attach(MatchDao.class);

			Person pre = personDao.findById(prePersonId)
				.orElseThrow(() -> new IllegalArgumentException("Pre-survey person not found: " + prePersonId));
			Person post = personDao.findById(postPersonId)
				.orElseThrow(() -> new IllegalArgumentException("Post-survey person not found: " + postPersonId));

			if (pre.getSurveyType() != SurveyType.PRE)
			{
				throw new IllegalArgumentException("Person " + prePersonId + " is not a pre-survey respondent");
			}
			if (post.getSurveyType() != SurveyType.POST)
			{
				throw new IllegalArgumentException("Person " + postPersonId + " is not a post-survey respondent");
			}

			PersonMatch match = new PersonMatch(pre.getCohort(), prePersonId, postPersonId, MatchType.MANUAL, null);
			match.setMatchedBy(matchedBy);
			match.setNotes(notes);

			long matchId = matchDao.insert(match);
			match.setId(matchId);
			return match;
		});
	}

	/**
	 * Gets all unmatched pre-survey persons.
	 *
	 * @return list of unmatched pre-survey persons
	 */
	public List<Person> getUnmatchedPre()
	{
		return jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			return personDao.findUnmatchedPre();
		});
	}

	/**
	 * Gets all unmatched post-survey persons.
	 *
	 * @return list of unmatched post-survey persons
	 */
	public List<Person> getUnmatchedPost()
	{
		return jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			return personDao.findUnmatchedPost();
		});
	}

	/**
	 * Gets match statistics.
	 *
	 * @return match statistics
	 */
	public MatchDao.MatchStatistics getStatistics()
	{
		return jdbi.withHandle(handle -> {
			MatchDao matchDao = handle.attach(MatchDao.class);
			return matchDao.getStatistics();
		});
	}

	private void createMatch(MatchDao matchDao, String cohort, Person pre, Person post, MatchType matchType,
		BigDecimal confidence)
	{
		if (matchDao.exists(pre.getId(), post.getId()))
		{
			LOG.debug("Match already exists between {} and {}", pre.getId(), post.getId());
			return;
		}

		PersonMatch match = new PersonMatch(cohort, pre.getId(), post.getId(), matchType, confidence);
		matchDao.insert(match);
		LOG.debug("Created {} match: {} -> {} (confidence: {})", matchType, pre.getName(), post.getName(), confidence);
	}

	private Map<String, List<Person>> groupByCohort(List<Person> people)
	{
		Map<String, List<Person>> result = new HashMap<>();
		for (Person person : people)
		{
			result.computeIfAbsent(person.getCohort(), k -> new ArrayList<>()).add(person);
		}
		return result;
	}

	/**
	 * Finds emails that are shared by multiple people with different names.
	 * <p>
	 * Some survey respondents didn't have their own email address, so one email is shared by
	 * multiple family members or friends with different names. For these cases, matching should
	 * be based on name only, not email, to avoid incorrect matches.
	 *
	 * @param people
	 *            list of people to analyze
	 * @return set of normalized emails that are shared by multiple different-named people
	 */
	private Set<String> findSharedEmails(List<Person> people)
	{
		// Map: email -> set of unique names using that email
		Map<String, Set<String>> emailToNames = new HashMap<>();

		for (Person person : people)
		{
			String email = person.getNormalizedEmail();
			String name = person.getName();

			if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(name))
			{
				emailToNames.computeIfAbsent(email, k -> new HashSet<>()).add(name.toLowerCase().trim());
			}
		}

		// Find emails with multiple different names
		Set<String> sharedEmails = new HashSet<>();
		for (Map.Entry<String, Set<String>> entry : emailToNames.entrySet())
		{
			if (entry.getValue().size() > 1)
			{
				sharedEmails.add(entry.getKey());
				LOG.debug("Shared email detected: {} used by {} different people", entry.getKey(), entry.getValue().size());
			}
		}

		return sharedEmails;
	}

	/**
	 * Calculates name similarity using Levenshtein distance.
	 *
	 * @param name1
	 *            first name
	 * @param name2
	 *            second name
	 * @return similarity score between 0.0 and 1.0
	 */
	private double calculateNameSimilarity(String name1, String name2)
	{
		if (name1 == null || name2 == null)
		{
			return 0.0;
		}

		String n1 = name1.toLowerCase().trim();
		String n2 = name2.toLowerCase().trim();

		if (n1.equals(n2))
		{
			return 1.0;
		}

		int maxLength = Math.max(n1.length(), n2.length());
		if (maxLength == 0)
		{
			return 0.0;
		}

		int distance = levenshtein.apply(n1, n2);
		return 1.0 - ((double) distance / maxLength);
	}

	/**
	 * Creates a manual match between two persons and persists it to file for recovery after
	 * database rebuilds.
	 *
	 * @param prePersonId
	 *            ID of the pre-survey person
	 * @param postPersonId
	 *            ID of the post-survey person
	 * @param matchedBy
	 *            username of person creating the match
	 * @param notes
	 *            optional notes about the match
	 * @return the created match
	 */
	public PersonMatch createAndPersistManualMatch(Long prePersonId, Long postPersonId, String matchedBy, String notes)
	{
		return jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			MatchDao matchDao = handle.attach(MatchDao.class);
			PreSurveyDao preSurveyDao = handle.attach(PreSurveyDao.class);
			PostSurveyDao postSurveyDao = handle.attach(PostSurveyDao.class);

			Person pre = personDao.findById(prePersonId)
				.orElseThrow(() -> new IllegalArgumentException("Pre-survey person not found: " + prePersonId));
			Person post = personDao.findById(postPersonId)
				.orElseThrow(() -> new IllegalArgumentException("Post-survey person not found: " + postPersonId));

			if (pre.getSurveyType() != SurveyType.PRE)
			{
				throw new IllegalArgumentException("Person " + prePersonId + " is not a pre-survey respondent");
			}
			if (post.getSurveyType() != SurveyType.POST)
			{
				throw new IllegalArgumentException("Person " + postPersonId + " is not a post-survey respondent");
			}

			// Get timestamps from survey responses
			LocalDateTime preTimestamp = preSurveyDao.findByPersonId(prePersonId)
				.map(PreSurveyResponse::getTimestamp)
				.orElse(null);
			LocalDateTime postTimestamp = postSurveyDao.findByPersonId(postPersonId)
				.map(PostSurveyResponse::getTimestamp)
				.orElse(null);

			// Create database match
			PersonMatch match = new PersonMatch(pre.getCohort(), prePersonId, postPersonId, MatchType.MANUAL, null);
			match.setMatchedBy(matchedBy);
			match.setNotes(notes);

			long matchId = matchDao.insert(match);
			match.setId(matchId);

			// Persist to file for recovery
			manualMatchPersistence.saveManualMatch(pre, preTimestamp, post, postTimestamp, notes, matchedBy);

			return match;
		});
	}

	/**
	 * Applies stored manual matches from the persistence file after a database rebuild.
	 *
	 * @return number of matches successfully restored
	 */
	public int applyStoredManualMatches()
	{
		List<ManualMatchEntry> entries = manualMatchPersistence.getAllEntries();
		LOG.info("Attempting to restore {} stored manual matches", entries.size());

		int restoredCount = 0;

		for (ManualMatchEntry entry : entries)
		{
			try
			{
				boolean restored = jdbi.withHandle(handle -> {
					PersonDao personDao = handle.attach(PersonDao.class);
					MatchDao matchDao = handle.attach(MatchDao.class);

					// Find PRE person by composite key
					Optional<Person> preOpt = personDao.findByCompositeKey(
						entry.preEmail(),
						entry.preName(),
						SurveyType.PRE,
						entry.preTimestamp());

					// Find POST person by composite key
					Optional<Person> postOpt = personDao.findByCompositeKey(
						entry.postEmail(),
						entry.postName(),
						SurveyType.POST,
						entry.postTimestamp());

					if (preOpt.isPresent() && postOpt.isPresent())
					{
						Person pre = preOpt.get();
						Person post = postOpt.get();

						// Check if match already exists
						if (matchDao.exists(pre.getId(), post.getId()))
						{
							LOG.debug("Match already exists for {} -> {}", pre.getName(), post.getName());
							return true;
						}

						PersonMatch match = new PersonMatch(pre.getCohort(), pre.getId(), post.getId(), MatchType.MANUAL, null);
						match.setMatchedBy(entry.createdBy());
						match.setNotes(entry.notes());
						matchDao.insert(match);

						LOG.info("Restored manual match: {} -> {}", pre.getName(), post.getName());
						return true;
					}
					else
					{
						LOG.warn("Could not find persons for stored match: {} -> {}",
							entry.preName(), entry.postName());
						return false;
					}
				});

				if (restored)
				{
					restoredCount++;
				}
			}
			catch (Exception e)
			{
				LOG.error("Failed to restore match: {} -> {}", entry.preName(), entry.postName(), e);
			}
		}

		LOG.info("Restored {} of {} stored manual matches", restoredCount, entries.size());
		return restoredCount;
	}

	/**
	 * Gets all matches with full person and survey data for display.
	 *
	 * @return list of match row data
	 */
	public List<MatchRowData> getAllMatchesWithData()
	{
		return jdbi.withHandle(handle -> {
			MatchDao matchDao = handle.attach(MatchDao.class);
			PersonDao personDao = handle.attach(PersonDao.class);
			PreSurveyDao preSurveyDao = handle.attach(PreSurveyDao.class);
			PostSurveyDao postSurveyDao = handle.attach(PostSurveyDao.class);

			List<PersonMatch> matches = matchDao.findAll();
			List<MatchRowData> result = new ArrayList<>();

			for (PersonMatch match : matches)
			{
				Optional<Person> preOpt = personDao.findById(match.getPrePersonId());
				Optional<Person> postOpt = personDao.findById(match.getPostPersonId());

				if (preOpt.isPresent() && postOpt.isPresent())
				{
					Person pre = preOpt.get();
					Person post = postOpt.get();

					LocalDateTime preTimestamp = preSurveyDao.findByPersonId(pre.getId())
						.map(PreSurveyResponse::getTimestamp)
						.orElse(null);
					LocalDateTime postTimestamp = postSurveyDao.findByPersonId(post.getId())
						.map(PostSurveyResponse::getTimestamp)
						.orElse(null);

					result.add(new MatchRowData(
						match.getId(),
						match.getCohort(),
						match.getMatchType(),
						match.getMatchedBy(),
						match.getNotes(),
						pre.getId(),
						pre.getName(),
						pre.getEmail(),
						preTimestamp,
						post.getId(),
						post.getName(),
						post.getEmail(),
						postTimestamp));
				}
			}

			return result;
		});
	}

	/**
	 * Gets all unmatched PRE survey persons with their survey data.
	 *
	 * @return list of unmatched person data
	 */
	public List<UnmatchedPersonData> getUnmatchedPreWithData()
	{
		return jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			PreSurveyDao preSurveyDao = handle.attach(PreSurveyDao.class);

			List<Person> persons = personDao.findUnmatchedPre();
			List<UnmatchedPersonData> result = new ArrayList<>();

			for (Person person : persons)
			{
				LocalDateTime timestamp = preSurveyDao.findByPersonId(person.getId())
					.map(PreSurveyResponse::getTimestamp)
					.orElse(null);

				result.add(new UnmatchedPersonData(
					person.getId(),
					person.getCohort(),
					person.getName(),
					person.getEmail(),
					person.getSurveyType(),
					person.isRequiresManualMatch(),
					timestamp));
			}

			return result;
		});
	}

	/**
	 * Gets all unmatched POST survey persons with their survey data.
	 *
	 * @return list of unmatched person data
	 */
	public List<UnmatchedPersonData> getUnmatchedPostWithData()
	{
		return jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			PostSurveyDao postSurveyDao = handle.attach(PostSurveyDao.class);

			List<Person> persons = personDao.findUnmatchedPost();
			List<UnmatchedPersonData> result = new ArrayList<>();

			for (Person person : persons)
			{
				LocalDateTime timestamp = postSurveyDao.findByPersonId(person.getId())
					.map(PostSurveyResponse::getTimestamp)
					.orElse(null);

				result.add(new UnmatchedPersonData(
					person.getId(),
					person.getCohort(),
					person.getName(),
					person.getEmail(),
					person.getSurveyType(),
					person.isRequiresManualMatch(),
					timestamp));
			}

			return result;
		});
	}

	/**
	 * Result of automatic matching.
	 */
	public record MatchResult(int emailMatches, int nameMatches)
	{
		/**
		 * Gets the total number of matches created.
		 *
		 * @return total matches
		 */
		public int totalMatches()
		{
			return emailMatches + nameMatches;
		}
	}
}
