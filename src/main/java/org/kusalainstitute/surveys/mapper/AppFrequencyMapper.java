package org.kusalainstitute.surveys.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kusalainstitute.surveys.pojo.enums.AppFrequency;

/**
 * Mapper for POST survey Q3: App usage frequency.
 * Uses explicit Map-based lookups for easy multi-language support.
 */
public class AppFrequencyMapper implements ChoiceMapper<AppFrequency>
{

	private static final Map<String, AppFrequency> MAPPINGS = new HashMap<>();

	static
	{
		// French mappings
		MAPPINGS.put("très fréquemment, plusieurs fois par jour", AppFrequency.VERY_FREQUENT);
		MAPPINGS.put("très fréquemment", AppFrequency.VERY_FREQUENT);
		MAPPINGS.put("plusieurs fois par jour", AppFrequency.VERY_FREQUENT);
		MAPPINGS.put("fréquemment, au moins une fois par jour", AppFrequency.FREQUENT);
		MAPPINGS.put("fréquemment", AppFrequency.FREQUENT);
		MAPPINGS.put("une fois par jour", AppFrequency.FREQUENT);
		MAPPINGS.put("régulièrement, quelques fois par semaine", AppFrequency.REGULAR);
		MAPPINGS.put("régulièrement", AppFrequency.REGULAR);
		MAPPINGS.put("quelques fois par semaine", AppFrequency.REGULAR);
		MAPPINGS.put("occasionnellement, quelques fois par mois", AppFrequency.OCCASIONAL);
		MAPPINGS.put("occasionnellement", AppFrequency.OCCASIONAL);
		MAPPINGS.put("quelques fois par mois", AppFrequency.OCCASIONAL);
		MAPPINGS.put("rarement, presque jamais", AppFrequency.RARE);
		MAPPINGS.put("rarement", AppFrequency.RARE);
		MAPPINGS.put("presque jamais", AppFrequency.RARE);

		// English mappings
		MAPPINGS.put("very frequently, multiple times per day", AppFrequency.VERY_FREQUENT);
		MAPPINGS.put("very frequently", AppFrequency.VERY_FREQUENT);
		MAPPINGS.put("multiple times per day", AppFrequency.VERY_FREQUENT);
		MAPPINGS.put("frequently, at least once per day", AppFrequency.FREQUENT);
		MAPPINGS.put("frequently", AppFrequency.FREQUENT);
		MAPPINGS.put("once per day", AppFrequency.FREQUENT);
		MAPPINGS.put("regularly, few times per week", AppFrequency.REGULAR);
		MAPPINGS.put("regularly", AppFrequency.REGULAR);
		MAPPINGS.put("few times per week", AppFrequency.REGULAR);
		MAPPINGS.put("occasionally, few times per month", AppFrequency.OCCASIONAL);
		MAPPINGS.put("occasionally", AppFrequency.OCCASIONAL);
		MAPPINGS.put("few times per month", AppFrequency.OCCASIONAL);
		MAPPINGS.put("rarely, almost never", AppFrequency.RARE);
		MAPPINGS.put("rarely", AppFrequency.RARE);
		MAPPINGS.put("almost never", AppFrequency.RARE);
	}

	@Override
	public AppFrequency map(String value)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}

		String normalized = value.toLowerCase().trim();

		// Try direct mapping first
		AppFrequency result = MAPPINGS.get(normalized);
		if (result != null)
		{
			return result;
		}

		// Try numeric prefix (e.g., "1= Très fréquemment...")
		if (!normalized.isEmpty() && Character.isDigit(normalized.charAt(0)))
		{
			int digit = Character.getNumericValue(normalized.charAt(0));
			AppFrequency byCode = AppFrequency.fromCode(digit);
			if (byCode != null)
			{
				return byCode;
			}
		}

		// Try partial matching
		for (Map.Entry<String, AppFrequency> entry : MAPPINGS.entrySet())
		{
			if (normalized.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	@Override
	public Set<AppFrequency> mapMultiple(String value)
	{
		// Single-select field
		AppFrequency single = map(value);
		return single != null ? EnumSet.of(single) : EnumSet.noneOf(AppFrequency.class);
	}
}
