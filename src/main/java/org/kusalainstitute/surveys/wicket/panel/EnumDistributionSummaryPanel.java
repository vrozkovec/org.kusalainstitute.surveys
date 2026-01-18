package org.kusalainstitute.surveys.wicket.panel;

import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.EnumDistribution;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;

/**
 * Panel displaying distribution of responses for enum questions (PRE/POST Q1-Q4). Shows a 2-column
 * grid with 4 cards per section (PRE and POST), each card showing response counts and bar chart.
 */
public class EnumDistributionSummaryPanel extends GenericPanel<SituationAnalysisModel>
{

	/**
	 * Creates a new EnumDistributionSummaryPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public EnumDistributionSummaryPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		SituationAnalysisModel data = getModelObject();
		List<EnumDistribution> distributions = data.getEnumDistributions();

		TooltipConfig tooltipConfig = new TooltipConfig().withPlacement(TooltipConfig.Placement.top).withHtml(true);

		// PRE survey distributions (first 4)
		List<EnumDistribution> preDistributions = distributions.subList(0, 4);
		add(new ListView<>("preDistributions", preDistributions)
		{
			@Override
			protected void populateItem(ListItem<EnumDistribution> item)
			{
				populateDistributionCard(item, tooltipConfig);
			}
		});

		// POST survey distributions (last 3)
		List<EnumDistribution> postDistributions = distributions.subList(4, 7);
		add(new ListView<>("postDistributions", postDistributions)
		{
			@Override
			protected void populateItem(ListItem<EnumDistribution> item)
			{
				populateDistributionCard(item, tooltipConfig);
			}
		});
	}

	/**
	 * Populates a distribution card with label, count, and distribution table.
	 *
	 * @param item
	 *            the list item for the distribution card
	 * @param tooltipConfig
	 *            tooltip configuration
	 */
	private void populateDistributionCard(ListItem<EnumDistribution> item, TooltipConfig tooltipConfig)
	{
		EnumDistribution dist = item.getModelObject();

		// Card header with tooltip
		WebMarkupContainer cardHeader = new WebMarkupContainer("cardHeader");
		cardHeader.add(new Label("questionNumber", dist.questionNumber()));
		cardHeader.add(new Label("label", dist.label()));
		cardHeader.add(new TooltipBehavior(Model.of(dist.fullQuestion()), tooltipConfig));
		item.add(cardHeader);

		// Response count
		item.add(new Label("responseCount", dist.totalResponses() + " responses"));

		// Distribution rows
		int maxCount = dist.getMaxCount();
		item.add(new ListView<>("distributionRows", List.copyOf(dist.valueCounts().entrySet()))
		{
			@Override
			protected void populateItem(ListItem<Map.Entry<String, Integer>> rowItem)
			{
				Map.Entry<String, Integer> entry = rowItem.getModelObject();
				String valueLabel = entry.getKey();
				int count = entry.getValue();
				int percent = dist.getPercent(count);

				rowItem.add(new Label("valueLabel", valueLabel));
				rowItem.add(new Label("count", count));
				rowItem.add(new Label("percent", percent + "%"));

				// Bar width calculation (relative to max count in this distribution)
				int barWidth = maxCount > 0 ? (count * 100) / maxCount : 0;
				WebMarkupContainer bar = new WebMarkupContainer("bar");
				bar.add(AttributeModifier.replace("style", "width: " + barWidth + "%"));
				// Use different color for pre vs post
				String barClass = dist.isPreSurvey() ? "bar-pre" : "bar-post";
				bar.add(AttributeModifier.append("class", barClass));
				rowItem.add(bar);
			}
		});
	}
}
