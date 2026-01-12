package org.kusalainstitute.surveys.pojo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Progress assessment options for POST survey Q4.
 * Uses numeric codes 1-5 for language-independent storage.
 */
public enum ProgressAssessment
{

	NO_PROGRESS(1, "No progress", "Aucun progrès"),
	LITTLE_PROGRESS(2, "Little progress", "Peu de progrès"),
	MODERATE_PROGRESS(3, "Moderate progress", "Progrès modérés"),
	SIGNIFICANT_PROGRESS(4, "Significant progress", "Progrès significatifs"),
	HUGE_PROGRESS(5, "Huge progress", "Progrès énormes");

	private final int code;
	private final String englishLabel;
	private final String frenchLabel;

	ProgressAssessment(int code, String englishLabel, String frenchLabel)
	{
		this.code = code;
		this.englishLabel = englishLabel;
		this.frenchLabel = frenchLabel;
	}

	/**
	 * Returns the numeric code (1-5) for this progress level.
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
	 * Returns the English label for this progress level.
	 *
	 * @return the English label
	 */
	public String getEnglishLabel()
	{
		return englishLabel;
	}

	/**
	 * Returns the French label for this progress level.
	 *
	 * @return the French label
	 */
	public String getFrenchLabel()
	{
		return frenchLabel;
	}

	/**
	 * Parses a text value to a ProgressAssessment. Handles French and English variations.
	 *
	 * @param text
	 *            the response text
	 * @return the corresponding ProgressAssessment, or null if not found
	 */
	public static ProgressAssessment fromText(String text)
	{
		if (StringUtils.isBlank(text))
		{
			return null;
		}

		String normalized = text.toLowerCase().trim();

		// Try numeric prefix (e.g., "1=Aucun progrès")
		if (normalized.length() >= 1 && Character.isDigit(normalized.charAt(0)))
		{
			int digit = Character.getNumericValue(normalized.charAt(0));
			ProgressAssessment result = fromCode(digit);
			if (result != null)
			{
				return result;
			}
		}

		// Content matching - French
		if (normalized.contains("aucun progrès") || normalized.contains("no progress"))
		{
			return NO_PROGRESS;
		}
		if (normalized.contains("peu de progrès") || normalized.contains("little progress"))
		{
			return LITTLE_PROGRESS;
		}
		if (normalized.contains("progrès modérés") || normalized.contains("moderate progress"))
		{
			return MODERATE_PROGRESS;
		}
		if (normalized.contains("progrès significatifs") || normalized.contains("significant progress"))
		{
			return SIGNIFICANT_PROGRESS;
		}
		if (normalized.contains("progrès énormes") || normalized.contains("huge progress") || normalized.contains("progres enormes"))
		{
			return HUGE_PROGRESS;
		}

		return null;
	}

	/**
	 * Gets a ProgressAssessment from its numeric code.
	 *
	 * @param code
	 *            the code (1-5)
	 * @return the corresponding ProgressAssessment, or null if invalid
	 */
	public static ProgressAssessment fromCode(int code)
	{
		return switch (code) {
			case 1 -> NO_PROGRESS;
			case 2 -> LITTLE_PROGRESS;
			case 3 -> MODERATE_PROGRESS;
			case 4 -> SIGNIFICANT_PROGRESS;
			case 5 -> HUGE_PROGRESS;
			default -> null;
		};
	}

	/**
	 * Gets a ProgressAssessment from its code string.
	 *
	 * @param codeString
	 *            the code string ("1"-"5")
	 * @return the corresponding ProgressAssessment, or null if invalid
	 */
	public static ProgressAssessment fromCodeString(String codeString)
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
