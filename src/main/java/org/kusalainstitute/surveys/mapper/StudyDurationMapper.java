package org.kusalainstitute.surveys.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kusalainstitute.surveys.pojo.enums.StudyDuration;

/**
 * Mapper for PRE survey Q2 (with teacher) and Q3 (on own): Study duration.
 * Uses explicit Map-based lookups for easy multi-language support.
 */
public class StudyDurationMapper implements ChoiceMapper<StudyDuration>
{

	private static final Map<String, StudyDuration> MAPPINGS = new HashMap<>();

	static
	{
		// French mappings
		MAPPINGS.put("moins de 3 mois", StudyDuration.A);
		MAPPINGS.put("entre 4 mois et 1 an", StudyDuration.B);
		MAPPINGS.put("1-2 ans", StudyDuration.C);
		MAPPINGS.put("2-5 ans", StudyDuration.D);
		MAPPINGS.put("5-10 ans", StudyDuration.E);
		MAPPINGS.put("10 ans ou plus", StudyDuration.F);

		// English mappings
		MAPPINGS.put("less than 3 months", StudyDuration.A);
		MAPPINGS.put("4 months - 1 year", StudyDuration.B);
		MAPPINGS.put("1-2 years", StudyDuration.C);
		MAPPINGS.put("2-5 years", StudyDuration.D);
		MAPPINGS.put("5-10 years", StudyDuration.E);
		MAPPINGS.put("10+ years", StudyDuration.F);
	}

	@Override
	public StudyDuration map(String value)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}

		String normalized = value.toLowerCase().trim();

		// Try direct mapping first
		StudyDuration result = MAPPINGS.get(normalized);
		if (result != null)
		{
			return result;
		}

		// Try letter prefix (e.g., "A= Moins de 3 mois")
		if (!normalized.isEmpty() && Character.isLetter(normalized.charAt(0)))
		{
			char prefix = Character.toUpperCase(normalized.charAt(0));
			if (prefix >= 'A' && prefix <= 'F')
			{
				return StudyDuration.fromCode(String.valueOf(prefix));
			}
		}

		// Try partial matching
		for (Map.Entry<String, StudyDuration> entry : MAPPINGS.entrySet())
		{
			if (normalized.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	@Override
	public Set<StudyDuration> mapMultiple(String value)
	{
		// Single-select field
		StudyDuration single = map(value);
		return single != null ? EnumSet.of(single) : EnumSet.noneOf(StudyDuration.class);
	}
}
