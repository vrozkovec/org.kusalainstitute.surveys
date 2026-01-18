package org.kusalainstitute.surveys.wicket.panel;

import java.math.BigDecimal;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.kusalainstitute.surveys.wicket.model.EnumAnswerData;
import org.kusalainstitute.surveys.wicket.model.SingleValueData;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.HeaderInfo;
import org.kusalainstitute.surveys.wicket.model.SituationData;
import org.kusalainstitute.surveys.wicket.model.StudentRow;
import org.kusalainstitute.surveys.wicket.model.TextAnswerData;

import cz.newforms.wicket.markup.html.basic.container.AjaxContainer;
import cz.newforms.wicket.markup.html.panel.AjaxGenericPanel;

import name.berries.wicket.bootstrap.form.BootstrapMultiSelect;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;

/**
 * Wicket panel displaying a situation-by-situation analysis table. Shows three sections: -
 * Speaking: pre/post comparison with progress bars and delta - Understanding: pre Q9 values only
 * (single bar) - Ease: post Q7 inverted values only (single bar)
 */
public class SituationAnalysisPanel extends AjaxGenericPanel<SituationAnalysisModel>
{

	/** Model containing available cohorts for filtering. */
	private final IModel<List<String>> availableCohortsModel;

	/** Model containing selected cohorts for filtering. */
	private final IModel<List<String>> selectedCohortsModel;

	/** Container for AJAX refresh of panel content. */
	private AjaxContainer contentContainer;

