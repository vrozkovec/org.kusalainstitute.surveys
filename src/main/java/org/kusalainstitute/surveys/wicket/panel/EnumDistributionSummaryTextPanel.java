package org.kusalainstitute.surveys.wicket.panel;

import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.EnumDistribution;

/**
 * Panel displaying enum question response distributions in AI-friendly plain text format.
 * The output is formatted for easy copy-paste into AI systems for further analysis.
 */
public class EnumDistributionSummaryTextPanel extends GenericPanel<SituationAnalysisModel>
{

	/**
	 * Creates a new EnumDistributionSummaryTextPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public EnumDistributionSummaryTextPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		add(new Label("enumText", this::buildEnumText).setEscapeModelStrings(false));
	}

	/**
	 * Builds the AI-friendly text representation of enum distributions.
	 *
	 * @return formatted text with enum distributions
	 */
	private String buildEnumText()
	{
		SituationAnalysisModel model = getModelObject();
		if (model == null)
		{
			return "No data available.";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("=== ENUM RESPONSE DISTRIBUTIONS ===\n\n");

		List<EnumDistribution> distributions = model.getEnumDistributions();

		// PRE-SURVEY section
		sb.append("PRE-SURVEY QUESTIONS\n");
		sb.append("=".repeat(60)).append("\n\n");

		for (EnumDistribution dist : distributions)
		{
			if (dist.isPreSurvey())
			{
				appendDistribution(sb, dist);
			}
		}

		// POST-SURVEY section
		sb.append("\nPOST-SURVEY QUESTIONS\n");
		sb.append("=".repeat(60)).append("\n\n");

		for (EnumDistribution dist : distributions)
		{
			if (!dist.isPreSurvey())
			{
				appendDistribution(sb, dist);
			}
		}

		return sb.toString();
	}

	/**
	 * Appends a single distribution to the string builder.
	 *
	 * @param sb
	 *            the string builder
	 * @param dist
	 *            the enum distribution
	 */
	private void appendDistribution(StringBuilder sb, EnumDistribution dist)
	{
		sb.append("--- ").append(dist.questionNumber()).append(": ").append(dist.label()).append(" ---\n");
		sb.append("Question: ").append(dist.fullQuestion()).append("\n");
		sb.append("Total responses: ").append(dist.totalResponses()).append("\n\n");

		sb.append(String.format("%-40s %6s %6s\n", "Value", "Count", "%"));
		sb.append("-".repeat(54)).append("\n");

		for (Map.Entry<String, Integer> entry : dist.valueCounts().entrySet())
		{
			String value = entry.getKey();
			int count = entry.getValue();
			int percent = dist.getPercent(count);

			// Truncate long values
			String displayValue = value.length() > 38 ? value.substring(0, 35) + "..." : value;

			sb.append(String.format("%-40s %6d %5d%%\n", displayValue, count, percent));
		}

		sb.append("\n");
	}
}
