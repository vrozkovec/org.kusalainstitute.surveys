package org.kusalainstitute.surveys.config;

import java.lang.reflect.Type;
import java.sql.Types;
import java.util.Optional;
import java.util.function.Function;

import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;

/**
 * JDBI argument factory that converts enum values to their name strings for database storage.
 *
 * @param <E>
 *            the enum type
 */
public class EnumByNameArgumentFactory<E extends Enum<E>> implements ArgumentFactory.Preparable
{

	private final Class<E> enumClass;
	private final Function<E, String> nameExtractor;

	/**
	 * Creates a new factory for the given enum class using Enum.name() for conversion.
	 *
	 * @param enumClass
	 *            the enum class
	 */
	public EnumByNameArgumentFactory(Class<E> enumClass)
	{
		this(enumClass, Enum::name);
	}

	/**
	 * Creates a new factory for the given enum class with a custom name extractor.
	 *
	 * @param enumClass
	 *            the enum class
	 * @param nameExtractor
	 *            function to extract the string value from the enum
	 */
	public EnumByNameArgumentFactory(Class<E> enumClass, Function<E, String> nameExtractor)
	{
		this.enumClass = enumClass;
		this.nameExtractor = nameExtractor;
	}

	@Override
	public Optional<Function<Object, Argument>> prepare(Type type, ConfigRegistry config)
	{
		if (type instanceof Class<?> clazz && enumClass.isAssignableFrom(clazz))
		{
			return Optional.of(value -> {
				if (value == null)
				{
					return (position, statement, ctx) -> statement.setNull(position, Types.VARCHAR);
				}
				@SuppressWarnings("unchecked")
				E enumValue = (E)value;
				String name = nameExtractor.apply(enumValue);
				return (position, statement, ctx) -> statement.setString(position, name);
			});
		}
		return Optional.empty();
	}
}
