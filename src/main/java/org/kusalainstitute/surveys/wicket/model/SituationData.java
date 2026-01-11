package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Holds data for a single situation cell in the analysis table.
 * Contains pre/post values and the calculated delta.
 */
public record SituationData(
	Integer preValue,
	Integer postValue,
	BigDecimal delta,
	int preWidthPercent,
	int postWidthPercent) implements Serializable
{

	private static final int MAX_SCALE = 5;

	/**
	 * Creates a SituationData from pre and post values.
	 * Automatically calculates delta and bar width percentages.
	 *
	 * @param preValue
	 *            the pre-survey value (1-5), can be null
	 * @param postValue
	 *            the post-survey value (1-5), can be null
	 * @return new SituationData instance
	 */
	public static SituationData of(Integer preValue, Integer postValue)
	{
		BigDecimal delta = null;
		if (preValue != null && postValue != null)
		{
			delta = BigDecimal.valueOf(postValue - preValue);
		}

		int preWidthPercent = calculateWidthPercent(preValue);
		int postWidthPercent = calculateWidthPercent(postValue);

		return new SituationData(preValue, postValue, delta, preWidthPercent, postWidthPercent);
	}

	/**
	 * Calculates the bar width percentage for a value on a 1-5 scale.
	 *
	 * @param value
	 *            the value (1-5), can be null
	 * @return width percentage (0-100)
	 */
	private static int calculateWidthPercent(Integer value)
	{
		if (value == null)
		{
			return 0;
		}
		return (int)Math.round((value.doubleValue() / MAX_SCALE) * 100);
	}

	/**
	 * Returns the CSS class for the delta value.
	 *
	 * @return "pos" for positive, "neg" for negative, "neu" for zero/null
	 */
	public String getDeltaCssClass()
	{
		if (delta == null || delta.compareTo(BigDecimal.ZERO) == 0)
		{
			return "neu";
		}
		return delta.compareTo(BigDecimal.ZERO) > 0 ? "pos" : "neg";
	}

	/**
	 * Returns the formatted delta string with sign prefix.
	 *
	 * @return formatted delta like "+1.0", "-0.5", "0.0", or "--" if null
	 */
	public String getFormattedDelta()
	{
		if (delta == null)
		{
			return "--";
		}
		if (delta.compareTo(BigDecimal.ZERO) > 0)
		{
			return "+" + delta.setScale(1).toPlainString();
		}
		return delta.setScale(1).toPlainString();
	}
}
