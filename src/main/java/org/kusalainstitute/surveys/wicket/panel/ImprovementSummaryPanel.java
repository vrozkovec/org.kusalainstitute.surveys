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

		var dataModel = getModel();
		var summaryModel = dataModel.map(SituationAnalysisModel::getImprovementSummary);

		// Summary statistics - all using dynamic models
		add(new Label("totalStudents", summaryModel.map(s -> String.valueOf(s.totalStudents()))));
		add(new Label("improvedCount", summaryModel.map(s -> String.valueOf(s.improvedCount()))));
		add(new Label("improvedPercent", summaryModel.map(s -> s.improvedPercent() + "%")));
		add(new Label("sameCount", summaryModel.map(s -> String.valueOf(s.sameCount()))));
		add(new Label("samePercent", summaryModel.map(s -> s.samePercent() + "%")));
		add(new Label("worseCount", summaryModel.map(s -> String.valueOf(s.worseCount()))));
		add(new Label("worsePercent", summaryModel.map(s -> s.worsePercent() + "%")));
		add(new Label("averageImprovement",
			summaryModel.map(s -> SituationAnalysisModel.formatAverage(s.averageImprovement()))));

		// Category breakdown - using dynamic model
		add(new ListView<>("categoryRows", dataModel.map(SituationAnalysisModel::getImprovementCategories))
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
