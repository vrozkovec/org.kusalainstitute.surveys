package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Interview type preference options for POST survey Q16. Uses letter codes A-C for
 * language-independent storage.
 */
public enum InterviewTypePreference
{

	A("No preference", "Je n'ai pas de préférence"), B("Video call (WhatsApp/Zoom/Meet)",
		"Appel vidéo utilisant WhatsApp, Zoom ou Google Meet"), C("Voice call (WhatsApp)",
			"Appel vocal utilisant WhatsApp avec mon numéro ci-dessous");

	private final String englishLabel;
	private final String frenchLabel;

	InterviewTypePreference(String englishLabel, String frenchLabel)
	{
		this.englishLabel = englishLabel;
		this.frenchLabel = frenchLabel;
	}

	/**
	 * Returns the English label for this preference.
	 *
	 * @return the English label
	 */
	public String getEnglishLabel()
	{
		return englishLabel;
	}

	/**
	 * Returns the French label for this preference.
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
	 * @return the code string (A, B, or C)
	 */
	public String getCode()
	{
		return name();
	}

	/**
	 * Gets an InterviewTypePreference from its code.
	 *
	 * @param code
	 *            the code (A, B, or C)
	 * @return the corresponding InterviewTypePreference, or null if invalid
	 */
	public static InterviewTypePreference fromCode(String code)
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
