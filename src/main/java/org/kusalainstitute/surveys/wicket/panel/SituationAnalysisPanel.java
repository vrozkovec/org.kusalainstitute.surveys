package org.kusalainstitute.surveys.wicket.panel;

import java.math.BigDecimal;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationData;
import org.kusalainstitute.surveys.wicket.model.StudentRow;

/**
 * Wicket panel displaying a situation-by-situation analysis table.
 * Shows pre/post survey comparisons with visual progress bars and change indicators.
 */
public class SituationAnalysisPanel extends GenericPanel<SituationAnalysisModel>
{

	/**
	 * Creates a new SituationAnalysisPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public SituationAnalysisPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		SituationAnalysisModel data = getModelObject();

		// Situation name headers - Speaking group
		add(new ListView<>("speakingHeaders", data.getSituationNames())
		{
			@Override
			protected void populateItem(ListItem<String> item)
			{
				item.add(new Label("situationName", item.getModelObject()));
			}
		});

		// Situation name headers - Understanding group
		add(new ListView<>("understandingHeaders", data.getSituationNames())
		{
			@Override
			protected void populateItem(ListItem<String> item)
			{
				item.add(new Label("situationName", item.getModelObject()));
			}
		});

		// Student data rows
		add(new ListView<>("studentRows", data.getRows())
		{
			@Override
			protected void populateItem(ListItem<StudentRow> item)
			{
				StudentRow row = item.getModelObject();

				// Student identity cell
				item.add(new Label("studentName", row.name()));
				item.add(new Label("studentId", String.valueOf(row.id())));
				item.add(new Label("studentCohort", row.cohort()));

				// Individual total changes
				Label speakingTotal = new Label("studentSpeakingTotal",
					SituationAnalysisModel.formatAverage(row.totalSpeakingChange()));
				speakingTotal
					.add(AttributeModifier.append("class", SituationAnalysisModel.getAverageCssClass(row.totalSpeakingChange())));
				item.add(speakingTotal);

				Label understandingTotal = new Label("studentUnderstandingTotal",
					SituationAnalysisModel.formatAverage(row.totalUnderstandingChange()));
				understandingTotal.add(
					AttributeModifier.append("class", SituationAnalysisModel.getAverageCssClass(row.totalUnderstandingChange())));
				item.add(understandingTotal);

				// Speaking situation cells
				item.add(new ListView<>("speakingCells", row.speakingData())
				{
					@Override
					protected void populateItem(ListItem<SituationData> cellItem)
					{
						populateSituationCell(cellItem);
					}
				});

				// Understanding situation cells
				item.add(new ListView<>("understandingCells", row.understandingData())
				{
					@Override
					protected void populateItem(ListItem<SituationData> cellItem)
					{
						populateSituationCell(cellItem);
					}
				});
			}
		});

		// Footer averages - Speaking
		add(new ListView<>("speakingAverages", data.getSpeakingAverages())
		{
			@Override
			protected void populateItem(ListItem<BigDecimal> item)
			{
				BigDecimal avg = item.getModelObject();
				Label label = new Label("avgValue", SituationAnalysisModel.formatAverage(avg));
				label.add(AttributeModifier.append("class", SituationAnalysisModel.getAverageCssClass(avg)));
				item.add(label);
			}
		});

		// Footer averages - Understanding
		add(new ListView<>("understandingAverages", data.getUnderstandingAverages())
		{
			@Override
			protected void populateItem(ListItem<BigDecimal> item)
			{
				BigDecimal avg = item.getModelObject();
				Label label = new Label("avgValue", SituationAnalysisModel.formatAverage(avg));
				label.add(AttributeModifier.append("class", SituationAnalysisModel.getAverageCssClass(avg)));
				item.add(label);
			}
		});

		// Total change labels
		BigDecimal totalSpeaking = data.getTotalSpeakingChange();
		Label totalSpeakingLabel = new Label("totalSpeakingChange", SituationAnalysisModel.formatAverage(totalSpeaking));
		totalSpeakingLabel.add(AttributeModifier.append("class", SituationAnalysisModel.getAverageCssClass(totalSpeaking)));
		add(totalSpeakingLabel);

		BigDecimal totalUnderstanding = data.getTotalUnderstandingChange();
		Label totalUnderstandingLabel = new Label("totalUnderstandingChange",
			SituationAnalysisModel.formatAverage(totalUnderstanding));
		totalUnderstandingLabel
			.add(AttributeModifier.append("class", SituationAnalysisModel.getAverageCssClass(totalUnderstanding)));
		add(totalUnderstandingLabel);
	}

	/**
	 * Populates a single situation cell with pre/post bars and delta.
	 *
	 * @param cellItem
	 *            the list item for the situation cell
	 */
	private void populateSituationCell(ListItem<SituationData> cellItem)
	{
		SituationData data = cellItem.getModelObject();

		// Pre bar
		WebMarkupContainer preBar = new WebMarkupContainer("preBar");
		preBar.add(AttributeModifier.replace("style", "width:" + data.preWidthPercent() + "%"));
		cellItem.add(preBar);

		// Pre value
		cellItem.add(new Label("preVal", data.preValue() != null ? String.valueOf(data.preValue()) : "-"));

		// Post bar
		WebMarkupContainer postBar = new WebMarkupContainer("postBar");
		postBar.add(AttributeModifier.replace("style", "width:" + data.postWidthPercent() + "%"));
		cellItem.add(postBar);

		// Post value
		cellItem.add(new Label("postVal", data.postValue() != null ? String.valueOf(data.postValue()) : "-"));

		// Delta
		Label delta = new Label("delta", data.getFormattedDelta());
		delta.add(AttributeModifier.append("class", data.getDeltaCssClass()));
		cellItem.add(delta);
	}
}
