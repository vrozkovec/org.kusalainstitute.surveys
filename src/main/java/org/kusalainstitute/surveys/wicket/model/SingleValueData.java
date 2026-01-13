package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;

/**
 * Holds data for a single-value situation cell in the analysis table.
 * Used for Understanding (Q9) and Ease (Q7 Inv) which are displayed
 * as standalone values without pre/post comparison.
 */
public record SingleValueData(
	Integer value,
	int widthPercent) implements Serializable
{

	private static final int MAX_SCALE = 5;

	/**
	 * Creates a SingleValueData from a value.
	 * Automatically calculates bar width percentage.
	 *
	 * @param value
	 *            the value (1-5), can be null
	 * @return new SingleValueData instance
	 */
	public static SingleValueData of(Integer value)
	{
		int widthPercent = calculateWidthPercent(value);
		return new SingleValueData(value, widthPercent);
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
	 * Returns the formatted value string.
	 *
	 * @return the value as string, or "-" if null
	 */
	public String getFormattedValue()
	{
		return value != null ? String.valueOf(value) : "-";
	}
}
