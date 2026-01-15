package org.kusalainstitute.surveys.wicket.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.CategoryCount;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.ImprovementSummary;

/**
 * Panel displaying improvement summary statistics. Shows overview metrics (total students, improved,
 * same, worse) and a breakdown by improvement categories with visual bars.
 */
public class ImprovementSummaryPanel extends GenericPanel<SituationAnalysisModel>
{

	/**
	 * Creates a new ImprovementSummaryPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public ImprovementSummaryPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		SituationAnalysisModel data = getModelObject();
		ImprovementSummary summary = data.getImprovementSummary();

		// Summary statistics
		add(new Label("totalStudents", String.valueOf(summary.totalStudents())));
		add(new Label("improvedCount", String.valueOf(summary.improvedCount())));
		add(new Label("improvedPercent", summary.improvedPercent() + "%"));
		add(new Label("sameCount", String.valueOf(summary.sameCount())));
		add(new Label("samePercent", summary.samePercent() + "%"));
		add(new Label("worseCount", String.valueOf(summary.worseCount())));
		add(new Label("worsePercent", summary.worsePercent() + "%"));
		add(new Label("averageImprovement", SituationAnalysisModel.formatAverage(summary.averageImprovement())));

		// Category breakdown
		add(new ListView<>("categoryRows", data.getImprovementCategories())
		{
			@Override
			protected void populateItem(ListItem<CategoryCount> item)
			{
				CategoryCount cat = item.getModelObject();

				item.add(new Label("categoryName", cat.category().getLabel()));
				item.add(new Label("categoryDescription", cat.category().getDescription()));
				item.add(new Label("categoryCount", String.valueOf(cat.count())));
				item.add(new Label("categoryPercent", cat.percent() + "%"));

				// Progress bar
				WebMarkupContainer bar = new WebMarkupContainer("categoryBar");
				bar.add(AttributeModifier.replace("style", "width:" + cat.percent() + "%"));
				bar.add(AttributeModifier.append("class", "bg-" + cat.category().getCssClass()));
				item.add(bar);
			}
		});
	}
}
