package org.kusalainstitute.surveys.config;

import java.util.List;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ColumnNameMatcher;
import org.jdbi.v3.core.mapper.reflect.ReflectionMappers;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.kusalainstitute.surveys.service.AnalysisService;
import org.kusalainstitute.surveys.service.ImportService;
import org.kusalainstitute.surveys.service.MatchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Main Guice module for the Surveys application. Configures database connection, JDBI, and
 * services.
 */
public class SurveysModule extends AbstractModule
{

	private static final Logger LOG = LoggerFactory.getLogger(SurveysModule.class);

	@Override
	protected void configure()
	{
		// Bind configuration
		bind(SurveysDatabaseConfig.class).in(Singleton.class);

		// Bind services
		bind(ImportService.class).in(Singleton.class);
		bind(MatchingService.class).in(Singleton.class);
		bind(AnalysisService.class).in(Singleton.class);
	}

	/**
	 * Provides the HikariCP data source.
	 *
	 * @param config
	 *            database configuration
	 * @return configured HikariDataSource
	 */
	@Provides
	@Singleton
	public HikariDataSource provideDataSource(SurveysDatabaseConfig config)
	{
		LOG.info("Creating HikariCP connection pool for: {}", config.getDbConnectionUrl());

		MysqlDataSource mysqlDs = new MysqlDataSource();
		mysqlDs.setUrl(config.getDbConnectionUrl());
		mysqlDs.setUser(config.getDbConfigUsername());
		mysqlDs.setPassword(config.getDbConfigPassword());

		HikariConfig hc = new HikariConfig();
		hc.setDataSource(mysqlDs);
		hc.setMaximumPoolSize(config.getMaxPoolSize());
		hc.setMinimumIdle(config.getMinIdle());
		hc.setConnectionTimeout(config.getConnectionTimeout());

		// MySQL specific settings
		hc.addDataSourceProperty("cachePrepStmts", "true");
		hc.addDataSourceProperty("prepStmtCacheSize", "250");
		hc.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hc.addDataSourceProperty("useServerPrepStmts", "true");

		return new HikariDataSource(hc);
	}

	/**
	 * Provides the JDBI instance configured with SqlObject plugin and bean mappers.
	 *
	 * @param dataSource
	 *            the HikariCP data source
	 * @return configured Jdbi instance
	 */
	@Provides
	@Singleton
	public Jdbi provideJdbi(HikariDataSource dataSource)
	{
		LOG.info("Initializing JDBI instance");

		Jdbi jdbi = Jdbi.create(dataSource);

		// Install SqlObject plugin for DAO interfaces
		jdbi.installPlugin(new SqlObjectPlugin());

		// Configure snake_case to camelCase column mapping
		jdbi.getConfig(ReflectionMappers.class).setColumnNameMatchers(List.of(new SnakeCaseColumnNameMatcher()));

		// Apply custom configuration
		SurveysJdbiConfigurator configurator = new SurveysJdbiConfigurator();
		configurator.onInitialize(jdbi);

		LOG.info("JDBI initialization completed");
		return jdbi;
	}

	/**
	 * Provides Flyway for database migrations.
	 *
	 * @param dataSource
	 *            the HikariCP data source
	 * @return configured Flyway instance
	 */
	@Provides
	@Singleton
	public Flyway provideFlyway(HikariDataSource dataSource)
	{
		return Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").baselineOnMigrate(true).load();
	}

	/**
	 * Column name matcher that converts snake_case database columns to camelCase Java properties.
	 */
	private static class SnakeCaseColumnNameMatcher implements ColumnNameMatcher
	{

		@Override
		public boolean columnNameMatches(String columnName, String javaName)
		{
			// Convert snake_case to camelCase and compare
			String converted = snakeToCamel(columnName);
			return converted.equalsIgnoreCase(javaName);
		}

		@Override
		public boolean columnNameStartsWith(String columnName, String prefix)
		{
			String converted = snakeToCamel(columnName);
			return converted.toLowerCase().startsWith(prefix.toLowerCase());
		}

		private String snakeToCamel(String snake)
		{
			if (snake == null || snake.isEmpty())
			{
				return snake;
			}
			StringBuilder result = new StringBuilder();
			boolean capitalizeNext = false;
			for (char c : snake.toCharArray())
			{
				if (c == '_')
				{
					capitalizeNext = true;
				}
				else if (capitalizeNext)
				{
					result.append(Character.toUpperCase(c));
					capitalizeNext = false;
				}
				else
				{
					result.append(Character.toLowerCase(c));
				}
			}
			return result.toString();
		}
	}
}
