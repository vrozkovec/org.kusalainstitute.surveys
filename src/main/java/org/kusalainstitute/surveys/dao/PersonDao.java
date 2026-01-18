package org.kusalainstitute.surveys.dao;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;

/**
 * JDBI DAO interface for Person entities.
 */
@RegisterBeanMapper(Person.class)
public interface PersonDao
{

	/**
	 * Inserts a new person into the database.
	 *
	 * @param person
	 *            the person to insert
	 * @return the generated ID
	 */
	@SqlUpdate("""
		INSERT INTO person (cohort, email, name, normalized_email, requires_manual_match, survey_type, created_at)
		VALUES (:cohort, :email, :name, :normalizedEmail, :requiresManualMatch, :surveyType, :createdAt)
		""")
	@GetGeneratedKeys
	long insert(@BindBean Person person);

	/**
	 * Finds a person by ID.
	 *
	 * @param id
	 *            the person ID
	 * @return Optional containing the person if found
	 */
	@SqlQuery("SELECT * FROM person WHERE id = :id")
	Optional<Person> findById(@Bind("id") long id);

	/**
	 * Finds all persons by cohort and survey type.
	 *
	 * @param cohort
	 *            the cohort code
	 * @param surveyType
	 *            PRE or POST
	 * @return list of matching persons
	 */
	@SqlQuery("SELECT * FROM person WHERE cohort = :cohort AND survey_type = :surveyType")
	List<Person> findByCohortAndType(@Bind("cohort") String cohort, @Bind("surveyType") SurveyType surveyType);

	/**
	 * Finds a person by normalized email and survey type.
	 *
	 * @param normalizedEmail
	 *            the normalized email
	 * @param surveyType
	 *            PRE or POST
	 * @return Optional containing the person if found
	 */
	@SqlQuery("SELECT * FROM person WHERE normalized_email = :normalizedEmail AND survey_type = :surveyType")
	Optional<Person> findByEmailAndType(@Bind("normalizedEmail") String normalizedEmail,
		@Bind("surveyType") SurveyType surveyType);

	/**
	 * Finds all persons of a given survey type.
	 *
	 * @param surveyType
	 *            PRE or POST
	 * @return list of all persons with that survey type
	 */
	@SqlQuery("SELECT * FROM person WHERE survey_type = :surveyType ORDER BY cohort, name")
	List<Person> findAllByType(@Bind("surveyType") SurveyType surveyType);

	/**
	 * Finds all pre-survey persons that are not yet matched.
	 *
	 * @return list of unmatched pre-survey persons
	 */
	@SqlQuery("""
		SELECT * FROM person
		WHERE survey_type = 'PRE'
		AND id NOT IN (SELECT pre_person_id FROM person_match)
		ORDER BY cohort, name
		""")
	List<Person> findUnmatchedPre();

	/**
	 * Finds all post-survey persons that are not yet matched.
	 *
	 * @return list of unmatched post-survey persons
	 */
	@SqlQuery("""
		SELECT * FROM person
		WHERE survey_type = 'POST'
		AND id NOT IN (SELECT post_person_id FROM person_match)
		ORDER BY cohort, name
		""")
	List<Person> findUnmatchedPost();

	/**
	 * Gets all distinct cohort codes from the database.
	 *
	 * @return list of cohort codes
	 */
	@SqlQuery("SELECT DISTINCT cohort FROM person ORDER BY cohort")
	List<String> findAllCohorts();

	/**
	 * Gets all distinct cohort codes from POST survey persons only. PRE survey cohorts contain
	 * incorrect "all?" values due to initial survey collection mistake, so we use POST person's
	 * cohort which has correct values.
	 *
	 * @return list of cohort codes from POST survey persons
	 */
	@SqlQuery("SELECT DISTINCT cohort FROM person WHERE survey_type = 'POST' ORDER BY cohort")
	List<String> findAllPostCohorts();

	/**
	 * Finds a person by composite key (cohort, email, name, and survey type) joined with survey
	 * response timestamp. This is used to restore manual matches after database rebuilds.
	 *
	 * @param cohort
	 *            the cohort code
	 * @param normalizedEmail
	 *            the normalized email address
	 * @param name
	 *            the person's name
	 * @param surveyType
	 *            PRE or POST survey type
	 * @param timestamp
	 *            the survey response timestamp
	 * @return Optional containing the person if found
	 */
	@SqlQuery("""
		SELECT p.* FROM person p
		LEFT JOIN pre_survey_response pre ON p.id = pre.person_id AND p.survey_type = 'PRE'
		LEFT JOIN post_survey_response post ON p.id = post.person_id AND p.survey_type = 'POST'
		WHERE p.survey_type = :surveyType
		AND (p.normalized_email = :normalizedEmail OR p.name = :name)
		AND (
		    (p.survey_type = 'PRE' AND pre.timestamp = :timestamp)
		    OR (p.survey_type = 'POST' AND post.timestamp = :timestamp)
		)
		LIMIT 1
		""")
	Optional<Person> findByCompositeKey(
		@Bind("normalizedEmail") String normalizedEmail,
		@Bind("name") String name,
		@Bind("surveyType") SurveyType surveyType,
		@Bind("timestamp") java.time.LocalDateTime timestamp);
}
