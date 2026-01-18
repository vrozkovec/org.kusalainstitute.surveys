package org.kusalainstitute.surveys.wicket.panel;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.RatingDistribution;

/**
 * Panel displaying rating distributions for all speaking situations in AI-friendly plain text format.
 * The output is formatted for easy copy-paste into AI systems for further analysis.
 */
public class RatingDistributionAllTextPanel extends GenericPanel<SituationAnalysisModel>
{

	private static final String[] RATING_LABELS = {
		"1 - Can't at all",
		"2 - Struggle",
		"3 - Okay",
		"4 - Good",
		"5 - Very good"
	};

	/**
	 * Creates a new RatingDistributionAllTextPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public RatingDistributionAllTextPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		// Use model-based label for proper AJAX refresh
		add(new Label("distributionText", getModel().map(this::buildDistributionText)).setEscapeModelStrings(false));
	}

	/**
	 * Builds the AI-friendly text representation of rating distributions.
	 *
	 * @param model
	 *            the analysis model
	 * @return formatted text with rating distributions
	 */
	private String buildDistributionText(SituationAnalysisModel model)
	{
		if (model == null)
		{
			return "No data available.";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("=== RATING DISTRIBUTION (PRE vs POST) ===\n\n");
		sb.append("Scale: 1=Can't at all, 2=Struggle, 3=Okay, 4=Good, 5=Very good\n");
		sb.append("Pre = Before (speaking confidence), Post = After (speaking ability)\n\n");

		List<RatingDistribution> distributions = model.getAllRatingDistributions();

		for (RatingDistribution dist : distributions)
		{
			sb.append("--- ").append(dist.situationName().toUpperCase()).append(" ---\n");
			sb.append(String.format("%-20s %6s %6s %8s\n", "Rating", "Pre", "Post", "Delta"));
			sb.append("-".repeat(44)).append("\n");

			for (int i = 0; i < 5; i++)
			{
				int preCount = dist.preCounts().get(i);
				int postCount = dist.postCounts().get(i);
				int delta = postCount - preCount;
				String deltaStr = delta > 0 ? "+" + delta : String.valueOf(delta);

				sb.append(String.format("%-20s %6d %6d %8s\n", RATING_LABELS[i], preCount, postCount, deltaStr));
			}

			// Calculate totals
			int preTotal = dist.preCounts().stream().mapToInt(Integer::intValue).sum();
			int postTotal = dist.postCounts().stream().mapToInt(Integer::intValue).sum();
			sb.append("-".repeat(44)).append("\n");
			sb.append(String.format("%-20s %6d %6d\n", "Total responses", preTotal, postTotal));

			// Calculate weighted averages
			double preAvg = calculateWeightedAverage(dist.preCounts());
			double postAvg = calculateWeightedAverage(dist.postCounts());
			double avgDelta = postAvg - preAvg;
			String avgDeltaStr = avgDelta > 0 ? "+" + String.format("%.2f", avgDelta) : String.format("%.2f", avgDelta);
			sb.append(String.format("%-20s %6.2f %6.2f %8s\n", "Weighted average", preAvg, postAvg, avgDeltaStr));

			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Calculates the weighted average rating from counts.
	 *
	 * @param counts
	 *            list of counts for ratings 1-5
	 * @return weighted average
	 */
	private double calculateWeightedAverage(List<Integer> counts)
	{
		int total = 0;
		int weightedSum = 0;
		for (int i = 0; i < counts.size(); i++)
		{
			int count = counts.get(i);
			total += count;
			weightedSum += count * (i + 1); // i+1 gives rating 1-5
		}
		return total > 0 ? (double)weightedSum / total : 0.0;
	}
}
