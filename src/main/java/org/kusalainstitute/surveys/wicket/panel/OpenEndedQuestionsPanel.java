package org.kusalainstitute.surveys.wicket.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.OpenEndedQuestionData;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;

/**
 * Wicket panel displaying all open-ended questions with their answers grouped by question. Shows a
 * qualitative summary with PRE/POST indicators and anonymous answers.
 */
public class OpenEndedQuestionsPanel extends GenericPanel<SituationAnalysisModel>
{

	/**
	 * Creates a new OpenEndedQuestionsPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public OpenEndedQuestionsPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		SituationAnalysisModel data = getModelObject();

		// Question list - outer ListView for questions
		add(new ListView<>("questions", data.getOpenEndedQuestions())
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
