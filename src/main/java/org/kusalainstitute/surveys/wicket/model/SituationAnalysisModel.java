package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;

/**
 * Complete data model for the situation analysis table. Contains all student rows and calculated
 * averages for speaking (pre/post comparison), understanding (pre Q9), and ease (post Q7 inverted).
 */
public class SituationAnalysisModel implements Serializable
{

	/**
	 * Names of all 11 situations in display order.
	 */
	public static final List<String> SITUATION_NAMES = List.of("Directions", "Healthcare", "Authorities", "Job Interview",
		"Informal", "Children Education", "Landlord", "Social Events", "Local Services", "Support Orgs", "Shopping");

	/**
	 * Full descriptions of all 11 situations (translated from French survey).
	 */
	public static final List<String> SITUATION_DESCRIPTIONS = List.of(
		"Asking for or giving directions in the street",
		"Talking to healthcare workers (doctor, nurse, pharmacist)",
		"Communicating with government agencies (police, immigration, public services)",
		"Having a job interview in English",
		"Informal conversations with friends, family, or colleagues",
		"Communicating with children's school staff (teachers, administration)",
		"Communicating with landlord or property management",
		"Participating in social events or community gatherings",
		"Communicating with local service providers (utilities, banks, shops)",
		"Communicating with support organizations (NGOs, charities, community groups)",
		"Shopping, reading labels, seeking product information");

	/**
	 * Question text for Pre-Survey Q7 (Speaking Confidence).
	 */
	public static final String Q7_PRE_TEXT = "Please rate your confidence level for SPEAKING English in: ";

	/**
	 * Question text for Pre-Survey Q9 (Understanding Confidence).
	 */
	public static final String Q9_PRE_TEXT = "Please rate your confidence level for UNDERSTANDING English in: ";

	/**
	 * Question text for Post-Survey Q6 (Speaking Ability).
	 */
	public static final String Q6_POST_TEXT = "Please rate your ability to SPEAK English in: ";

	/**
	 * Question text for Post-Survey Q7 (Difficulty Expressing).
	 */
	public static final String Q7_POST_TEXT = "Please rate the difficulty of EXPRESSING yourself in: ";

	/**
	 * Header information for table column headers, including label, question number, and tooltip.
	 *
	 * @param label
	 *            the short situation name displayed in the header
	 * @param questionNumber
	 *            the question number (e.g., "Q7→Q6.1", "Q9.1")
	 * @param tooltip
	 *            the full question text for the tooltip
	 */
	public record HeaderInfo(String label, String questionNumber, String tooltip) implements Serializable
	{
	}

	/**
	 * Labels for text answer columns (5 pre-survey + 8 post-survey = 13 total).
	 */
	public static final List<String> TEXT_COLUMN_LABELS = List.of(
		// Pre-survey (5)
		"Most Difficult Thing (Pre)",
		"Why Improve English (Pre)",
		"Other Situations (Pre)",
		"Difficult Part (Pre)",
		"Describe Situations (Pre)",
		// Post-survey (8)
		"What Helped Most (Post)",
		"Most Difficult Overall (Post)",
		"Most Difficult For Job (Post)",
		"Emotional Difficulties (Post)",
		"Avoided Situations (Post)",
		"Desired Resources (Post)",
		"Interview Decline Reason (Post)",
		"Additional Comments (Post)");

	/**
	 * Full question texts for text answer columns (translated from French survey). These are displayed
	 * as tooltips on the column headers.
	 */
	public static final List<String> TEXT_COLUMN_FULL_QUESTIONS = List.of(
		// Pre-survey (5)
		"Q5: What is the most difficult thing about using English for you?",
		"Q6: Why do you want to improve your English?",
		"Q8: In what other situations would you like to speak English better?",
		"Q10: What is the most difficult part about not being able to express yourself?",
		"Q11: Please describe all situations where you could not speak well enough",
		// Post-survey (8)
		"Q5: What helped you the most during the program?",
		"Q8: What was the most difficult thing overall?",
		"Q9: What was the most difficult for finding a job?",
		"Q10: Did you experience any emotional difficulties?",
		"Q11: Did you avoid any situations because of English?",
		"Q13: What additional resources would you like?",
		"Q15: Why did you decline the interview?",
		"Q17: Any additional comments?");

	private final List<StudentRow> rows;
	private final List<BigDecimal> speakingAverages;
	private final List<BigDecimal> understandingAverages;
	private final List<BigDecimal> easeAverages;
	private final BigDecimal totalSpeakingChange;

