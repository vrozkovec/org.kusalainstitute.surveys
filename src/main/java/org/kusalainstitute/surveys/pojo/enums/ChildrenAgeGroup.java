package org.kusalainstitute.surveys.pojo.enums;

import java.util.EnumSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Children age group options for PRE survey Q4. Uses letter codes A-E for language-independent
 * storage. This is a multi-select field - users can choose multiple options.
 */
public enum ChildrenAgeGroup
{

	A("5 and under", "5 ans et moins"), B("6-12", "6-12 ans"), C("13-18", "13-18 ans"), D("19+", "19 ans et plus"), E("No children",
		"Pas d'enfants");

	private final String englishLabel;
	private final String frenchLabel;

	ChildrenAgeGroup(String englishLabel, String frenchLabel)
	{
		this.englishLabel = englishLabel;
		this.frenchLabel = frenchLabel;
	}

	/**
	 * Returns the English label for this age group.
	 *
	 * @return the English label
	 */
	public String getEnglishLabel()
	{
		return englishLabel;
	}

	/**
	 * Returns the French label for this age group.
	 *
	 * @return the French label
	 */
	public String getFrenchLabel()
	{
		return frenchLabel;
	}

	/**
	 * Returns the enum code (A, B, C, etc.) for database storage.
	 *
	 * @return the code string
	 */
	public String getCode()
	{
		return name();
	}

	/**
	 * Gets a ChildrenAgeGroup from its code.
	 *
	 * @param code
	 *            the code (A-E)
	 * @return the corresponding ChildrenAgeGroup, or null if invalid
	 */
	public static ChildrenAgeGroup fromCode(String code)
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

	/**
	 * Parses a comma-separated list of codes to a set of ChildrenAgeGroups.
	 *
	 * @param codes
	 *            comma-separated codes (e.g., "A,B,C")
	 * @return set of corresponding ChildrenAgeGroups (empty if none found)
	 */
	public static Set<ChildrenAgeGroup> fromCodes(String codes)
	{
		Set<ChildrenAgeGroup> result = EnumSet.noneOf(ChildrenAgeGroup.class);
		if (StringUtils.isBlank(codes))
		{
			return result;
		}

		String[] parts = codes.split(",");
		for (String part : parts)
		{
			ChildrenAgeGroup group = fromCode(part.trim());
			if (group != null)
			{
				result.add(group);
			}
		}

		return result;
	}

	/**
	 * Converts a set of ChildrenAgeGroups to a comma-separated string of codes.
	 *
	 * @param groups
	 *            the set of age groups
	 * @return comma-separated codes (e.g., "A,B,C")
	 */
	public static String toCodes(Set<ChildrenAgeGroup> groups)
	{
		if (groups == null || groups.isEmpty())
		{
			return null;
		}

		return String.join(",", groups.stream().sorted().map(ChildrenAgeGroup::getCode).toList());
	}
}
