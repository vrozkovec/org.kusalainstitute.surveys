package org.kusalainstitute.surveys.mapper;

import java.util.Set;

/**
 * Interface for mapping survey response text values to enum constants.
 * Handles multi-language text (French/English) and various input formats.
 *
 * @param <E> the enum type to map to
 */
public interface ChoiceMapper<E extends Enum<E>>
{

	/**
	 * Maps a single text value to an enum.
	 *
	 * @param value
	 *            the text value from survey response
	 * @return the corresponding enum, or null if not found
	 */
	E map(String value);

	/**
	 * Maps a multi-select text value to a set of enums.
	 * Values may be separated by comma, semicolon, or newline.
	 *
	 * @param value
	 *            comma/semicolon separated values
	 * @return set of corresponding enums (empty if none found)
	 */
	Set<E> mapMultiple(String value);
}
