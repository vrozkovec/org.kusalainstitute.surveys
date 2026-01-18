package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.kusalainstitute.surveys.pojo.enums.MatchType;

/**
 * Data record for displaying a match row in the match management panel.
 *
 * @param matchId
 *            the match database ID
 * @param cohort
 *            the cohort code
 * @param matchType
 *            the type of match (AUTO_EMAIL, AUTO_NAME, MANUAL)
 * @param matchedBy
 *            who created the match
 * @param notes
 *            optional notes about the match
 * @param prePersonId
 *            the PRE survey person ID
 * @param preName
 *            the PRE survey respondent name
 * @param preEmail
 *            the PRE survey respondent email
 * @param preTimestamp
 *            the PRE survey completion timestamp
 * @param postPersonId
 *            the POST survey person ID
 * @param postName
 *            the POST survey respondent name
 * @param postEmail
 *            the POST survey respondent email
 * @param postTimestamp
 *            the POST survey completion timestamp
 */
public record MatchRowData(
	Long matchId,
	String cohort,
	MatchType matchType,
	String matchedBy,
	String notes,
	Long prePersonId,
	String preName,
	String preEmail,
	LocalDateTime preTimestamp,
	Long postPersonId,
	String postName,
	String postEmail,
	LocalDateTime postTimestamp) implements Serializable
{

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * Gets the CSS badge class for the match type.
	 *
	 * @return the Bootstrap badge class
	 */
	public String getMatchTypeBadgeClass()
	{
		return switch (matchType)
		{
			case AUTO_EMAIL -> "bg-success";
			case AUTO_NAME -> "bg-info";
			case MANUAL -> "bg-warning text-dark";
		};
	}

	/**
	 * Gets the display label for the match type.
	 *
	 * @return the display label
	 */
	public String getMatchTypeLabel()
	{
		return switch (matchType)
		{
			case AUTO_EMAIL -> "Email";
			case AUTO_NAME -> "Name";
			case MANUAL -> "Manual";
		};
	}

	/**
	 * Gets the formatted PRE survey timestamp.
	 *
	 * @return the formatted date, or empty string if null
	 */
	public String getFormattedPreTimestamp()
	{
		return preTimestamp != null ? preTimestamp.format(DATE_FORMAT) : "";
	}

	/**
	 * Gets the formatted POST survey timestamp.
	 *
	 * @return the formatted date, or empty string if null
	 */
	public String getFormattedPostTimestamp()
	{
		return postTimestamp != null ? postTimestamp.format(DATE_FORMAT) : "";
	}
}