	/**
	 * Creates a new SituationAnalysisPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 * @param availableCohortsModel
	 *            model containing available cohorts for filtering
	 * @param selectedCohortsModel
	 *            model containing selected cohorts for filtering
	 */
	public SituationAnalysisPanel(String id, IModel<SituationAnalysisModel> model,
		IModel<List<String>> availableCohortsModel, IModel<List<String>> selectedCohortsModel)
	{
		super(id, model);
		this.availableCohortsModel = availableCohortsModel;
		this.selectedCohortsModel = selectedCohortsModel;
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		// Add cohort filter form
		Form<Void> filterForm = new Form<>("filterForm");
		add(filterForm);

		BootstrapMultiSelect<String> cohortSelect = new BootstrapMultiSelect<>("cohortFilter", selectedCohortsModel,
			availableCohortsModel);
		cohortSelect.add(OnChangeAjaxBehavior.onChange(t -> {
			getModel().detach();
			t.add(contentContainer);
		}));
		filterForm.add(cohortSelect);

		// Content container for AJAX refresh
		contentContainer = new AjaxContainer("contentContainer");
		add(contentContainer);

		contentContainer.add(new ImprovementSummaryPanel("improvementSummary", getModel()));
		contentContainer.add(new RatingDistributionAllPanel("ratingDistribution", getModel()));
		contentContainer.add(new EnumDistributionSummaryPanel("enumDistribution", getModel()));

		// Tooltip configuration for headers
		TooltipConfig tooltipConfig = new TooltipConfig().withPlacement(TooltipConfig.Placement.bottom).withHtml(true);

		// Group header tooltips (colspan row)
		WebMarkupContainer speakingGroupHeader = new WebMarkupContainer("speakingGroupHeader");
		speakingGroupHeader
			.add(new TooltipBehavior(Model.of("Pre (Q7): " + SituationAnalysisModel.Q7_PRE_TEXT.replace(": ", "")
				+ "<br/>Post (Q6): " + SituationAnalysisModel.Q6_POST_TEXT.replace(": ", "")), tooltipConfig));
		contentContainer.add(speakingGroupHeader);

		WebMarkupContainer understandingGroupHeader = new WebMarkupContainer("understandingGroupHeader");
		understandingGroupHeader.add(
			new TooltipBehavior(Model.of("Q9: " + SituationAnalysisModel.Q9_PRE_TEXT.replace(": ", "")), tooltipConfig));
		contentContainer.add(understandingGroupHeader);

		WebMarkupContainer easeGroupHeader = new WebMarkupContainer("easeGroupHeader");
		easeGroupHeader
			.add(new TooltipBehavior(Model.of("Q7 (Post): " + SituationAnalysisModel.Q7_POST_TEXT.replace(": ", "")
				+ "<br/>(Values inverted: higher = easier)"), tooltipConfig));
		contentContainer.add(easeGroupHeader);

		WebMarkupContainer textPreGroupHeader = new WebMarkupContainer("textPreGroupHeader");
		textPreGroupHeader.add(new TooltipBehavior(Model.of("Free-text responses from the pre-survey"), tooltipConfig));
		contentContainer.add(textPreGroupHeader);

		WebMarkupContainer textPostGroupHeader = new WebMarkupContainer("textPostGroupHeader");
		textPostGroupHeader.add(new TooltipBehavior(Model.of("Free-text responses from the post-survey"), tooltipConfig));
		contentContainer.add(textPostGroupHeader);

		// Enum group headers
		WebMarkupContainer enumPreGroupHeader = new WebMarkupContainer("enumPreGroupHeader");
		enumPreGroupHeader
			.add(new TooltipBehavior(Model.of("Pre-survey demographics and background (Q1-Q4)"), tooltipConfig));
		contentContainer.add(enumPreGroupHeader);

		WebMarkupContainer enumPostGroupHeader = new WebMarkupContainer("enumPostGroupHeader");
		enumPostGroupHeader.add(new TooltipBehavior(Model.of("Post-survey app usage and progress (Q1-Q4)"), tooltipConfig));
		contentContainer.add(enumPostGroupHeader);

		var dataModel = getModel();

		// Situation name headers - Speaking group
		contentContainer
			.add(new ListView<>("speakingHeaders", dataModel.map(SituationAnalysisModel::getSpeakingHeaderInfos))
			{
				@Override
				protected void populateItem(ListItem<HeaderInfo> item)
				{
					HeaderInfo info = item.getModelObject();
					item.add(new Label("situationName", info.label()));
					item.add(new Label("questionNumber", info.questionNumber()));
					item.add(new TooltipBehavior(Model.of(info.tooltip().replace("\n", "<br/>")), tooltipConfig));
				}
			});

		// Situation name headers - Understanding group
		contentContainer
			.add(new ListView<>("understandingHeaders", dataModel.map(SituationAnalysisModel::getUnderstandingHeaderInfos))
			{
				@Override
				protected void populateItem(ListItem<HeaderInfo> item)
				{
					HeaderInfo info = item.getModelObject();
					item.add(new Label("situationName", info.label()));
					item.add(new Label("questionNumber", info.questionNumber()));
					item.add(new TooltipBehavior(Model.of(info.tooltip().replace("\n", "<br/>")), tooltipConfig));
				}
			});

		// Situation name headers - Ease group
		contentContainer.add(new ListView<>("easeHeaders", dataModel.map(SituationAnalysisModel::getEaseHeaderInfos))
		{
			@Override
			protected void populateItem(ListItem<HeaderInfo> item)
			{
				HeaderInfo info = item.getModelObject();
				item.add(new Label("situationName", info.label()));
				item.add(new Label("questionNumber", info.questionNumber()));
				item.add(new TooltipBehavior(Model.of(info.tooltip().replace("\n", "<br/>")), tooltipConfig));
			}
		});

		// Text answer column headers with tooltips
		contentContainer.add(new ListView<>("textHeaders", dataModel.map(SituationAnalysisModel::getTextHeaderInfos))
		{
			@Override
			protected void populateItem(ListItem<HeaderInfo> item)
			{
				HeaderInfo info = item.getModelObject();
				item.add(new Label("headerLabel", info.label()));
				item.add(new TooltipBehavior(Model.of(info.tooltip()), tooltipConfig));
				// Apply CSS class based on position (first 5 are pre, rest are post)
				String cssClass = item.getIndex() < 5 ? "header-text-pre" : "header-text-post";
				item.add(AttributeModifier.append("class", cssClass));
			}
		});

		// Enum answer column headers with tooltips
		contentContainer.add(new ListView<>("enumHeaders", dataModel.map(SituationAnalysisModel::getEnumHeaderInfos))
		{
			@Override
			protected void populateItem(ListItem<HeaderInfo> item)
			{
				HeaderInfo info = item.getModelObject();
				item.add(new Label("headerLabel", info.label()));
				item.add(new Label("questionNumber", info.questionNumber()));
				item.add(new TooltipBehavior(Model.of(info.tooltip()), tooltipConfig));
				// Apply CSS class based on position (first 4 are pre, rest are post)
				String cssClass = item.getIndex() < 4 ? "header-enum-pre" : "header-enum-post";
				item.add(AttributeModifier.append("class", cssClass));
			}
		});

		// Student data rows
		contentContainer.add(new ListView<>("studentRows", dataModel.map(SituationAnalysisModel::getRows))
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
				speakingTotal.add(AttributeModifier.append("class",
					SituationAnalysisModel.getAverageCssClass(row.totalSpeakingChange())));
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
						populateSingleValueCell(cellItem, "active-understanding");
					}
				});

				// Ease situation cells (single value)
				item.add(new ListView<>("easeCells", row.easeData())
				{
					@Override
					protected void populateItem(ListItem<SingleValueData> cellItem)
					{
						populateSingleValueCell(cellItem, "active-ease");
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

				// Enum answer cells
				item.add(new ListView<>("enumCells", row.enumAnswers())
				{
					@Override
					protected void populateItem(ListItem<EnumAnswerData> cellItem)
					{
						populateEnumCell(cellItem);
					}
				});
			}
		});

		// Footer averages - Speaking (deltas with sign)
		contentContainer.add(new ListView<>("speakingAverages", dataModel.map(SituationAnalysisModel::getSpeakingAverages))
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
		contentContainer
			.add(new ListView<>("understandingAverages", dataModel.map(SituationAnalysisModel::getUnderstandingAverages))
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
		contentContainer.add(new ListView<>("easeAverages", dataModel.map(SituationAnalysisModel::getEaseAverages))
		{
			@Override
			protected void populateItem(ListItem<BigDecimal> item)
			{
				BigDecimal avg = item.getModelObject();
				Label label = new Label("avgValue", SituationAnalysisModel.formatValue(avg));
				item.add(label);
			}
		});

		// Total change label (speaking only) - uses dynamic model
		contentContainer.add(new Label("totalSpeakingChange",
			dataModel.map(d -> SituationAnalysisModel.formatAverage(d.getTotalSpeakingChange())))
		{
			@Override
			protected void onConfigure()
			{
				super.onConfigure();
				BigDecimal totalSpeaking = dataModel.getObject().getTotalSpeakingChange();
				add(AttributeModifier.replace("class",
					"total-summary-value " + SituationAnalysisModel.getAverageCssClass(totalSpeaking)));
			}
		});
	}

	private static final int MAX_BRICKS = 5;

	/**
	 * Populates a situation cell with pre/post brick rows and delta (for Speaking section).
	 *
	 * @param cellItem
	 *            the list item for the situation cell
	 */
	private void populateSituationCell(ListItem<SituationData> cellItem)
	{
		SituationData data = cellItem.getModelObject();

		// Pre bricks
		cellItem.add(createBrickRow("preBricks", data.preValue(), "active-pre"));

		// Pre value
		cellItem.add(new Label("preVal", data.preValue() != null ? String.valueOf(data.preValue()) : "-"));

		// Post bricks
		cellItem.add(createBrickRow("postBricks", data.postValue(), "active-post"));

		// Post value
		cellItem.add(new Label("postVal", data.postValue() != null ? String.valueOf(data.postValue()) : "-"));

		// Delta
		Label delta = new Label("delta", data.getFormattedDelta());
		delta.add(AttributeModifier.append("class", data.getDeltaCssClass()));
		cellItem.add(delta);
	}

	/**
	 * Creates a RepeatingView containing 5 brick divs with active classes based on value.
	 *
	 * @param id
	 *            the wicket component id
	 * @param value
	 *            the rating value (1-5), can be null
	 * @param activeClass
	 *            the CSS class to add for active bricks
	 * @return RepeatingView with 5 brick containers
	 */
	private RepeatingView createBrickRow(String id, Integer value, String activeClass)
	{
		RepeatingView bricks = new RepeatingView(id);
		for (int i = 0; i < MAX_BRICKS; i++)
		{
			WebMarkupContainer brick = new WebMarkupContainer(bricks.newChildId());
			if (value != null && i < value)
			{
				brick.add(AttributeModifier.append("class", activeClass));
			}
			bricks.add(brick);
		}
		return bricks;
	}

	/**
	 * Populates a single-value cell with brick row (for Understanding/Ease sections).
	 *
	 * @param cellItem
	 *            the list item for the single-value cell
	 * @param activeClass
	 *            the CSS class to use for active bricks
	 */
	private void populateSingleValueCell(ListItem<SingleValueData> cellItem, String activeClass)
	{
		SingleValueData data = cellItem.getModelObject();

		// Bricks
		cellItem.add(createBrickRow("bricks", data.value(), activeClass));

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

	/**
	 * Populates an enum answer cell with styled background based on survey type.
	 *
	 * @param cellItem
	 *            the list item for the enum answer cell
	 */
	private void populateEnumCell(ListItem<EnumAnswerData> cellItem)
	{
		EnumAnswerData data = cellItem.getModelObject();

		// Enum value
		cellItem.add(new Label("enumValue", data.displayValue()));

		// Apply CSS class for pre/post styling
		cellItem.add(AttributeModifier.append("class", data.getCssClass()));
	}
}
