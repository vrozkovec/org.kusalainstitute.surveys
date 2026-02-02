package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Options for PRE survey Q1: How did you find out about Kusala Institute. Uses letter codes A-D for
 * language-independent storage.
 */
public enum HowFoundKusala
{

	A("LinkedIn", "LinkedIn"), B("Facebook", "Facebook"), C("A local friend", "Un ami local"), D("A friend in another city/country",
		"Un ami qui vit dans une autre ville ou un autre pays");

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
