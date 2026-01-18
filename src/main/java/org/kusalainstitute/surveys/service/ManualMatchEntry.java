package org.kusalainstitute.surveys.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

/**
 * Record for persisting manual match data to a properties file. Uses composite keys based on
 * cohort, timestamp, email, and name to uniquely identify survey responses, allowing recovery
 * after database rebuilds.
 *
 * @param preCohort
 *            the PRE survey cohort
 * @param preTimestamp
 *            the PRE survey completion timestamp
 * @param preEmail
 *            the PRE survey respondent email
 * @param preName
 *            the PRE survey respondent name
 * @param postCohort
 *            the POST survey cohort
 * @param postTimestamp
 *            the POST survey completion timestamp
 * @param postEmail
 *            the POST survey respondent email
 * @param postName
 *            the POST survey respondent name
 * @param notes
 *            optional notes about the match
 * @param createdBy
 *            username who created the match
 * @param createdAt
 *            when the match was created
 */
public record ManualMatchEntry(
	String preCohort,
	LocalDateTime preTimestamp,
	String preEmail,
	String preName,
	String postCohort,
	LocalDateTime postTimestamp,
	String postEmail,
	String postName,
	String notes,
	String createdBy,
	LocalDateTime createdAt)
{

	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final String KEY_SEPARATOR = "|";
	private static final String VALUE_SEPARATOR = "|";

	/**
	 * Gets the property key for this entry (PRE side composite key).
	 *
	 * @return the property key in format: cohort|timestamp|email|name
	 */
	public String getKey()
	{
		return String.join(KEY_SEPARATOR,
			escapeSeparator(preCohort),
			preTimestamp != null ? preTimestamp.format(TIMESTAMP_FORMAT) : "",
			escapeSeparator(preEmail),
			escapeSeparator(preName));
	}

	/**
	 * Gets the property value for this entry (POST side composite + metadata).
	 *
	 * @return the property value in format: cohort|timestamp|email|name|notes|createdBy|createdAt
	 */
	public String toPropertyValue()
	{
		return String.join(VALUE_SEPARATOR,
			escapeSeparator(postCohort),
			postTimestamp != null ? postTimestamp.format(TIMESTAMP_FORMAT) : "",
			escapeSeparator(postEmail),
			escapeSeparator(postName),
			escapeSeparator(notes),
			escapeSeparator(createdBy),
			createdAt != null ? createdAt.format(TIMESTAMP_FORMAT) : "");
	}

	/**
	 * Creates a ManualMatchEntry from a properties file key-value pair.
	 *
	 * @param key
	 *            the property key (PRE composite)
	 * @param value
	 *            the property value (POST composite + metadata)
	 * @return the parsed ManualMatchEntry, or null if parsing fails
	 */
	public static ManualMatchEntry fromProperty(String key, String value)
	{
		if (StringUtils.isBlank(key) || StringUtils.isBlank(value))
		{
			return null;
		}

		String[] keyParts = key.split("\\|", -1);
		String[] valueParts = value.split("\\|", -1);

		if (keyParts.length < 4 || valueParts.length < 7)
		{
			return null;
		}

		try
		{
			return new ManualMatchEntry(
				unescapeSeparator(keyParts[0]),
				parseTimestamp(keyParts[1]),
				unescapeSeparator(keyParts[2]),
				unescapeSeparator(keyParts[3]),
				unescapeSeparator(valueParts[0]),
				parseTimestamp(valueParts[1]),
				unescapeSeparator(valueParts[2]),
				unescapeSeparator(valueParts[3]),
				unescapeSeparator(valueParts[4]),
				unescapeSeparator(valueParts[5]),
				parseTimestamp(valueParts[6]));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Parses a timestamp string to LocalDateTime.
	 *
	 * @param timestamp
	 *            the timestamp string
	 * @return the parsed LocalDateTime, or null if blank or invalid
	 */
	private static LocalDateTime parseTimestamp(String timestamp)
	{
		if (StringUtils.isBlank(timestamp))
		{
			return null;
		}
		return LocalDateTime.parse(timestamp, TIMESTAMP_FORMAT);
	}

	/**
	 * Escapes the separator character in a value.
	 *
	 * @param value
	 *            the value to escape
	 * @return the escaped value
	 */
	private static String escapeSeparator(String value)
	{
		if (value == null)
		{
			return "";
		}
		// Replace | with a safe placeholder
		return value.replace("|", "{{PIPE}}");
	}

	/**
	 * Unescapes the separator character in a value.
	 *
	 * @param value
	 *            the value to unescape
	 * @return the unescaped value
	 */
	private static String unescapeSeparator(String value)
	{
		if (value == null || value.isEmpty())
		{
			return null;
		}
		return value.replace("{{PIPE}}", "|");
	}
}
