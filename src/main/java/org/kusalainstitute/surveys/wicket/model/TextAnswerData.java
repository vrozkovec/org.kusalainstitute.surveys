package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * Holds data for a text answer column in the analysis table. Each text answer has a label, value,
 * and flag indicating whether it comes from the pre-survey (blue styling) or post-survey (green
 * styling).
 */
public record TextAnswerData(
	String label,
	String value,
	boolean isPreSurvey) implements Serializable
{

	/**
	 * Creates a TextAnswerData for a pre-survey text answer.
	 *
	 * @param label
	 *            the column header label
	 * @param value
	 *            the text answer value
	 * @return new TextAnswerData instance with isPreSurvey=true
	 */
	public static TextAnswerData pre(String label, String value)
	{
		return new TextAnswerData(label, value, true);
	}

	/**
	 * Creates a TextAnswerData for a post-survey text answer.
	 *
	 * @param label
	 *            the column header label
	 * @param value
	 *            the text answer value
	 * @return new TextAnswerData instance with isPreSurvey=false
	 */
	public static TextAnswerData post(String label, String value)
	{
		return new TextAnswerData(label, value, false);
	}

	/**
	 * Returns the CSS class for styling based on survey type.
	 *
	 * @return "text-cell-pre" for pre-survey, "text-cell-post" for post-survey
	 */
	public String getCssClass()
	{
		return isPreSurvey ? "text-cell-pre" : "text-cell-post";
	}

	/**
	 * Returns the formatted value for display.
	 *
	 * @return the value or empty string if null/blank
	 */
	public String getDisplayValue()
	{
		return StringUtils.defaultIfBlank(value, "");
	}
}
