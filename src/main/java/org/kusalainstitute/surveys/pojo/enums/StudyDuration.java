package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Study duration options for PRE survey Q2 (with teacher) and Q3 (on own).
 * Uses letter codes A-F for language-independent storage.
 */
public enum StudyDuration
{

	A("Less than 3 months", "Moins de 3 mois"),
	B("4 months - 1 year", "Entre 4 mois et 1 an"),
	C("1-2 years", "1-2 ans"),
	D("2-5 years", "2-5 ans"),
	E("5-10 years", "5-10 ans"),
	F("10+ years", "10 ans ou plus");

	private final String englishLabel;
	private final String frenchLabel;

	StudyDuration(String englishLabel, String frenchLabel)
	{
		this.englishLabel = englishLabel;
		this.frenchLabel = frenchLabel;
	}

	/**
	 * Returns the English label for this duration.
	 *
	 * @return the English label
	 */
	public String getEnglishLabel()
	{
		return englishLabel;
	}

	/**
	 * Returns the French label for this duration.
	 *
	 * @return the French label
	 */
	public String getFrenchLabel()
	{
		return frenchLabel;
	}

	/**
	 * Parses a text value to a StudyDuration. Handles French and English variations.
	 *
	 * @param text
	 *            the response text
	 * @return the corresponding StudyDuration, or null if not found
	 */
	public static StudyDuration fromText(String text)
	{
		if (StringUtils.isBlank(text))
		{
			return null;
		}

		String normalized = text.toLowerCase().trim();

		// Try exact code match first (A, B, C, etc.)
		if (normalized.length() == 1)
		{
			try
			{
				return valueOf(normalized.toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				// Not a valid code
			}
		}

		// Try letter prefix (e.g., "A= Moins de 3 mois")
		if (normalized.length() >= 1 && Character.isLetter(normalized.charAt(0)))
		{
			char prefix = Character.toUpperCase(normalized.charAt(0));
			if (prefix >= 'A' && prefix <= 'F')
			{
				try
				{
					return valueOf(String.valueOf(prefix));
				}
				catch (IllegalArgumentException e)
				{
					// Continue to content matching
				}
			}
		}

		// French content matching
		if (normalized.contains("moins de 3") || normalized.contains("less than 3"))
		{
			return A;
		}
		if (normalized.contains("4 mois") || normalized.contains("4 months"))
		{
			return B;
		}
		if (normalized.contains("1-2"))
		{
			return C;
		}
		if (normalized.contains("2-5"))
		{
			return D;
		}
		if (normalized.contains("5-10"))
		{
			return E;
		}
		if (normalized.contains("10 ans") || normalized.contains("10+") || normalized.contains("10 years"))
		{
			return F;
		}

		return null;
	}

	/**
	 * Returns the enum code (A, B, C, etc.) for database storage.
	 *
	 * @return the code string
	 */
	public String getCode()
	{
		return name();
	}

	/**
	 * Gets a StudyDuration from its code.
	 *
	 * @param code
	 *            the code (A-F)
	 * @return the corresponding StudyDuration, or null if invalid
	 */
	public static StudyDuration fromCode(String code)
	{
		if (StringUtils.isBlank(code))
		{
			return null;
		}
		try
		{
			return valueOf(code.toUpperCase().trim());
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
	}
}
