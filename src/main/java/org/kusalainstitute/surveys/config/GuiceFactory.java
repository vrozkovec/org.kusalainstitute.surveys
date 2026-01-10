package org.kusalainstitute.surveys.config;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;

import picocli.CommandLine;

/**
 * Picocli command factory that uses Guice to create command instances. This enables dependency
 * injection in CLI command classes.
 */
public class GuiceFactory implements CommandLine.IFactory
{

	private final Injector injector;
	private final CommandLine.IFactory defaultFactory;

	/**
	 * Creates a new GuiceFactory with the given injector.
	 *
	 * @param injector
	 *            the Guice injector to use for command creation
	 */
	public GuiceFactory(Injector injector)
	{
		this.injector = injector;
		this.defaultFactory = CommandLine.defaultFactory();
	}

	@Override
	public <K> K create(Class<K> cls) throws Exception
	{
		try
		{
			return injector.getInstance(cls);
		}
		catch (ConfigurationException e)
		{
			// Fall back to default factory for classes not managed by Guice
			return defaultFactory.create(cls);
		}
	}
}
