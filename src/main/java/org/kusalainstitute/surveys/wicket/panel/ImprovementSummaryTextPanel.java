package org.kusalainstitute.surveys.wicket.panel;

import java.math.BigDecimal;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.CategoryCount;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.ImprovementSummary;

/**
 * Panel displaying improvement summary statistics in AI-friendly plain text format.
 * The output is formatted for easy copy-paste into AI systems for further analysis.
 */
public class ImprovementSummaryTextPanel extends GenericPanel<SituationAnalysisModel>
{

	/**
	 * Creates a new ImprovementSummaryTextPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public ImprovementSummaryTextPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		add(new Label("summaryText", this::buildSummaryText).setEscapeModelStrings(false));
	}

	/**
	 * Builds the AI-friendly text representation of improvement summary.
	 *
	 * @return formatted text summary
	 */
	private String buildSummaryText()
	{
		SituationAnalysisModel model = getModelObject();
		if (model == null)
		{
			return "No data available.";
		}

		StringBuilder sb = new StringBuilder();

		// Header
		sb.append("=== IMPROVEMENT SUMMARY ===\n\n");

		// Overall statistics
		ImprovementSummary summary = model.getImprovementSummary();
		sb.append("OVERALL STATISTICS\n");
		sb.append("-".repeat(40)).append("\n");
		sb.append(String.format("Total matched students: %d\n", summary.totalStudents()));
		sb.append(String.format("Average improvement: %s\n", formatDelta(summary.averageImprovement())));
		sb.append("\n");

		// Improvement/Same/Worse breakdown
		sb.append("OUTCOME BREAKDOWN\n");
		sb.append("-".repeat(40)).append("\n");
		sb.append(String.format("Improved (>+0.1): %d students (%d%%)\n", summary.improvedCount(), summary.improvedPercent()));
		sb.append(String.format("Same (-0.1 to +0.1): %d students (%d%%)\n", summary.sameCount(), summary.samePercent()));
		sb.append(String.format("Worse (<-0.1): %d students (%d%%)\n", summary.worseCount(), summary.worsePercent()));
		sb.append("\n");

		// Category breakdown
		List<CategoryCount> categories = model.getImprovementCategories();
		sb.append("DETAILED CATEGORY BREAKDOWN\n");
		sb.append("-".repeat(40)).append("\n");
		for (CategoryCount cc : categories)
		{
			sb.append(String.format("%-12s (%s): %d students (%d%%)\n",
				cc.category().getLabel(),
				cc.category().getDescription(),
				cc.count(),
				cc.percent()));
		}
		sb.append("\n");

		// Per-situation averages
		sb.append("SPEAKING IMPROVEMENT BY SITUATION\n");
		sb.append("-".repeat(40)).append("\n");
		List<String> situationNames = SituationAnalysisModel.SITUATION_NAMES;
		List<BigDecimal> speakingAverages = model.getSpeakingAverages();
		for (int i = 0; i < situationNames.size(); i++)
		{
			String avgStr = formatDelta(speakingAverages.get(i));
			sb.append(String.format("%-20s: %s\n", situationNames.get(i), avgStr));
		}
		sb.append("\n");
		sb.append(String.format("%-20s: %s\n", "OVERALL AVERAGE", formatDelta(model.getTotalSpeakingChange())));

		return sb.toString();
	}

	/**
	 * Formats a delta value with sign prefix.
	 *
	 * @param value
	 *            the delta value
	 * @return formatted string with +/- prefix
	 */
	private String formatDelta(BigDecimal value)
	{
		if (value == null)
		{
			return "N/A";
		}
		if (value.compareTo(BigDecimal.ZERO) > 0)
		{
			return "+" + value.toPlainString();
		}
		return value.toPlainString();
	}
}
