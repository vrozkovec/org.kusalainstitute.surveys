package org.kusalainstitute.surveys.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kusalainstitute.surveys.pojo.enums.AppTimePerSession;

/**
 * Mapper for POST survey Q1/Q2: App usage duration and time per session.
 * Uses explicit Map-based lookups for easy multi-language support.
 */
public class AppTimePerSessionMapper implements ChoiceMapper<AppTimePerSession>
{

	private static final Map<String, AppTimePerSession> MAPPINGS = new HashMap<>();

	static
	{
		// French mappings
		MAPPINGS.put("moins de 15 minutes", AppTimePerSession.A);
		MAPPINGS.put("15-30 minutes", AppTimePerSession.B);
		MAPPINGS.put("entre 30 minutes et une heure", AppTimePerSession.C);
		MAPPINGS.put("1-2 heures", AppTimePerSession.D);
		MAPPINGS.put("plus de deux heures", AppTimePerSession.E);

		// English mappings
		MAPPINGS.put("less than 15 minutes", AppTimePerSession.A);
		MAPPINGS.put("30 min - 1 hour", AppTimePerSession.C);
		MAPPINGS.put("1-2 hours", AppTimePerSession.D);
		MAPPINGS.put("more than 2 hours", AppTimePerSession.E);
	}

	@Override
	public AppTimePerSession map(String value)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}

		String normalized = value.toLowerCase().trim();

		// Try direct mapping first
		AppTimePerSession result = MAPPINGS.get(normalized);
		if (result != null)
		{
			return result;
		}

		// Try letter prefix (e.g., "A= Moins de 15 minutes")
		if (!normalized.isEmpty() && Character.isLetter(normalized.charAt(0)))
		{
			char prefix = Character.toUpperCase(normalized.charAt(0));
			if (prefix >= 'A' && prefix <= 'E')
			{
				return AppTimePerSession.fromCode(String.valueOf(prefix));
			}
		}

		// Try partial matching
		for (Map.Entry<String, AppTimePerSession> entry : MAPPINGS.entrySet())
		{
			if (normalized.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	@Override
	public Set<AppTimePerSession> mapMultiple(String value)
	{
		// Single-select field
		AppTimePerSession single = map(value);
		return single != null ? EnumSet.of(single) : EnumSet.noneOf(AppTimePerSession.class);
	}
}
