package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Interview type preference options for POST survey Q16.
 * Uses letter codes A-C for language-independent storage.
 */
public enum InterviewTypePreference
{

	A("No preference", "Je n'ai pas de préférence"),
	B("Video call (WhatsApp/Zoom/Meet)", "Appel vidéo utilisant WhatsApp, Zoom ou Google Meet"),
	C("Voice call (WhatsApp)", "Appel vocal utilisant WhatsApp avec mon numéro ci-dessous");

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
	 * Parses a text value to an InterviewTypePreference. Handles French and English variations.
	 *
	 * @param text
	 *            the response text
	 * @return the corresponding InterviewTypePreference, or null if not found
	 */
	public static InterviewTypePreference fromText(String text)
	{
		if (StringUtils.isBlank(text))
		{
			return null;
		}

		String normalized = text.toLowerCase().trim();

		// Try exact code match first (A, B, C)
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
		if (normalized.contains("pas de préférence") || normalized.contains("no preference") || normalized.contains("n'ai pas de preference"))
		{
			return A;
		}
		if (normalized.contains("appel vidéo") || normalized.contains("video call") || normalized.contains("zoom")
			|| normalized.contains("google meet"))
		{
			return B;
		}
		if (normalized.contains("appel vocal") || normalized.contains("voice call") || normalized.contains("whatsapp avec mon numéro")
			|| normalized.contains("whatsapp with my number"))
		{
			return C;
		}

		return null;
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
