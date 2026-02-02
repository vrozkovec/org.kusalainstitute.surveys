package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Yes/No options for POST survey Q14 (willing to interview). Uses YES/NO codes for
 * language-independent storage.
 */
public enum YesNo
{

	YES("Yes", "Oui"), NO("No", "Non");

	private final String englishLabel;
	private final String frenchLabel;

	YesNo(String englishLabel, String frenchLabel)
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
	 * @return the code string (YES or NO)
	 */
	public String getCode()
	{
		return name();
	}

	/**
	 * Gets a YesNo from its code.
	 *
	 * @param code
	 *            the code (YES or NO)
	 * @return the corresponding YesNo, or null if invalid
	 */
	public static YesNo fromCode(String code)
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

	/**
	 * Returns true if this is YES.
	 *
	 * @return true if YES
	 */
	public boolean isYes()
	{
		return this == YES;
	}

	/**
	 * Returns true if this is NO.
	 *
	 * @return true if NO
	 */
	public boolean isNo()
	{
		return this == NO;
	}
}
