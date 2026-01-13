package org.kusalainstitute.surveys.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kusalainstitute.surveys.pojo.enums.YesNo;

/**
 * Mapper for POST survey Q14: Willing to interview (Yes/No).
 * Uses explicit Map-based lookups for easy multi-language support.
 */
public class YesNoMapper implements ChoiceMapper<YesNo>
{

	private static final Map<String, YesNo> MAPPINGS = new HashMap<>();

	static
	{
		// French mappings
		MAPPINGS.put("oui", YesNo.YES);
		MAPPINGS.put("non", YesNo.NO);
		MAPPINGS.put("o", YesNo.YES); // abbreviation

		// English mappings
		MAPPINGS.put("yes", YesNo.YES);
		MAPPINGS.put("no", YesNo.NO);
		MAPPINGS.put("y", YesNo.YES); // abbreviation
		MAPPINGS.put("n", YesNo.NO); // abbreviation
	}

	@Override
	public YesNo map(String value)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}

		String normalized = value.toLowerCase().trim();

		// Try direct mapping first
		YesNo result = MAPPINGS.get(normalized);
		if (result != null)
		{
			return result;
		}

		// Try partial matching
		for (Map.Entry<String, YesNo> entry : MAPPINGS.entrySet())
		{
			if (normalized.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	@Override
	public Set<YesNo> mapMultiple(String value)
	{
		// Single-select field
		YesNo single = map(value);
		return single != null ? EnumSet.of(single) : EnumSet.noneOf(YesNo.class);
	}
}
