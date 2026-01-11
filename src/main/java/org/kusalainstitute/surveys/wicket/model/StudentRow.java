package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Represents one student row in the situation analysis table.
 * Contains the student's identity and situation data for both speaking and understanding groups.
 */
public record StudentRow(
	String name,
	Long id,
	String cohort,
	BigDecimal totalSpeakingChange,
	BigDecimal totalUnderstandingChange,
	List<SituationData> speakingData,
	List<SituationData> understandingData) implements Serializable
{

	/**
	 * Returns a display-friendly truncated name.
	 *
	 * @param maxLength
	 *            maximum length before truncation
	 * @return truncated name with ellipsis if needed
	 */
	public String getDisplayName(int maxLength)
	{
		if (name == null)
		{
			return "Unknown";
		}
		if (name.length() <= maxLength)
		{
			return name;
		}
		return name.substring(0, maxLength - 3) + "...";
	}
}
