package org.kusalainstitute.surveys.pojo.enums;

/**
 * Confidence level for English language skills (1-4 scale). Maps French survey responses to numeric
 * values.
 */
public enum ConfidenceLevel
{
	/** */
	NOT_AT_ALL_CONFIDENT(1, "Pas du tout confiant(e)", "Not at all confident"),
	/** */
	SOMEWHAT_CONFIDENT(2, "Un peu confiant(e)", "Somewhat confident"),
	/** */
	CONFIDENT(3, "Confiant(e)", "Confident"),
	/** */
	VERY_CONFIDENT(4, "Très confiant(e)", "Very confident"),
	/** */
	EXTREMLY_CONFIDENT(5, "Extrêmement confiant(e)", "Extremly confident");

	private final int value;
	private final String frenchLabel;
	private final String englishLabel;

	ConfidenceLevel(int value, String frenchLabel, String englishLabel)
	{
		this.value = value;
		this.frenchLabel = frenchLabel;
		this.englishLabel = englishLabel;
	}

	public int getValue()
	{
		return value;
	}

	/**
	 * Gets the ConfidenceLevel from a numeric value.
	 *
	 * @param value
	 *            the numeric value (1-4)
	 * @return the corresponding ConfidenceLevel, or null if invalid
	 */
	public static ConfidenceLevel fromValue(int value)
	{
		for (ConfidenceLevel level : values())
		{
			if (level.value == value)
			{
				return level;
			}
		}
		return null;
	}
}
