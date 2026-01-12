package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Options for PRE survey Q1: How did you find out about Kusala Institute.
 * Uses letter codes A-D for language-independent storage.
 */
public enum HowFoundKusala
{

	A("LinkedIn", "LinkedIn"),
	B("Facebook", "Facebook"),
	C("A local friend", "Un ami local"),
	D("A friend in another city/country", "Un ami qui vit dans une autre ville ou un autre pays");

	private final String englishLabel;
	private final String frenchLabel;

	HowFoundKusala(String englishLabel, String frenchLabel)
	{
		this.englishLabel = englishLabel;
		this.frenchLabel = frenchLabel;
	}

	/**
	 * Returns the English label for this option.
	 *
	 * @return the English label
	 */
	public String getEnglishLabel()
	{
		return englishLabel;
	}

	/**
	 * Returns the French label for this option.
	 *
	 * @return the French label
	 */
	public String getFrenchLabel()
	{
		return frenchLabel;
	}

	/**
	 * Returns the code for database storage.
	 *
	 * @return the code string (A, B, C, or D)
	 */
	public String getCode()
	{
		return name();
	}

	/**
	 * Parses a text value to a HowFoundKusala. Handles French and English variations.
	 *
	 * @param text
	 *            the response text
	 * @return the corresponding HowFoundKusala, or null if not found
	 */
	public static HowFoundKusala fromText(String text)
	{
		if (StringUtils.isBlank(text))
		{
			return null;
		}

		String normalized = text.toLowerCase().trim();

		// Try exact code match first (A, B, C, D)
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

		// Content matching
		if (normalized.contains("linkedin"))
		{
			return A;
		}
		if (normalized.contains("facebook"))
		{
			return B;
		}
		if (normalized.contains("local friend") || normalized.contains("ami local") || normalized.equals("a local friend"))
		{
			return C;
		}
		if (normalized.contains("autre ville") || normalized.contains("autre pays") || normalized.contains("another city")
			|| normalized.contains("another country"))
		{
			return D;
		}

		return null;
	}

	/**
	 * Gets a HowFoundKusala from its code.
	 *
	 * @param code
	 *            the code (A-D)
	 * @return the corresponding HowFoundKusala, or null if invalid
	 */
	public static HowFoundKusala fromCode(String code)
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
