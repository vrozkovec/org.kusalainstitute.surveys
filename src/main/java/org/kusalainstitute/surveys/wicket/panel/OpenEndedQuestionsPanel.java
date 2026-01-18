package org.kusalainstitute.surveys.wicket.panel;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.OpenEndedQuestionData;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;

import name.berries.wicket.bootstrap.form.BootstrapMultiSelect;

/**
 * Wicket panel displaying all open-ended questions with their answers grouped by question. Shows a
 * qualitative summary with PRE/POST indicators and anonymous answers.
 */
public class OpenEndedQuestionsPanel extends GenericPanel<SituationAnalysisModel>
{

	/** Model containing available cohorts for filtering. */
	private final IModel<List<String>> availableCohortsModel;

	/** Model containing selected cohorts for filtering. */
	private final IModel<List<String>> selectedCohortsModel;

	/** Container for AJAX refresh of panel content. */
	private WebMarkupContainer contentContainer;

	/**
	 * Creates a new OpenEndedQuestionsPanel.
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
	public OpenEndedQuestionsPanel(String id, IModel<SituationAnalysisModel> model,
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
		cohortSelect.add(OnChangeAjaxBehavior.onChange(target -> {
			getModel().detach(); // Force reload with new filter
			target.add(contentContainer);
		}));
		filterForm.add(cohortSelect);

		// Content container for AJAX refresh
		contentContainer = new WebMarkupContainer("contentContainer");
		contentContainer.setOutputMarkupId(true);
		add(contentContainer);

		var dataModel = getModel();

		// Question list - outer ListView for questions
		contentContainer.add(new ListView<>("questions",
			dataModel.map(SituationAnalysisModel::getOpenEndedQuestions))
		{
			@Override
			protected void populateItem(ListItem<OpenEndedQuestionData> item)
			{
				OpenEndedQuestionData questionData = item.getModelObject();

				// Add CSS class for pre/post survey styling
				String blockClass = questionData.isPreSurvey() ? "pre-survey" : "post-survey";
				item.add(AttributeModifier.append("class", blockClass));

				// Question number and PRE/POST badge
				item.add(new Label("questionNumber", questionData.questionNumber()));
				item.add(new Label("surveyType", questionData.getSurveyTypeLabel())
					.add(AttributeModifier.replace("class", "survey-badge " + questionData.getSurveyTypeCssClass())));

				// Full question text
				item.add(new Label("questionText", questionData.questionText()));

				// Answer count
				item.add(new Label("answerCount", String.valueOf(questionData.getAnswerCount())));

				// Answers list - inner ListView for answers
				item.add(new ListView<>("answers", questionData.answers())
				{
					@Override
					protected void populateItem(ListItem<String> answerItem)
					{
						answerItem.add(new Label("answerText", answerItem.getModelObject()));
					}
				});
			}
		});
	}
}
