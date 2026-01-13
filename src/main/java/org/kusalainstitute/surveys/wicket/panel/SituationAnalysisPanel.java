package org.kusalainstitute.surveys.wicket.panel;

import java.math.BigDecimal;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.SingleValueData;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationData;
import org.kusalainstitute.surveys.wicket.model.StudentRow;
import org.kusalainstitute.surveys.wicket.model.TextAnswerData;

/**
 * Wicket panel displaying a situation-by-situation analysis table.
 * Shows three sections:
 * - Speaking: pre/post comparison with progress bars and delta
 * - Understanding: pre Q9 values only (single bar)
 * - Ease: post Q7 inverted values only (single bar)
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

		// Situation name headers - Ease group
		add(new ListView<>("easeHeaders", data.getSituationNames())
		{
			@Override
			protected void populateItem(ListItem<String> item)
			{
				item.add(new Label("situationName", item.getModelObject()));
			}
		});

		// Text answer column headers
		add(new ListView<>("textHeaders", data.getTextColumnLabels())
		{
			@Override
			protected void populateItem(ListItem<String> item)
			{
				item.add(new Label("headerLabel", item.getModelObject()));
				// Apply CSS class based on position (first 5 are pre, rest are post)
				String cssClass = item.getIndex() < 5 ? "header-text-pre" : "header-text-post";
				item.add(AttributeModifier.append("class", cssClass));
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

				// Individual total change for speaking
				Label speakingTotal = new Label("studentSpeakingTotal",
					SituationAnalysisModel.formatAverage(row.totalSpeakingChange()));
				speakingTotal
					.add(AttributeModifier.append("class", SituationAnalysisModel.getAverageCssClass(row.totalSpeakingChange())));
				item.add(speakingTotal);

				// Speaking situation cells (pre/post comparison)
				item.add(new ListView<>("speakingCells", row.speakingData())
				{
					@Override
					protected void populateItem(ListItem<SituationData> cellItem)
					{
						populateSituationCell(cellItem);
					}
				});

				// Understanding situation cells (single value)
				item.add(new ListView<>("understandingCells", row.understandingData())
				{
					@Override
					protected void populateItem(ListItem<SingleValueData> cellItem)
					{
						populateSingleValueCell(cellItem);
					}
				});

				// Ease situation cells (single value)
				item.add(new ListView<>("easeCells", row.easeData())
				{
					@Override
					protected void populateItem(ListItem<SingleValueData> cellItem)
					{
						populateSingleValueCell(cellItem);
					}
				});

				// Text answer cells
				item.add(new ListView<>("textCells", row.textAnswers())
				{
					@Override
					protected void populateItem(ListItem<TextAnswerData> cellItem)
					{
						populateTextCell(cellItem);
					}
				});
			}
		});

		// Footer averages - Speaking (deltas with sign)
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

		// Footer averages - Understanding (values without sign)
		add(new ListView<>("understandingAverages", data.getUnderstandingAverages())
		{
			@Override
			protected void populateItem(ListItem<BigDecimal> item)
			{
				BigDecimal avg = item.getModelObject();
				Label label = new Label("avgValue", SituationAnalysisModel.formatValue(avg));
				item.add(label);
			}
		});

		// Footer averages - Ease (values without sign)
		add(new ListView<>("easeAverages", data.getEaseAverages())
		{
			@Override
			protected void populateItem(ListItem<BigDecimal> item)
			{
				BigDecimal avg = item.getModelObject();
				Label label = new Label("avgValue", SituationAnalysisModel.formatValue(avg));
				item.add(label);
			}
		});

		// Total change label (speaking only)
		BigDecimal totalSpeaking = data.getTotalSpeakingChange();
		Label totalSpeakingLabel = new Label("totalSpeakingChange", SituationAnalysisModel.formatAverage(totalSpeaking));
		totalSpeakingLabel.add(AttributeModifier.append("class", SituationAnalysisModel.getAverageCssClass(totalSpeaking)));
		add(totalSpeakingLabel);
	}

	/**
	 * Populates a situation cell with pre/post bars and delta (for Speaking section).
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

	/**
	 * Populates a single-value cell with one bar (for Understanding/Ease sections).
	 *
	 * @param cellItem
	 *            the list item for the single-value cell
	 */
	private void populateSingleValueCell(ListItem<SingleValueData> cellItem)
	{
		SingleValueData data = cellItem.getModelObject();

		// Single bar
		WebMarkupContainer bar = new WebMarkupContainer("bar");
		bar.add(AttributeModifier.replace("style", "width:" + data.widthPercent() + "%"));
		cellItem.add(bar);

		// Value
		cellItem.add(new Label("val", data.getFormattedValue()));
	}

	/**
	 * Populates a text answer cell with styled background based on survey type.
	 *
	 * @param cellItem
	 *            the list item for the text answer cell
	 */
	private void populateTextCell(ListItem<TextAnswerData> cellItem)
	{
		TextAnswerData data = cellItem.getModelObject();

		// Text value
		cellItem.add(new Label("textValue", data.getDisplayValue()));

		// Apply CSS class for pre/post styling
		cellItem.add(AttributeModifier.append("class", data.getCssClass()));
	}
}
