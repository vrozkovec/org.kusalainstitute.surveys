package org.kusalainstitute.surveys.config;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.SqlStatements;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.PersonMatch;
import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;
import org.kusalainstitute.surveys.pojo.TranslationCache;
import org.kusalainstitute.surveys.pojo.enums.MatchType;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;

/**
 * Custom JDBI configurator for the Surveys application. Registers bean mappers and configures
 * column name mapping from snake_case to camelCase.
 */
public class SurveysJdbiConfigurator implements ICustomJdbiConfigurator
{

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(SurveysJdbiConfigurator.class);

	@Override
	public void onInitialize(Jdbi jdbi)
	{
		LOG.info("Initializing JDBI with custom configuration");

		// Install SqlObject plugin for DAO interfaces
		jdbi.installPlugin(new SqlObjectPlugin());

		// Configure underscore to camelCase column mapping
		jdbi.getConfig(SqlStatements.class).setUnusedBindingAllowed(true);

		// Register bean mappers for all entities
		jdbi.registerRowMapper(BeanMapper.factory(Person.class));
		jdbi.registerRowMapper(BeanMapper.factory(PreSurveyResponse.class));
		jdbi.registerRowMapper(BeanMapper.factory(PostSurveyResponse.class));
		jdbi.registerRowMapper(BeanMapper.factory(PersonMatch.class));
		jdbi.registerRowMapper(BeanMapper.factory(TranslationCache.class));

		// Register enum argument factories
		jdbi.registerArgument(new EnumByNameArgumentFactory<>(SurveyType.class));
		jdbi.registerArgument(new EnumByNameArgumentFactory<>(MatchType.class));

		// Register enum column mappers
		jdbi.registerColumnMapper(SurveyType.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return value != null ? SurveyType.valueOf(value) : null;
		});
		jdbi.registerColumnMapper(MatchType.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return value != null ? MatchType.valueOf(value) : null;
		});

		LOG.info("JDBI configuration completed");
	}

	@Override
	public void configurePool(HikariConfig hc)
	{
		// Additional pool configuration can be done here
		hc.addDataSourceProperty("cachePrepStmts", "true");
		hc.addDataSourceProperty("prepStmtCacheSize", "250");
		hc.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hc.addDataSourceProperty("useServerPrepStmts", "true");
	}
}
