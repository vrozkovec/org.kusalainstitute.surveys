package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;

/**
 * Complete data model for the situation analysis table. Contains all student rows and calculated
 * averages.
 */
public class SituationAnalysisModel implements Serializable
{

	/**
	 * Names of all 11 situations in display order.
	 */
	public static final List<String> SITUATION_NAMES = List.of("Directions", "Healthcare", "Authorities", "Job Interview",
		"Informal", "Children Education", "Landlord", "Social Events", "Local Services", "Support Orgs", "Shopping");

	private final List<StudentRow> rows;
	private final List<BigDecimal> speakingAverages;
	private final List<BigDecimal> understandingAverages;
	private final BigDecimal totalSpeakingChange;
	private final BigDecimal totalUnderstandingChange;

	/**
	 * Creates a new SituationAnalysisModel.
	 *
	 * @param rows
	 *            student rows
	 * @param speakingAverages
	 *            average changes for speaking situations (11 values)
	 * @param understandingAverages
	 *            average changes for understanding situations (11 values)
	 * @param totalSpeakingChange
	 *            overall average change for all speaking situations
	 * @param totalUnderstandingChange
	 *            overall average change for all understanding situations
	 */
	public SituationAnalysisModel(List<StudentRow> rows, List<BigDecimal> speakingAverages,
		List<BigDecimal> understandingAverages, BigDecimal totalSpeakingChange, BigDecimal totalUnderstandingChange)
	{
		this.rows = rows;
		this.speakingAverages = speakingAverages;
		this.understandingAverages = understandingAverages;
		this.totalSpeakingChange = totalSpeakingChange;
		this.totalUnderstandingChange = totalUnderstandingChange;
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

		// Accumulators for averages (11 situations each)
		List<List<BigDecimal>> speakingDeltas = new ArrayList<>();
		List<List<BigDecimal>> understandingDeltas = new ArrayList<>();
		for (int i = 0; i < 11; i++)
		{
			speakingDeltas.add(new ArrayList<>());
			understandingDeltas.add(new ArrayList<>());
		}

		for (MatchedPairData pair : matchedPairs)
		{
			List<SituationData> speakingData = buildSpeakingData(pair.pre(), pair.post());
			List<SituationData> understandingData = buildUnderstandingData(pair.pre(), pair.post());

			// Calculate individual totals for this student
			BigDecimal studentSpeakingTotal = calculateStudentAverage(speakingData);
			BigDecimal studentUnderstandingTotal = calculateStudentAverage(understandingData);

			rows.add(new StudentRow(pair.name(), pair.personId(), pair.cohort(), studentSpeakingTotal,
				studentUnderstandingTotal, speakingData, understandingData));

			// Collect deltas for averages
			for (int i = 0; i < 11; i++)
			{
				if (speakingData.get(i).delta() != null)
				{
					speakingDeltas.get(i).add(speakingData.get(i).delta());
				}
				if (understandingData.get(i).delta() != null)
				{
					understandingDeltas.get(i).add(understandingData.get(i).delta());
				}
			}
		}

		List<BigDecimal> speakingAverages = calculateAverages(speakingDeltas);
		List<BigDecimal> understandingAverages = calculateAverages(understandingDeltas);

		// Calculate total changes across all situations
		BigDecimal totalSpeakingChange = calculateTotalAverage(speakingDeltas);
		BigDecimal totalUnderstandingChange = calculateTotalAverage(understandingDeltas);

		return new SituationAnalysisModel(rows, speakingAverages, understandingAverages, totalSpeakingChange,
			totalUnderstandingChange);
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
	 * Builds understanding situation data (Pre Q9 understanding confidence vs Post Q7 difficulty
	 * expressing). Note: Post Q7 difficulty is inverted (higher difficulty = lower ease).
	 */
	private static List<SituationData> buildUnderstandingData(PreSurveyResponse pre, PostSurveyResponse post)
	{
		return List.of(SituationData.of(pre.getUnderstandDirections(), invertDifficulty(post.getDifficultyDirections())),
			SituationData.of(pre.getUnderstandHealthcare(), invertDifficulty(post.getDifficultyHealthcare())),
			SituationData.of(pre.getUnderstandAuthorities(), invertDifficulty(post.getDifficultyAuthorities())),
			SituationData.of(pre.getUnderstandJobInterview(), invertDifficulty(post.getDifficultyJobInterview())),
			SituationData.of(pre.getUnderstandInformal(), invertDifficulty(post.getDifficultyInformal())),
			SituationData.of(pre.getUnderstandChildrenEducation(), invertDifficulty(post.getDifficultyChildrenEducation())),
			SituationData.of(pre.getUnderstandLandlord(), invertDifficulty(post.getDifficultyLandlord())),
			SituationData.of(pre.getUnderstandSocialEvents(), invertDifficulty(post.getDifficultySocialEvents())),
			SituationData.of(pre.getUnderstandLocalServices(), invertDifficulty(post.getDifficultyLocalServices())),
			SituationData.of(pre.getUnderstandSupportOrgs(), invertDifficulty(post.getDifficultySupportOrgs())),
			SituationData.of(pre.getUnderstandShopping(), invertDifficulty(post.getDifficultyShopping())));
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
	 * Returns the total average change for all understanding situations.
	 *
	 * @return total understanding change
	 */
	public BigDecimal getTotalUnderstandingChange()
	{
		return totalUnderstandingChange;
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
