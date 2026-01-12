package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * App usage time per session options for POST survey Q2.
 * Uses letter codes A-E for language-independent storage.
 */
public enum AppTimePerSession
{

	A("Less than 15 minutes", "Moins de 15 minutes"),
	B("15-30 minutes", "15-30 minutes"),
	C("30 min - 1 hour", "Entre 30 minutes et une heure"),
	D("1-2 hours", "1-2 heures"),
	E("More than 2 hours", "Plus de deux heures");

	private final String englishLabel;
	private final String frenchLabel;

	AppTimePerSession(String englishLabel, String frenchLabel)
	{
		this.englishLabel = englishLabel;
		this.frenchLabel = frenchLabel;
	}

	/**
	 * Returns the English label for this time option.
	 *
	 * @return the English label
	 */
	public String getEnglishLabel()
	{
		return englishLabel;
	}

	/**
	 * Returns the French label for this time option.
	 *
	 * @return the French label
	 */
	public String getFrenchLabel()
	{
		return frenchLabel;
	}

	/**
	 * Parses a text value to an AppTimePerSession. Handles French and English variations.
	 *
	 * @param text
	 *            the response text
	 * @return the corresponding AppTimePerSession, or null if not found
	 */
	public static AppTimePerSession fromText(String text)
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

		// Try letter prefix (e.g., "A= Moins de 15 minutes")
		if (normalized.length() >= 1 && Character.isLetter(normalized.charAt(0)))
		{
			char prefix = Character.toUpperCase(normalized.charAt(0));
			if (prefix >= 'A' && prefix <= 'E')
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

		// Content matching
		if (normalized.contains("moins de 15") || normalized.contains("less than 15"))
		{
			return A;
		}
		if (normalized.contains("15-30") || normalized.contains("15 - 30"))
		{
			return B;
		}
		if (normalized.contains("30 minutes et une heure") || normalized.contains("30 min") && normalized.contains("1 hour"))
		{
			return C;
		}
		if (normalized.contains("1-2 heure") || normalized.contains("1-2 hour"))
		{
			return D;
		}
		if (normalized.contains("plus de deux") || normalized.contains("more than 2"))
		{
			return E;
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
	 * Gets an AppTimePerSession from its code.
	 *
	 * @param code
	 *            the code (A-E)
	 * @return the corresponding AppTimePerSession, or null if invalid
	 */
	public static AppTimePerSession fromCode(String code)
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
