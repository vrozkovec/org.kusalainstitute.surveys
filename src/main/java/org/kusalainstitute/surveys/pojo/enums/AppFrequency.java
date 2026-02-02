package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * App usage frequency options for POST survey Q3. Uses numeric codes 1-5 for language-independent
 * storage.
 */
public enum AppFrequency
{

	VERY_FREQUENT(1, "Very frequently, multiple times per day", "Très fréquemment, plusieurs fois par jour"), FREQUENT(2,
		"Frequently, at least once per day", "Fréquemment, au moins une fois par jour"), REGULAR(3, "Regularly, few times per week",
			"Régulièrement, quelques fois par semaine"), OCCASIONAL(4, "Occasionally, few times per month",
				"Occasionnellement, quelques fois par mois"), RARE(5, "Rarely, almost never", "Rarement, presque jamais");

	private final int code;
	private final String englishLabel;
	private final String frenchLabel;

	AppFrequency(int code, String englishLabel, String frenchLabel)
	{
		this.code = code;
		this.englishLabel = englishLabel;
		this.frenchLabel = frenchLabel;
	}

	/**
	 * Returns the numeric code (1-5) for this frequency.
	 *
	 * @return the numeric code
	 */
	public int getCode()
	{
		return code;
	}

	/**
	 * Returns the code as a string for database storage.
	 *
	 * @return the code string
	 */
	public String getCodeString()
	{
		return String.valueOf(code);
	}

	/**
	 * Returns the English label for this frequency.
	 *
	 * @return the English label
	 */
	public String getEnglishLabel()
	{
		return englishLabel;
	}

	/**
	 * Returns the French label for this frequency.
	 *
	 * @return the French label
	 */
	public String getFrenchLabel()
	{
		return frenchLabel;
	}

	/**
	 * Gets an AppFrequency from its numeric code.
	 *
	 * @param code
	 *            the code (1-5)
	 * @return the corresponding AppFrequency, or null if invalid
	 */
	public static AppFrequency fromCode(int code)
	{
		return switch (code)
		{
			case 1 -> VERY_FREQUENT;
			case 2 -> FREQUENT;
			case 3 -> REGULAR;
			case 4 -> OCCASIONAL;
			case 5 -> RARE;
			default -> null;
		};
	}

	/**
	 * Gets an AppFrequency from its code string.
	 *
	 * @param codeString
	 *            the code string ("1"-"5")
	 * @return the corresponding AppFrequency, or null if invalid
	 */
	public static AppFrequency fromCodeString(String codeString)
	{
		if (StringUtils.isBlank(codeString))
		{
			return null;
		}
		try
		{
			int code = Integer.parseInt(codeString.trim());
			return fromCode(code);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
}