	/**
	 * Creates a new SituationAnalysisModel.
	 *
	 * @param rows
	 *            student rows
	 * @param speakingAverages
	 *            average changes for speaking situations (11 values)
	 * @param understandingAverages
	 *            average values for understanding situations (11 values, pre Q9 only)
	 * @param easeAverages
	 *            average values for ease situations (11 values, post Q7 inverted)
	 * @param totalSpeakingChange
	 *            overall average change for all speaking situations
	 */
	public SituationAnalysisModel(List<StudentRow> rows, List<BigDecimal> speakingAverages,
		List<BigDecimal> understandingAverages, List<BigDecimal> easeAverages, BigDecimal totalSpeakingChange)
	{
		this.rows = rows;
		this.speakingAverages = speakingAverages;
		this.understandingAverages = understandingAverages;
		this.easeAverages = easeAverages;
		this.totalSpeakingChange = totalSpeakingChange;
	}

	/**
	 * Builds a SituationAnalysisModel from matched pre/post survey data.
	 *
	 * @param matchedPairs
	 *            list of matched pairs (name, personId, pre, post)
	 * @return populated model
	 */
	public static SituationAnalysisModel buildFromMatchedPairs(List<MatchedPairData> matchedPairs)
	{
		List<StudentRow> rows = new ArrayList<>();

		// Accumulators for speaking deltas (11 situations)
		List<List<BigDecimal>> speakingDeltas = new ArrayList<>();
		for (int i = 0; i < 11; i++)
		{
			speakingDeltas.add(new ArrayList<>());
		}

		// Accumulators for understanding values (pre Q9 only)
		List<List<Integer>> understandingValues = new ArrayList<>();
		for (int i = 0; i < 11; i++)
		{
			understandingValues.add(new ArrayList<>());
		}

		// Accumulators for ease values (post Q7 inverted)
		List<List<Integer>> easeValues = new ArrayList<>();
		for (int i = 0; i < 11; i++)
		{
			easeValues.add(new ArrayList<>());
		}

		for (MatchedPairData pair : matchedPairs)
		{
			List<SituationData> speakingData = buildSpeakingData(pair.pre(), pair.post());
			List<SingleValueData> understandingData = buildUnderstandingData(pair.pre());
			List<SingleValueData> easeData = buildEaseData(pair.post());
			List<TextAnswerData> textAnswers = buildTextAnswers(pair.pre(), pair.post());

			// Calculate individual total for speaking
			BigDecimal studentSpeakingTotal = calculateStudentAverage(speakingData);

			rows.add(new StudentRow(pair.name(), pair.personId(), pair.cohort(), studentSpeakingTotal,
				speakingData, understandingData, easeData, textAnswers));

			// Collect deltas for speaking averages
			for (int i = 0; i < 11; i++)
			{
				if (speakingData.get(i).delta() != null)
				{
					speakingDeltas.get(i).add(speakingData.get(i).delta());
				}
			}

			// Collect values for understanding averages
			for (int i = 0; i < 11; i++)
			{
				if (understandingData.get(i).value() != null)
				{
					understandingValues.get(i).add(understandingData.get(i).value());
				}
			}

			// Collect values for ease averages
			for (int i = 0; i < 11; i++)
			{
				if (easeData.get(i).value() != null)
				{
					easeValues.get(i).add(easeData.get(i).value());
				}
			}
		}

		List<BigDecimal> speakingAverages = calculateAverages(speakingDeltas);
		List<BigDecimal> understandingAverages = calculateValueAverages(understandingValues);
		List<BigDecimal> easeAverages = calculateValueAverages(easeValues);

		// Calculate total change for speaking only
		BigDecimal totalSpeakingChange = calculateTotalAverage(speakingDeltas);

		return new SituationAnalysisModel(rows, speakingAverages, understandingAverages, easeAverages, totalSpeakingChange);
	}

