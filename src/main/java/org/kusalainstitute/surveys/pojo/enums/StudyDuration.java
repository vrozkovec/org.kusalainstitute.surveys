package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Study duration options for PRE survey Q2 (with teacher) and Q3 (on own). Uses letter codes A-F
 * for language-independent storage.
 */
public enum StudyDuration
{

	A("Less than 3 months", "Moins de 3 mois"), B("4 months - 1 year", "Entre 4 mois et 1 an"), C("1-2 years",
		"1-2 ans"), D("2-5 years", "2-5 ans"), E("5-10 years", "5-10 ans"), F("10+ years", "10 ans ou plus");

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
