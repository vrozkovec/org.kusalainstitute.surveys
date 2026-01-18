package org.kusalainstitute.surveys.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * Database configuration implementing IDbConfig for JDBI integration. Loads connection properties
 * from application.properties.
 */
@Singleton
public class SurveysDatabaseConfig implements IDbConfig
{

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(SurveysDatabaseConfig.class);
	private static final String PROPERTIES_FILE = "application.properties";

	private final Properties properties;

	/**
	 * Creates a new database configuration, loading properties from application.properties.
	 */
	public SurveysDatabaseConfig()
	{
		properties = loadProperties();
	}

	@Override
	public String getDbConnectionUrl()
	{
		return properties.getProperty("db.url");
	}

	@Override
	public String getDbConfigConnectionUrlParams()
	{
		// Extract params from URL if present (everything after ?)
		String url = getDbConnectionUrl();
		if (url != null && url.contains("?"))
		{
			return url.substring(url.indexOf('?') + 1);
		}
		return "";
	}

	@Override
	public String getDbConfigHostname()
	{
		// Extract hostname from JDBC URL
		String url = getDbConnectionUrl();
		if (url != null)
		{
			// jdbc:mysql://localhost/dbname -> localhost
			String withoutProtocol = url.replaceFirst("jdbc:\\w+://", "");
			int slashIndex = withoutProtocol.indexOf('/');
			if (slashIndex > 0)
			{
				return withoutProtocol.substring(0, slashIndex);
			}
		}
		return "localhost";
	}

	@Override
	public String getDbConfigDatabase()
	{
		// Extract database name from JDBC URL
		String url = getDbConnectionUrl();
		if (url != null)
		{
			String withoutProtocol = url.replaceFirst("jdbc:\\w+://", "");
			int slashIndex = withoutProtocol.indexOf('/');
			if (slashIndex > 0)
			{
				String afterSlash = withoutProtocol.substring(slashIndex + 1);
				int questionIndex = afterSlash.indexOf('?');
				if (questionIndex > 0)
				{
					return afterSlash.substring(0, questionIndex);
				}
				return afterSlash;
			}
		}
		return "";
	}

	@Override
	public String getDbConfigUsername()
	{
		return properties.getProperty("db.username");
	}

	@Override
	public String getDbConfigPassword()
	{
		return properties.getProperty("db.password", "");
	}

	/**
	 * Gets a property value from application.properties.
	 *
	 * @param key
	 *            the property key
	 * @return the property value, or null if not found
	 */
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	/**
	 * Gets a property value with a default.
	 *
	 * @param key
	 *            the property key
	 * @param defaultValue
	 *            default value if property not found
	 * @return the property value or default
	 */
	public String getProperty(String key, String defaultValue)
	{
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * Gets the maximum pool size configuration.
	 *
	 * @return maximum pool size
	 */
	public int getMaxPoolSize()
	{
		return Integer.parseInt(properties.getProperty("db.pool.maxSize", "10"));
	}

	/**
	 * Gets the minimum idle connections configuration.
	 *
	 * @return minimum idle connections
	 */
	public int getMinIdle()
	{
		return Integer.parseInt(properties.getProperty("db.pool.minIdle", "2"));
	}

	/**
	 * Gets the connection timeout in milliseconds.
	 *
	 * @return connection timeout
	 */
	public long getConnectionTimeout()
	{
		return Long.parseLong(properties.getProperty("db.pool.connectionTimeout", "30000"));
	}

	/**
	 * Gets the data directory path for survey files and persistence.
	 *
	 * @return the data directory path
	 */
	public String getDataDir()
	{
		return properties.getProperty("data.dir", "data");
	}

	/**
	 * Gets the manual matches file path. If not explicitly configured, defaults to
	 * {@code <data.dir>/manual-matches.properties}.
	 *
	 * @return the manual matches file path
	 */
	public String getManualMatchesFile()
	{
		String explicit = properties.getProperty("data.manual-matches.file");
		if (explicit != null && !explicit.isBlank())
		{
			return explicit;
		}
		return getDataDir() + "/manual-matches.properties";
	}

	private Properties loadProperties()
	{
		Properties props = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE))
		{
			if (input == null)
			{
				LOG.warn("Unable to find {}", PROPERTIES_FILE);
				return props;
			}
			props.load(input);
			LOG.info("Loaded database configuration from {}", PROPERTIES_FILE);
		}
		catch (IOException e)
		{
			LOG.error("Error loading properties file", e);
		}
		return props;
	}
}
