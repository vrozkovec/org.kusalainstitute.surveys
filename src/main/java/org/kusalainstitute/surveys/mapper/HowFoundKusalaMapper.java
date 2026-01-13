package org.kusalainstitute.surveys.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kusalainstitute.surveys.pojo.enums.HowFoundKusala;

/**
 * Mapper for PRE survey Q1: How did you find out about Kusala Institute.
 * Uses explicit Map-based lookups for easy multi-language support.
 */
public class HowFoundKusalaMapper implements ChoiceMapper<HowFoundKusala>
{

	private static final Map<String, HowFoundKusala> MAPPINGS = new HashMap<>();

	static
	{
		// LinkedIn (same in both languages)
		MAPPINGS.put("linkedin", HowFoundKusala.A);

		// Facebook (same in both languages)
		MAPPINGS.put("facebook", HowFoundKusala.B);

		// Local friend
		MAPPINGS.put("un ami local", HowFoundKusala.C);
		MAPPINGS.put("a local friend", HowFoundKusala.C);

		// Friend in another city/country
		MAPPINGS.put("un ami qui vit dans une autre ville ou un autre pays", HowFoundKusala.D);
		MAPPINGS.put("a friend in another city/country", HowFoundKusala.D);
	}

	@Override
	public HowFoundKusala map(String value)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}

		String normalized = value.toLowerCase().trim();

		// Try direct mapping first
		HowFoundKusala result = MAPPINGS.get(normalized);
		if (result != null)
		{
			return result;
		}

		// Try letter prefix (e.g., "A= LinkedIn")
		if (!normalized.isEmpty() && Character.isLetter(normalized.charAt(0)))
		{
			char prefix = Character.toUpperCase(normalized.charAt(0));
			if (prefix >= 'A' && prefix <= 'D')
			{
				return HowFoundKusala.fromCode(String.valueOf(prefix));
			}
		}

		// Try partial matching
		for (Map.Entry<String, HowFoundKusala> entry : MAPPINGS.entrySet())
		{
			if (normalized.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	@Override
	public Set<HowFoundKusala> mapMultiple(String value)
	{
		// Single-select field
		HowFoundKusala single = map(value);
		return single != null ? EnumSet.of(single) : EnumSet.noneOf(HowFoundKusala.class);
	}
}
