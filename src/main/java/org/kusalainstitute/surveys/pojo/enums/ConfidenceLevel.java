package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Confidence level for English language skills (1-4 scale). Maps French survey responses to numeric
 * values.
 */
public enum ConfidenceLevel
{

	NOT_AT_ALL_CONFIDENT(1, "Pas du tout confiant(e)", "Not at all confident"), SOMEWHAT_CONFIDENT(2, "Un peu confiant(e)",
		"Somewhat confident"), CONFIDENT(3, "Confiant(e)", "Confident"), VERY_CONFIDENT(4, "Très confiant(e)", "Very confident");

	private final int value;
	private final String frenchLabel;
	private final String englishLabel;

	ConfidenceLevel(int value, String frenchLabel, String englishLabel)
	{
		this.value = value;
		this.frenchLabel = frenchLabel;
		this.englishLabel = englishLabel;
	}

	public int getValue()
	{
		return value;
	}

	public String getFrenchLabel()
	{
		return frenchLabel;
	}

	public String getEnglishLabel()
	{
		return englishLabel;
	}

	/**
	 * Parses a French response text to a ConfidenceLevel. Handles variations like "1 = Pas du tout
	 * confiant(e)" or just "Pas du tout confiant(e)".
	 *
	 * @param text
	 *            the French response text
	 * @return the corresponding ConfidenceLevel, or null if not found
	 */
	public static ConfidenceLevel fromFrench(String text)
	{
		if (StringUtils.isBlank(text))
		{
			return null;
		}

		String normalized = text.toLowerCase().trim();

		// Try to match by number prefix first (e.g., "1 = Pas du tout...")
		if (normalized.startsWith("1"))
		{
			return NOT_AT_ALL_CONFIDENT;
		}
		else if (normalized.startsWith("2"))
		{
			return SOMEWHAT_CONFIDENT;
		}
		else if (normalized.startsWith("3"))
		{
			return CONFIDENT;
		}
		else if (normalized.startsWith("4"))
		{
			return VERY_CONFIDENT;
		}

		// Try to match by French label content
		for (ConfidenceLevel level : values())
		{
			if (normalized.contains(level.frenchLabel.toLowerCase()))
			{
				return level;
			}
		}

		// Try partial matching
		if (normalized.contains("pas du tout"))
		{
			return NOT_AT_ALL_CONFIDENT;
		}
		else if (normalized.contains("un peu"))
		{
			return SOMEWHAT_CONFIDENT;
		}
		else if (normalized.contains("très"))
		{
			return VERY_CONFIDENT;
		}
		else if (normalized.contains("confiant"))
		{
			return CONFIDENT;
		}

		return null;
	}

	/**
	 * Parses an English response text to a ConfidenceLevel.
	 *
	 * @param text
	 *            the English response text
	 * @return the corresponding ConfidenceLevel, or null if not found
	 */
	public static ConfidenceLevel fromEnglish(String text)
	{
		if (StringUtils.isBlank(text))
		{
			return null;
		}

		String normalized = text.toLowerCase().trim();

		// Try to match by number prefix first
		if (normalized.startsWith("1"))
		{
			return NOT_AT_ALL_CONFIDENT;
		}
		else if (normalized.startsWith("2"))
		{
			return SOMEWHAT_CONFIDENT;
		}
		else if (normalized.startsWith("3"))
		{
			return CONFIDENT;
		}
		else if (normalized.startsWith("4"))
		{
			return VERY_CONFIDENT;
		}

		// Try to match by English label content
		if (normalized.contains("not at all"))
		{
			return NOT_AT_ALL_CONFIDENT;
		}
		else if (normalized.contains("somewhat"))
		{
			return SOMEWHAT_CONFIDENT;
		}
		else if (normalized.contains("very"))
		{
			return VERY_CONFIDENT;
		}
		else if (normalized.contains("confident"))
		{
			return CONFIDENT;
		}

		return null;
	}

	/**
	 * Gets the ConfidenceLevel from a numeric value.
	 *
	 * @param value
	 *            the numeric value (1-4)
	 * @return the corresponding ConfidenceLevel, or null if invalid
	 */
	public static ConfidenceLevel fromValue(int value)
	{
		for (ConfidenceLevel level : values())
		{
			if (level.value == value)
			{
				return level;
			}
		}
		return null;
	}
}
