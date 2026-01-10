package org.kusalainstitute.surveys.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jdbi.v3.core.Jdbi;
import org.kusalainstitute.surveys.dao.MatchDao;
import org.kusalainstitute.surveys.dao.PersonDao;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.PersonMatch;
import org.kusalainstitute.surveys.pojo.enums.MatchType;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;
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

	private final Jdbi jdbi;
	private final LevenshteinDistance levenshtein;

	/**
	 * Creates a new MatchingService with injected JDBI instance.
	 *
	 * @param jdbi
	 *            the JDBI instance for database access
	 */
	@Inject
	public MatchingService(Jdbi jdbi)
	{
		this.jdbi = jdbi;
		this.levenshtein = new LevenshteinDistance();
	}

	/**
	 * Runs automatic matching on all unmatched persons.
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

			// Group by cohort for efficient matching
			Map<String, List<Person>> preByCohor = groupByCohort(prePeople);
			Map<String, List<Person>> postByCohort = groupByCohort(postPeople);

			int emailMatches = 0;
			int nameMatches = 0;

			for (String cohort : preByCohor.keySet())
			{
				List<Person> preInCohort = preByCohor.get(cohort);
				List<Person> postInCohort = postByCohort.getOrDefault(cohort, List.of());

				if (postInCohort.isEmpty())
				{
					continue;
				}

				// First pass: exact email matching
				List<Person> unmatchedPre = new ArrayList<>(preInCohort);
				List<Person> unmatchedPost = new ArrayList<>(postInCohort);

				for (Person pre : new ArrayList<>(unmatchedPre))
				{
					if (pre.isRequiresManualMatch() || StringUtils.isBlank(pre.getNormalizedEmail()))
					{
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
							createMatch(matchDao, cohort, pre, post, MatchType.AUTO_EMAIL, BigDecimal.ONE);
							unmatchedPre.remove(pre);
							unmatchedPost.remove(post);
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
						createMatch(matchDao, cohort, pre, bestMatch, MatchType.AUTO_NAME, BigDecimal.valueOf(bestSimilarity));
						unmatchedPre.remove(pre);
						unmatchedPost.remove(bestMatch);
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
	 * Result of automatic matching.
	 */
	public record MatchResult(int emailMatches, int nameMatches)
	{
		public int totalMatches()
		{
			return emailMatches + nameMatches;
		}
	}
}
