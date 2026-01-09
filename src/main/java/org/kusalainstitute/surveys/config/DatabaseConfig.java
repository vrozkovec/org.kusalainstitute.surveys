package org.kusalainstitute.surveys.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Database configuration and connection pool management. Uses HikariCP for connection pooling and
 * Flyway for migrations.
 */
public class DatabaseConfig
{

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);
	private static final String PROPERTIES_FILE = "application.properties";

	private static DatabaseConfig instance;
	private final HikariDataSource dataSource;
	private final Properties properties;

	private DatabaseConfig()
	{
		this.properties = loadProperties();
		this.dataSource = createDataSource();
	}

	/**
	 * Gets the singleton instance of DatabaseConfig.
	 *
	 * @return the DatabaseConfig instance
	 */
	public static synchronized DatabaseConfig getInstance()
	{
		if (instance == null)
		{
			instance = new DatabaseConfig();
		}
		return instance;
	}

	/**
	 * Gets a connection from the pool.
	 *
	 * @return a database connection
	 * @throws SQLException
	 *             if connection cannot be obtained
	 */
	public Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}

	/**
	 * Runs Flyway database migrations.
	 */
	public void runMigrations()
	{
		LOG.info("Running database migrations...");

		Flyway flyway = Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").baselineOnMigrate(true)
			.load();

		flyway.migrate();

		LOG.info("Database migrations completed.");
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
	 * Closes the data source and releases resources.
	 */
	public void close()
	{
		if (dataSource != null && !dataSource.isClosed())
		{
			dataSource.close();
			LOG.info("Database connection pool closed.");
		}
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
			LOG.info("Loaded configuration from {}", PROPERTIES_FILE);
		}
		catch (IOException e)
		{
			LOG.error("Error loading properties file", e);
		}
		return props;
	}

	private HikariDataSource createDataSource()
	{
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(properties.getProperty("db.url"));
		config.setUsername(properties.getProperty("db.username"));
		config.setPassword(properties.getProperty("db.password", ""));

		// Pool settings
		config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.maxSize", "10")));
		config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.pool.minIdle", "2")));
		config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.pool.connectionTimeout", "30000")));

		// MySQL specific settings
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.addDataSourceProperty("useServerPrepStmts", "true");

		LOG.info("Creating database connection pool for: {}", properties.getProperty("db.url"));
		return new HikariDataSource(config);
	}
}
