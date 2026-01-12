package org.kusalainstitute.surveys.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Types;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.SqlStatements;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.PersonMatch;
import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;
import org.kusalainstitute.surveys.pojo.TranslationCache;
import org.kusalainstitute.surveys.pojo.enums.AppFrequency;
import org.kusalainstitute.surveys.pojo.enums.AppTimePerSession;
import org.kusalainstitute.surveys.pojo.enums.ChildrenAgeGroup;
import org.kusalainstitute.surveys.pojo.enums.HowFoundKusala;
import org.kusalainstitute.surveys.pojo.enums.InterviewTypePreference;
import org.kusalainstitute.surveys.pojo.enums.MatchType;
import org.kusalainstitute.surveys.pojo.enums.ProgressAssessment;
import org.kusalainstitute.surveys.pojo.enums.StudyDuration;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;
import org.kusalainstitute.surveys.pojo.enums.YesNo;
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

		// PRE survey enums - use name() since enum names are the codes (A, B, C, etc.)
		jdbi.registerArgument(new EnumByNameArgumentFactory<>(StudyDuration.class));
		jdbi.registerArgument(new EnumByNameArgumentFactory<>(HowFoundKusala.class));
		jdbi.registerArgument(new EnumByNameArgumentFactory<>(YesNo.class));
		jdbi.registerArgument(new EnumByNameArgumentFactory<>(AppTimePerSession.class));
		jdbi.registerArgument(new EnumByNameArgumentFactory<>(InterviewTypePreference.class));

		// POST survey enums - use getCodeString() since enum names differ from codes
		jdbi.registerArgument(new EnumByNameArgumentFactory<>(AppFrequency.class, AppFrequency::getCodeString));
		jdbi.registerArgument(new EnumByNameArgumentFactory<>(ProgressAssessment.class, ProgressAssessment::getCodeString));

		// Set<ChildrenAgeGroup> argument factory - converts to comma-separated codes
		jdbi.registerArgument(new SetChildrenAgeGroupArgumentFactory());

		// Register enum column mappers
		jdbi.registerColumnMapper(SurveyType.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return value != null ? SurveyType.valueOf(value) : null;
		});
		jdbi.registerColumnMapper(MatchType.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return value != null ? MatchType.valueOf(value) : null;
		});

		// PRE survey enum column mappers
		jdbi.registerColumnMapper(StudyDuration.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return StudyDuration.fromCode(value);
		});
		jdbi.registerColumnMapper(HowFoundKusala.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return HowFoundKusala.fromCode(value);
		});
		jdbi.registerColumnMapper(YesNo.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return YesNo.fromCode(value);
		});
		jdbi.registerColumnMapper(AppTimePerSession.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return AppTimePerSession.fromCode(value);
		});
		jdbi.registerColumnMapper(InterviewTypePreference.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return InterviewTypePreference.fromCode(value);
		});

		// POST survey enum column mappers - use fromCodeString since codes are numeric
		jdbi.registerColumnMapper(AppFrequency.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return AppFrequency.fromCodeString(value);
		});
		jdbi.registerColumnMapper(ProgressAssessment.class, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return ProgressAssessment.fromCodeString(value);
		});

		// Set<ChildrenAgeGroup> column mapper - stored as comma-separated codes
		jdbi.registerColumnMapper(new org.jdbi.v3.core.generic.GenericType<Set<ChildrenAgeGroup>>() {}, (rs, col, ctx) -> {
			String value = rs.getString(col);
			return ChildrenAgeGroup.fromCodes(value);
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

	/**
	 * JDBI argument factory for Set&lt;ChildrenAgeGroup&gt; that converts the set to a comma-separated
	 * string of codes for database storage.
	 */
	private static class SetChildrenAgeGroupArgumentFactory implements ArgumentFactory.Preparable
	{

		@Override
		public Optional<Function<Object, Argument>> prepare(Type type, ConfigRegistry config)
		{
			if (type instanceof ParameterizedType pt
				&& pt.getRawType() == Set.class
				&& pt.getActualTypeArguments().length == 1
				&& pt.getActualTypeArguments()[0] == ChildrenAgeGroup.class)
			{
				return Optional.of(value -> {
					if (value == null)
					{
						return (position, statement, ctx) -> statement.setNull(position, Types.VARCHAR);
					}
					@SuppressWarnings("unchecked")
					Set<ChildrenAgeGroup> set = (Set<ChildrenAgeGroup>)value;
					String codes = ChildrenAgeGroup.toCodes(set);
					return (position, statement, ctx) -> statement.setString(position, codes);
				});
			}
			return Optional.empty();
		}
	}
}
