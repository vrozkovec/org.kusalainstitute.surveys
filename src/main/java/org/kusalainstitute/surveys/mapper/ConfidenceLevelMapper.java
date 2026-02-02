package org.kusalainstitute.surveys.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kusalainstitute.surveys.pojo.enums.ConfidenceLevel;

/**
 * Mapper for PRE survey Q7 and Q9: Confidence levels for speaking and understanding.
 * Uses explicit Map-based lookups for easy multi-language support.
 */
public class ConfidenceLevelMapper implements ChoiceMapper<ConfidenceLevel>
{

	private static final Map<String, ConfidenceLevel> MAPPINGS = new HashMap<>();

	static
	{
		// French mappings
		MAPPINGS.put("pas du tout confiant(e)", ConfidenceLevel.NOT_AT_ALL_CONFIDENT);
		MAPPINGS.put("pas du tout", ConfidenceLevel.NOT_AT_ALL_CONFIDENT);
		MAPPINGS.put("un peu confiant(e)", ConfidenceLevel.SOMEWHAT_CONFIDENT);
		MAPPINGS.put("un peu", ConfidenceLevel.SOMEWHAT_CONFIDENT);
		MAPPINGS.put("confiant(e)", ConfidenceLevel.CONFIDENT);
		MAPPINGS.put("très confiant(e)", ConfidenceLevel.VERY_CONFIDENT);
		MAPPINGS.put("tres confiant(e)", ConfidenceLevel.VERY_CONFIDENT); // without accent
		MAPPINGS.put("très", ConfidenceLevel.VERY_CONFIDENT);
		MAPPINGS.put("extrêmement confiant(e)", ConfidenceLevel.EXTREMLY_CONFIDENT);
		MAPPINGS.put("extremement confiant(e)", ConfidenceLevel.EXTREMLY_CONFIDENT); // without accent
		MAPPINGS.put("extrêmement", ConfidenceLevel.EXTREMLY_CONFIDENT);

		// English mappings
		MAPPINGS.put("not at all confident", ConfidenceLevel.NOT_AT_ALL_CONFIDENT);
		MAPPINGS.put("not at all", ConfidenceLevel.NOT_AT_ALL_CONFIDENT);
		MAPPINGS.put("somewhat confident", ConfidenceLevel.SOMEWHAT_CONFIDENT);
		MAPPINGS.put("somewhat", ConfidenceLevel.SOMEWHAT_CONFIDENT);
		MAPPINGS.put("confident", ConfidenceLevel.CONFIDENT);
		MAPPINGS.put("very confident", ConfidenceLevel.VERY_CONFIDENT);
		MAPPINGS.put("very", ConfidenceLevel.VERY_CONFIDENT);
		MAPPINGS.put("extremly confident", ConfidenceLevel.EXTREMLY_CONFIDENT);
		MAPPINGS.put("extremely confident", ConfidenceLevel.EXTREMLY_CONFIDENT); // correct spelling
	}

	@Override
	public ConfidenceLevel map(String value)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}

		String normalized = value.toLowerCase().trim();

		// Try direct mapping first
		ConfidenceLevel result = MAPPINGS.get(normalized);
		if (result != null)
		{
			return result;
		}

		// Try numeric prefix (e.g., "1 = Pas du tout confiant(e)")
		if (!normalized.isEmpty() && Character.isDigit(normalized.charAt(0)))
		{
			int digit = Character.getNumericValue(normalized.charAt(0));
			ConfidenceLevel byValue = ConfidenceLevel.fromValue(digit);
			if (byValue != null)
			{
				return byValue;
			}
		}

		// Try partial matching
		for (Map.Entry<String, ConfidenceLevel> entry : MAPPINGS.entrySet())
		{
			if (normalized.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	@Override
	public Set<ConfidenceLevel> mapMultiple(String value)
	{
		// Single-select field
		ConfidenceLevel single = map(value);
		return single != null ? EnumSet.of(single) : EnumSet.noneOf(ConfidenceLevel.class);
	}
}