	/**
	 * Builds speaking situation data (Pre Q7 speaking confidence vs Post Q6 speaking ability).
	 */
	private static List<SituationData> buildSpeakingData(PreSurveyResponse pre, PostSurveyResponse post)
	{
		return List.of(SituationData.of(pre.getSpeakDirections(), post.getSpeakDirections()),
			SituationData.of(pre.getSpeakHealthcare(), post.getSpeakHealthcare()),
			SituationData.of(pre.getSpeakAuthorities(), post.getSpeakAuthorities()),
			SituationData.of(pre.getSpeakJobInterview(), post.getSpeakJobInterview()),
			SituationData.of(pre.getSpeakInformal(), post.getSpeakInformal()),
			SituationData.of(pre.getSpeakChildrenEducation(), post.getSpeakChildrenEducation()),
			SituationData.of(pre.getSpeakLandlord(), post.getSpeakLandlord()),
			SituationData.of(pre.getSpeakSocialEvents(), post.getSpeakSocialEvents()),
			SituationData.of(pre.getSpeakLocalServices(), post.getSpeakLocalServices()),
			SituationData.of(pre.getSpeakSupportOrgs(), post.getSpeakSupportOrgs()),
			SituationData.of(pre.getSpeakShopping(), post.getSpeakShopping()));
	}

	/**
	 * Builds understanding situation data from Pre Q9 (understanding confidence).
	 * These are standalone values, not compared with post-survey.
	 *
	 * @param pre
	 *            the pre-survey response
	 * @return list of 11 SingleValueData for understanding situations
	 */
	private static List<SingleValueData> buildUnderstandingData(PreSurveyResponse pre)
	{
		return List.of(
			SingleValueData.of(pre.getUnderstandDirections()),
			SingleValueData.of(pre.getUnderstandHealthcare()),
			SingleValueData.of(pre.getUnderstandAuthorities()),
			SingleValueData.of(pre.getUnderstandJobInterview()),
			SingleValueData.of(pre.getUnderstandInformal()),
			SingleValueData.of(pre.getUnderstandChildrenEducation()),
			SingleValueData.of(pre.getUnderstandLandlord()),
			SingleValueData.of(pre.getUnderstandSocialEvents()),
			SingleValueData.of(pre.getUnderstandLocalServices()),
			SingleValueData.of(pre.getUnderstandSupportOrgs()),
			SingleValueData.of(pre.getUnderstandShopping()));
	}

	/**
	 * Builds ease situation data from Post Q7 (difficulty expressing), inverted.
	 * These are standalone values, not compared with pre-survey.
	 * Difficulty is inverted so that higher values indicate more ease.
	 *
	 * @param post
	 *            the post-survey response
	 * @return list of 11 SingleValueData for ease situations
	 */
	private static List<SingleValueData> buildEaseData(PostSurveyResponse post)
	{
		return List.of(
			SingleValueData.of(invertDifficulty(post.getDifficultyDirections())),
			SingleValueData.of(invertDifficulty(post.getDifficultyHealthcare())),
			SingleValueData.of(invertDifficulty(post.getDifficultyAuthorities())),
			SingleValueData.of(invertDifficulty(post.getDifficultyJobInterview())),
			SingleValueData.of(invertDifficulty(post.getDifficultyInformal())),
			SingleValueData.of(invertDifficulty(post.getDifficultyChildrenEducation())),
			SingleValueData.of(invertDifficulty(post.getDifficultyLandlord())),
			SingleValueData.of(invertDifficulty(post.getDifficultySocialEvents())),
			SingleValueData.of(invertDifficulty(post.getDifficultyLocalServices())),
			SingleValueData.of(invertDifficulty(post.getDifficultySupportOrgs())),
			SingleValueData.of(invertDifficulty(post.getDifficultyShopping())));
	}

	/**
	 * Inverts a difficulty value to ease value on a 1-5 scale. Difficulty 1 (easy) becomes Ease 5,
	 * Difficulty 5 (hard) becomes Ease 1.
	 *
	 * @param difficulty
	 *            the difficulty value (1-5), can be null
	 * @return inverted ease value, or null if input is null
	 */
	private static Integer invertDifficulty(Integer difficulty)
	{
		if (difficulty == null)
		{
			return null;
		}
		return 6 - difficulty;
	}

