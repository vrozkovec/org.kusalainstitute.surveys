package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Represents one student row in the situation analysis table. Contains the student's identity and
 * situation data for speaking (pre/post comparison), understanding (pre Q9 only), ease (post Q7
 * inverted only), and text answers from both pre and post surveys.
 */
public record StudentRow(
	String name,
	Long id,
	String cohort,
	BigDecimal totalSpeakingChange,
	List<SituationData> speakingData,
	List<SingleValueData> understandingData,
	List<SingleValueData> easeData,
	List<TextAnswerData> textAnswers) implements Serializable
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
