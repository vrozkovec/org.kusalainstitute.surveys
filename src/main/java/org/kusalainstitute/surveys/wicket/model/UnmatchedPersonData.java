package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;

/**
 * Data record for displaying an unmatched person in the match management panel.
 *
 * @param personId
 *            the person database ID
 * @param cohort
 *            the cohort code
 * @param name
 *            the person's name
 * @param email
 *            the person's email
 * @param surveyType
 *            PRE or POST survey type
 * @param requiresManual
 *            true if marked as requiring manual matching
 * @param timestamp
 *            the survey completion timestamp
 */
public record UnmatchedPersonData(
	Long personId,
	String cohort,
	String name,
	String email,
	SurveyType surveyType,
	boolean requiresManual,
	LocalDateTime timestamp) implements Serializable
{

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * Gets display text combining name and email for UI display.
	 *
	 * @return formatted display text
	 */
	public String getDisplayText()
	{
		var sb = new StringBuilder();
		if (StringUtils.isNotBlank(name))
		{
			sb.append(name);
		}
		if (StringUtils.isNotBlank(email))
		{
			if (!sb.isEmpty())
			{
				sb.append(" (").append(email).append(")");
			}
			else
			{
				sb.append(email);
			}
		}
		return sb.isEmpty() ? "[No name/email]" : sb.toString();
	}

	/**
	 * Gets the CSS class for the row based on state.
	 *
	 * @return the CSS class
	 */
	public String getRowCssClass()
	{
		if (requiresManual)
		{
			return "table-warning";
		}
		return "";
	}

	/**
	 * Gets the formatted survey timestamp.
	 *
	 * @return the formatted date, or empty string if null
	 */
	public String getFormattedTimestamp()
	{
		return timestamp != null ? timestamp.format(DATE_FORMAT) : "";
	}
}
