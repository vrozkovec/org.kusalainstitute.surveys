package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;
import java.util.List;

/**
 * Holds data for an open-ended question with all its answers. Used by OpenEndedQuestionsPanel to
 * display qualitative survey responses grouped by question.
 *
 * @param questionNumber
 *            the question number (e.g., "Q5", "Q12")
 * @param questionText
 *            the full question text
 * @param isPreSurvey
 *            true if this is a pre-survey question, false for post-survey
 * @param answers
 *            list of all non-empty answers (anonymous, no student names)
 */
public record OpenEndedQuestionData(
	String questionNumber,
	String questionText,
	boolean isPreSurvey,
	List<String> answers) implements Serializable
{

	/**
	 * Returns the survey type label for display.
	 *
	 * @return "PRE" or "POST"
	 */
	public String getSurveyTypeLabel()
	{
		return isPreSurvey ? "PRE" : "POST";
	}

	/**
	 * Returns the CSS class for the survey type badge.
	 *
	 * @return "badge-pre" or "badge-post"
	 */
	public String getSurveyTypeCssClass()
	{
		return isPreSurvey ? "badge-pre" : "badge-post";
	}

	/**
	 * Returns the number of answers for this question.
	 *
	 * @return answer count
	 */
	public int getAnswerCount()
	{
		return answers != null ? answers.size() : 0;
	}
}
