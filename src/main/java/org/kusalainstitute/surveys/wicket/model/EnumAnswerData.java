package org.kusalainstitute.surveys.wicket.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.kusalainstitute.surveys.pojo.enums.AppFrequency;
import org.kusalainstitute.surveys.pojo.enums.AppTimePerSession;
import org.kusalainstitute.surveys.pojo.enums.ChildrenAgeGroup;
import org.kusalainstitute.surveys.pojo.enums.HowFoundKusala;
import org.kusalainstitute.surveys.pojo.enums.ProgressAssessment;
import org.kusalainstitute.surveys.pojo.enums.StudyDuration;

/**
 * Represents an enum answer data for a single cell in the situation analysis table. Holds the
 * display value (English label or comma-separated for multi-select), question number, and whether
 * it's from a pre-survey.
 *
 * @param displayValue
 *            the enum's English label, or comma-separated values for multi-select, or "-" if null
 * @param questionNumber
 *            the question number e.g., "PRE Q1", "POST Q2"
 * @param isPreSurvey
 *            true for pre-survey answers, false for post-survey
 */
public record EnumAnswerData(String displayValue, String questionNumber, boolean isPreSurvey) implements Serializable
{

	private static final String NO_VALUE = "-";

	/**
	 * Creates EnumAnswerData from a HowFoundKusala value (PRE Q1).
	 *
	 * @param value
	 *            the enum value, can be null
	 * @return EnumAnswerData with the English label or "-" if null
	 */
	public static EnumAnswerData ofHowFoundKusala(HowFoundKusala value)
	{
		String displayValue = value != null ? value.getEnglishLabel() : NO_VALUE;
		return new EnumAnswerData(displayValue, "PRE Q1", true);
	}

	/**
	 * Creates EnumAnswerData from a StudyDuration value for PRE Q2.
	 *
	 * @param value
	 *            the enum value, can be null
	 * @return EnumAnswerData with the English label or "-" if null
	 */
	public static EnumAnswerData ofStudyWithTeacher(StudyDuration value)
	{
		String displayValue = value != null ? value.getEnglishLabel() : NO_VALUE;
		return new EnumAnswerData(displayValue, "PRE Q2", true);
	}

	/**
	 * Creates EnumAnswerData from a StudyDuration value for PRE Q3.
	 *
	 * @param value
	 *            the enum value, can be null
	 * @return EnumAnswerData with the English label or "-" if null
	 */
	public static EnumAnswerData ofStudyOnOwn(StudyDuration value)
	{
		String displayValue = value != null ? value.getEnglishLabel() : NO_VALUE;
		return new EnumAnswerData(displayValue, "PRE Q3", true);
	}

	/**
	 * Creates EnumAnswerData from a Set of ChildrenAgeGroup values (PRE Q4 - multi-select).
	 *
	 * @param values
	 *            the set of enum values, can be null or empty
	 * @return EnumAnswerData with comma-separated English labels or "-" if empty/null
	 */
	public static EnumAnswerData ofChildrenAges(Set<ChildrenAgeGroup> values)
	{
		String displayValue = formatMultiSelect(values, ChildrenAgeGroup::getEnglishLabel);
		return new EnumAnswerData(displayValue, "PRE Q4", true);
	}

	/**
	 * Creates EnumAnswerData from an AppTimePerSession value for POST Q2.
	 *
	 * @param value
	 *            the enum value, can be null
	 * @return EnumAnswerData with the English label or "-" if null
	 */
	public static EnumAnswerData ofAppTimePerSession(AppTimePerSession value)
	{
		String displayValue = value != null ? value.getEnglishLabel() : NO_VALUE;
		return new EnumAnswerData(displayValue, "POST Q2", false);
	}

	/**
	 * Creates EnumAnswerData from an AppFrequency value for POST Q3.
	 *
	 * @param value
	 *            the enum value, can be null
	 * @return EnumAnswerData with the English label or "-" if null
	 */
	public static EnumAnswerData ofAppFrequency(AppFrequency value)
	{
		String displayValue = value != null ? value.getEnglishLabel() : NO_VALUE;
		return new EnumAnswerData(displayValue, "POST Q3", false);
	}

	/**
	 * Creates EnumAnswerData from a ProgressAssessment value for POST Q4.
	 *
	 * @param value
	 *            the enum value, can be null
	 * @return EnumAnswerData with the English label or "-" if null
	 */
	public static EnumAnswerData ofProgressAssessment(ProgressAssessment value)
	{
		String displayValue = value != null ? value.getEnglishLabel() : NO_VALUE;
		return new EnumAnswerData(displayValue, "POST Q4", false);
	}

	/**
	 * Returns the CSS class for styling based on survey type.
	 *
	 * @return "enum-cell-pre" for pre-survey, "enum-cell-post" for post-survey
	 */
	public String getCssClass()
	{
		return isPreSurvey ? "enum-cell-pre" : "enum-cell-post";
	}

	/**
	 * Formats a collection of enum values as a comma-separated string of labels.
	 *
	 * @param <T>
	 *            the enum type
	 * @param values
	 *            the collection of values
	 * @param labelExtractor
	 *            function to extract the label from each enum value
	 * @return comma-separated labels or "-" if empty/null
	 */
	private static <T> String formatMultiSelect(Collection<T> values, java.util.function.Function<T, String> labelExtractor)
	{
		if (values == null || values.isEmpty())
		{
			return NO_VALUE;
		}
		return values.stream()
			.map(labelExtractor)
			.collect(Collectors.joining(", "));
	}
}
