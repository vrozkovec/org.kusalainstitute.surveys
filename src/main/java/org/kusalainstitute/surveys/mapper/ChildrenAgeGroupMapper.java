package org.kusalainstitute.surveys.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.kusalainstitute.surveys.pojo.enums.ChildrenAgeGroup;

/**
 * Mapper for PRE survey Q4: Children age groups.
 * This is a multi-select field - users can choose multiple options.
 * Uses explicit Map-based lookups for easy multi-language support.
 */
public class ChildrenAgeGroupMapper implements ChoiceMapper<ChildrenAgeGroup>
{

	private static final Map<String, ChildrenAgeGroup> MAPPINGS = new HashMap<>();

	static
	{
		// French mappings
		MAPPINGS.put("5 ans et moins", ChildrenAgeGroup.A);
		MAPPINGS.put("5 ou moins", ChildrenAgeGroup.A);
		MAPPINGS.put("6-12 ans", ChildrenAgeGroup.B);
		MAPPINGS.put("13-18 ans", ChildrenAgeGroup.C);
		MAPPINGS.put("19 ans et plus", ChildrenAgeGroup.D);
		MAPPINGS.put("19 et plus", ChildrenAgeGroup.D);
		MAPPINGS.put("pas d'enfants", ChildrenAgeGroup.E);
		MAPPINGS.put("pas d'enfant", ChildrenAgeGroup.E);

		// English mappings
		MAPPINGS.put("5 and under", ChildrenAgeGroup.A);
		MAPPINGS.put("6-12", ChildrenAgeGroup.B);
		MAPPINGS.put("13-18", ChildrenAgeGroup.C);
		MAPPINGS.put("19+", ChildrenAgeGroup.D);
		MAPPINGS.put("no children", ChildrenAgeGroup.E);
	}

	@Override
	public ChildrenAgeGroup map(String value)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}

		String normalized = value.toLowerCase().trim();

		// Try direct mapping first
		ChildrenAgeGroup result = MAPPINGS.get(normalized);
		if (result != null)
		{
			return result;
		}

		// Try letter prefix (e.g., "A= 5 ans et moins")
		if (!normalized.isEmpty() && Character.isLetter(normalized.charAt(0)))
		{
			char prefix = Character.toUpperCase(normalized.charAt(0));
			if (prefix >= 'A' && prefix <= 'E')
			{
				return ChildrenAgeGroup.fromCode(String.valueOf(prefix));
			}
		}

		// Try partial matching
		for (Map.Entry<String, ChildrenAgeGroup> entry : MAPPINGS.entrySet())
		{
			if (normalized.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	@Override
	public Set<ChildrenAgeGroup> mapMultiple(String value)
	{
		Set<ChildrenAgeGroup> result = EnumSet.noneOf(ChildrenAgeGroup.class);
		if (StringUtils.isBlank(value))
		{
			return result;
		}

		// Split by common delimiters
		String[] parts = value.split("[,;\\n]+");
		for (String part : parts)
		{
			ChildrenAgeGroup group = map(part.trim());
			if (group != null)
			{
				result.add(group);
			}
		}

		return result;
	}
}