	/**
	 * Builds text answer data from pre and post survey responses. Returns a list of 13
	 * TextAnswerData objects (5 pre-survey + 8 post-survey) in the same order as TEXT_COLUMN_LABELS.
	 *
	 * @param pre
	 *            the pre-survey response
	 * @param post
	 *            the post-survey response
	 * @return list of 13 TextAnswerData objects
	 */
	private static List<TextAnswerData> buildTextAnswers(PreSurveyResponse pre, PostSurveyResponse post)
	{
		return List.of(
			// Pre-survey (5)
			TextAnswerData.pre("Most Difficult Thing", pre.getMostDifficultThingTranslated()),
			TextAnswerData.pre("Why Improve English", pre.getWhyImproveEnglishTranslated()),
			TextAnswerData.pre("Other Situations", pre.getOtherSituationsTranslated()),
			TextAnswerData.pre("Difficult Part", pre.getDifficultPartTranslated()),
			TextAnswerData.pre("Describe Situations", pre.getDescribeSituationsTranslated()),
			// Post-survey (8)
			TextAnswerData.post("What Helped Most", post.getWhatHelpedMostTranslated()),
			TextAnswerData.post("Most Difficult Overall", post.getMostDifficultOverallTranslated()),
			TextAnswerData.post("Most Difficult For Job", post.getMostDifficultForJobTranslated()),
			TextAnswerData.post("Emotional Difficulties", post.getEmotionalDifficultiesTranslated()),
			TextAnswerData.post("Avoided Situations", post.getAvoidedSituationsTranslated()),
			TextAnswerData.post("Desired Resources", post.getDesiredResourcesTranslated()),
			TextAnswerData.post("Interview Decline Reason", post.getInterviewDeclineReasonTranslated()),
			TextAnswerData.post("Additional Comments", post.getAdditionalCommentsTranslated()));
	}

	/**
	 * Calculates averages for each situation from collected deltas.
	 */
	private static List<BigDecimal> calculateAverages(List<List<BigDecimal>> deltaLists)
	{
		List<BigDecimal> averages = new ArrayList<>();
		for (List<BigDecimal> deltas : deltaLists)
		{
			if (deltas.isEmpty())
			{
				averages.add(null);
			}
			else
			{
				BigDecimal sum = deltas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
				averages.add(sum.divide(BigDecimal.valueOf(deltas.size()), 1, RoundingMode.HALF_UP));
			}
		}
		return averages;
	}

