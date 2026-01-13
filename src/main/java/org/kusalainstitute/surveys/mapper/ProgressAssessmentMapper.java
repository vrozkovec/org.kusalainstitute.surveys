package org.kusalainstitute.surveys.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kusalainstitute.surveys.pojo.enums.ProgressAssessment;

/**
 * Mapper for POST survey Q4: Progress assessment.
 * Uses explicit Map-based lookups for easy multi-language support.
 */
public class ProgressAssessmentMapper implements ChoiceMapper<ProgressAssessment>
{

	private static final Map<String, ProgressAssessment> MAPPINGS = new HashMap<>();

	static
	{
		// French mappings
		MAPPINGS.put("aucun progrès", ProgressAssessment.NO_PROGRESS);
		MAPPINGS.put("peu de progrès", ProgressAssessment.LITTLE_PROGRESS);
		MAPPINGS.put("progrès modérés", ProgressAssessment.MODERATE_PROGRESS);
		MAPPINGS.put("progrès significatifs", ProgressAssessment.SIGNIFICANT_PROGRESS);
		MAPPINGS.put("progrès énormes", ProgressAssessment.HUGE_PROGRESS);
		MAPPINGS.put("progres enormes", ProgressAssessment.HUGE_PROGRESS); // without accents

		// English mappings
		MAPPINGS.put("no progress", ProgressAssessment.NO_PROGRESS);
		MAPPINGS.put("little progress", ProgressAssessment.LITTLE_PROGRESS);
		MAPPINGS.put("moderate progress", ProgressAssessment.MODERATE_PROGRESS);
		MAPPINGS.put("significant progress", ProgressAssessment.SIGNIFICANT_PROGRESS);
		MAPPINGS.put("huge progress", ProgressAssessment.HUGE_PROGRESS);
	}

	@Override
	public ProgressAssessment map(String value)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}

		String normalized = value.toLowerCase().trim();

		// Try direct mapping first
		ProgressAssessment result = MAPPINGS.get(normalized);
		if (result != null)
		{
			return result;
		}

		// Try numeric prefix (e.g., "1= Aucun progrès")
		if (!normalized.isEmpty() && Character.isDigit(normalized.charAt(0)))
		{
			int digit = Character.getNumericValue(normalized.charAt(0));
			ProgressAssessment byCode = ProgressAssessment.fromCode(digit);
			if (byCode != null)
			{
				return byCode;
			}
		}

		// Try partial matching
		for (Map.Entry<String, ProgressAssessment> entry : MAPPINGS.entrySet())
		{
			if (normalized.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	@Override
	public Set<ProgressAssessment> mapMultiple(String value)
	{
		// Single-select field
		ProgressAssessment single = map(value);
		return single != null ? EnumSet.of(single) : EnumSet.noneOf(ProgressAssessment.class);
	}
}
