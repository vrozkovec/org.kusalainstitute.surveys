package org.kusalainstitute.surveys.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kusalainstitute.surveys.pojo.enums.InterviewTypePreference;

/**
 * Mapper for POST survey Q16: Preferred interview type.
 * Uses explicit Map-based lookups for easy multi-language support.
 */
public class InterviewTypePreferenceMapper implements ChoiceMapper<InterviewTypePreference>
{

	private static final Map<String, InterviewTypePreference> MAPPINGS = new HashMap<>();

	static
	{
		// French mappings
		MAPPINGS.put("je n'ai pas de préférence", InterviewTypePreference.A);
		MAPPINGS.put("pas de préférence", InterviewTypePreference.A);
		MAPPINGS.put("n'ai pas de preference", InterviewTypePreference.A); // without accent
		MAPPINGS.put("appel vidéo utilisant whatsapp, zoom ou google meet", InterviewTypePreference.B);
		MAPPINGS.put("appel vidéo", InterviewTypePreference.B);
		MAPPINGS.put("appel vocal utilisant whatsapp avec mon numéro ci-dessous", InterviewTypePreference.C);
		MAPPINGS.put("appel vocal", InterviewTypePreference.C);

		// English mappings
		MAPPINGS.put("no preference", InterviewTypePreference.A);
		MAPPINGS.put("video call (whatsapp/zoom/meet)", InterviewTypePreference.B);
		MAPPINGS.put("video call", InterviewTypePreference.B);
		MAPPINGS.put("voice call (whatsapp)", InterviewTypePreference.C);
		MAPPINGS.put("voice call", InterviewTypePreference.C);

		// Common platform names
		MAPPINGS.put("zoom", InterviewTypePreference.B);
		MAPPINGS.put("google meet", InterviewTypePreference.B);
	}

	@Override
	public InterviewTypePreference map(String value)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}

		String normalized = value.toLowerCase().trim();

		// Try direct mapping first
		InterviewTypePreference result = MAPPINGS.get(normalized);
		if (result != null)
		{
			return result;
		}

		// Try letter prefix (e.g., "A= Je n'ai pas de préférence")
		if (!normalized.isEmpty() && Character.isLetter(normalized.charAt(0)))
		{
			char prefix = Character.toUpperCase(normalized.charAt(0));
			if (prefix >= 'A' && prefix <= 'C')
			{
				return InterviewTypePreference.fromCode(String.valueOf(prefix));
			}
		}

		// Try partial matching
		for (Map.Entry<String, InterviewTypePreference> entry : MAPPINGS.entrySet())
		{
			if (normalized.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	@Override
	public Set<InterviewTypePreference> mapMultiple(String value)
	{
		// Single-select field
		InterviewTypePreference single = map(value);
		return single != null ? EnumSet.of(single) : EnumSet.noneOf(InterviewTypePreference.class);
	}
}