	/**
	 * Calculates averages for each situation from collected integer values.
	 *
	 * @param valueLists
	 *            list of value lists for each situation
	 * @return list of averages
	 */
	private static List<BigDecimal> calculateValueAverages(List<List<Integer>> valueLists)
	{
		List<BigDecimal> averages = new ArrayList<>();
		for (List<Integer> values : valueLists)
		{
			if (values.isEmpty())
			{
				averages.add(null);
			}
			else
			{
				int sum = values.stream().mapToInt(Integer::intValue).sum();
				averages.add(BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(values.size()), 1, RoundingMode.HALF_UP));
			}
		}
		return averages;
	}

	/**
	 * Calculates the total average across all situations from collected deltas.
	 *
	 * @param deltaLists
	 *            list of delta lists for each situation
	 * @return overall average of all deltas, or null if no data
	 */
	private static BigDecimal calculateTotalAverage(List<List<BigDecimal>> deltaLists)
	{
		List<BigDecimal> allDeltas = new ArrayList<>();
		for (List<BigDecimal> deltas : deltaLists)
		{
			allDeltas.addAll(deltas);
		}

		if (allDeltas.isEmpty())
		{
			return null;
		}

		BigDecimal sum = allDeltas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		return sum.divide(BigDecimal.valueOf(allDeltas.size()), 2, RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the average delta for a single student across all situations.
	 *
	 * @param situationData
	 *            list of situation data for the student
	 * @return average delta, or null if no data
	 */
	private static BigDecimal calculateStudentAverage(List<SituationData> situationData)
	{
		List<BigDecimal> deltas = situationData.stream()
			.map(SituationData::delta)
			.filter(d -> d != null)
			.toList();

		if (deltas.isEmpty())
		{
			return null;
		}

		BigDecimal sum = deltas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		return sum.divide(BigDecimal.valueOf(deltas.size()), 2, RoundingMode.HALF_UP);
	}

	/**
	 * Returns the situation names for column headers.
	 *
	 * @return list of 11 situation names
	 */
	public List<String> getSituationNames()
	{
		return SITUATION_NAMES;
	}

	/**
	 * Returns the text column labels for column headers.
	 *
	 * @return list of 13 text column labels (5 pre + 8 post)
	 */
	public List<String> getTextColumnLabels()
	{
		return TEXT_COLUMN_LABELS;
	}

	/**
	 * Returns header information for Speaking columns. Speaking compares Pre Q7 (confidence) with
	 * Post Q6 (ability), so the question number shows "Q7→Q6.n". Tooltip shows only situation
	 * description since the common question text is shown in the group header.
	 *
	 * @return list of 11 HeaderInfo objects for speaking columns
	 */
	public List<HeaderInfo> getSpeakingHeaderInfos()
	{
		return IntStream.range(0, SITUATION_NAMES.size())
			.mapToObj(i -> new HeaderInfo(
				SITUATION_NAMES.get(i),
				"Q7→Q6." + (i + 1),
				SITUATION_DESCRIPTIONS.get(i)))
			.toList();
	}

	/**
	 * Returns header information for Understanding columns (Pre Q9 only). Tooltip shows only
	 * situation description since the common question text is shown in the group header.
	 *
	 * @return list of 11 HeaderInfo objects for understanding columns
	 */
	public List<HeaderInfo> getUnderstandingHeaderInfos()
	{
		return IntStream.range(0, SITUATION_NAMES.size())
			.mapToObj(i -> new HeaderInfo(
				SITUATION_NAMES.get(i),
				"Q9." + (i + 1),
				SITUATION_DESCRIPTIONS.get(i)))
			.toList();
	}

	/**
	 * Returns header information for Ease columns (Post Q7 inverted). Tooltip shows only situation
	 * description since the common question text is shown in the group header.
	 *
	 * @return list of 11 HeaderInfo objects for ease columns
	 */
	public List<HeaderInfo> getEaseHeaderInfos()
	{
		return IntStream.range(0, SITUATION_NAMES.size())
			.mapToObj(i -> new HeaderInfo(
				SITUATION_NAMES.get(i),
				"Q7." + (i + 1),
				SITUATION_DESCRIPTIONS.get(i)))
			.toList();
	}

	/**
	 * Returns header information for text answer columns. Each column has a short label and a full
	 * question text as tooltip.
	 *
	 * @return list of 13 HeaderInfo objects for text answer columns
	 */
	public List<HeaderInfo> getTextHeaderInfos()
	{
		return IntStream.range(0, TEXT_COLUMN_LABELS.size())
			.mapToObj(i -> new HeaderInfo(
				TEXT_COLUMN_LABELS.get(i),
				null, // no question number for text columns
				TEXT_COLUMN_FULL_QUESTIONS.get(i)))
			.toList();
	}

	public List<StudentRow> getRows()
	{
		return rows;
	}

	public List<BigDecimal> getSpeakingAverages()
	{
		return speakingAverages;
	}

	public List<BigDecimal> getUnderstandingAverages()
	{
		return understandingAverages;
	}

	public List<BigDecimal> getEaseAverages()
	{
		return easeAverages;
	}

	/**
	 * Returns the total average change for all speaking situations.
	 *
	 * @return total speaking change
	 */
	public BigDecimal getTotalSpeakingChange()
	{
		return totalSpeakingChange;
	}

	/**
	 * Returns formatted average change string with sign prefix.
	 *
	 * @param value
	 *            the average value
	 * @return formatted string like "+0.5", "-0.5", "0.0", or "--" if null
	 */
	public static String formatAverage(BigDecimal value)
	{
		if (value == null)
		{
			return "--";
		}
		if (value.compareTo(BigDecimal.ZERO) > 0)
		{
			return "+" + value.setScale(1, RoundingMode.HALF_UP).toPlainString();
		}
		return value.setScale(1, RoundingMode.HALF_UP).toPlainString();
	}

	/**
	 * Returns formatted value string (without sign prefix, for non-delta values).
	 *
	 * @param value
	 *            the value
	 * @return formatted string like "3.5", or "--" if null
	 */
	public static String formatValue(BigDecimal value)
	{
		if (value == null)
		{
			return "--";
		}
		return value.setScale(1, RoundingMode.HALF_UP).toPlainString();
	}

	/**
	 * Returns the CSS class for an average value.
	 *
	 * @param value
	 *            the average value
	 * @return "pos" for positive, "neg" for negative, "neu" for zero/null
	 */
	public static String getAverageCssClass(BigDecimal value)
	{
		if (value == null || value.compareTo(BigDecimal.ZERO) == 0)
		{
			return "neu";
		}
		return value.compareTo(BigDecimal.ZERO) > 0 ? "pos" : "neg";
	}

	/**
	 * Data transfer object for matched pair input.
	 */
	public record MatchedPairData(String name, Long personId, String cohort, PreSurveyResponse pre,
		PostSurveyResponse post) implements Serializable
	{
	}
}
