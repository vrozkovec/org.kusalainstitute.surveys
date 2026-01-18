package org.kusalainstitute.surveys.wicket.panel;

import java.io.Serializable;
import java.util.List;

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

		var dataModel = getModel();

		TooltipConfig tooltipConfig = new TooltipConfig().withPlacement(TooltipConfig.Placement.top).withHtml(true);

		// PRE survey distributions (first 4) - using dynamic model
		add(new ListView<>("preDistributions",
			dataModel.map(d -> d.getEnumDistributions().subList(0, 4)))
		{
			@Override
			protected void populateItem(ListItem<EnumDistribution> item)
			{
				populateDistributionCard(item, tooltipConfig);
			}
		});

		// POST survey distributions (last 3) - using dynamic model
		add(new ListView<>("postDistributions",
			dataModel.map(d -> d.getEnumDistributions().subList(4, 7)))
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
		item.add(new ListView<>("distributionRows",
			List.copyOf(dist.valueCounts().entrySet()).stream().map(e -> new Entry(e.getKey(), e.getValue())).toList())
		{
			@Override
			protected void populateItem(ListItem<Entry> rowItem)
			{
				Entry entry = rowItem.getModelObject();
				String valueLabel = entry.key();
				int count = entry.value();
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

	record Entry(String key, Integer value) implements Serializable
	{
	}
}
