package org.kusalainstitute.surveys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;

/**
 * Data Access Object for Person entities.
 */
public class PersonDao extends BaseDao
{

	private static final String INSERT_SQL = """
		INSERT INTO person (cohort, email, name, normalized_email, requires_manual_match, survey_type, created_at)
		VALUES (?, ?, ?, ?, ?, ?, ?)
		""";

	private static final String SELECT_BY_ID_SQL = """
		SELECT id, cohort, email, name, normalized_email, requires_manual_match, survey_type, created_at
		FROM person WHERE id = ?
		""";

	private static final String SELECT_BY_COHORT_AND_TYPE_SQL = """
		SELECT id, cohort, email, name, normalized_email, requires_manual_match, survey_type, created_at
		FROM person WHERE cohort = ? AND survey_type = ?
		""";

	private static final String SELECT_BY_EMAIL_AND_TYPE_SQL = """
		SELECT id, cohort, email, name, normalized_email, requires_manual_match, survey_type, created_at
		FROM person WHERE normalized_email = ? AND survey_type = ?
		""";

	private static final String SELECT_ALL_BY_TYPE_SQL = """
		SELECT id, cohort, email, name, normalized_email, requires_manual_match, survey_type, created_at
		FROM person WHERE survey_type = ?
		ORDER BY cohort, name
		""";

	private static final String SELECT_UNMATCHED_PRE_SQL = """
		SELECT p.id, p.cohort, p.email, p.name, p.normalized_email, p.requires_manual_match, p.survey_type, p.created_at
		FROM person p
		WHERE p.survey_type = 'PRE'
		AND p.id NOT IN (SELECT pre_person_id FROM person_match)
		ORDER BY p.cohort, p.name
		""";

	private static final String SELECT_UNMATCHED_POST_SQL = """
		SELECT p.id, p.cohort, p.email, p.name, p.normalized_email, p.requires_manual_match, p.survey_type, p.created_at
		FROM person p
		WHERE p.survey_type = 'POST'
		AND p.id NOT IN (SELECT post_person_id FROM person_match)
		ORDER BY p.cohort, p.name
		""";

	/**
	 * Inserts a new person into the database.
	 *
	 * @param person
	 *            the person to insert
	 * @return the inserted person with generated ID
	 * @throws SQLException
	 *             if insertion fails
	 */
	public Person insert(Person person) throws SQLException
	{
		try (Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS))
		{

			stmt.setString(1, person.getCohort());
			setNullableString(stmt, 2, person.getEmail());
			setNullableString(stmt, 3, person.getName());
			setNullableString(stmt, 4, person.getNormalizedEmail());
			stmt.setBoolean(5, person.isRequiresManualMatch());
			stmt.setString(6, person.getSurveyType().name());
			setNullableTimestamp(stmt, 7, person.getCreatedAt());

			stmt.executeUpdate();
			person.setId(getGeneratedKey(stmt));

			log.debug("Inserted person: {}", person);
			return person;
		}
	}

	/**
	 * Finds a person by ID.
	 *
	 * @param id
	 *            the person ID
	 * @return Optional containing the person if found
	 * @throws SQLException
	 *             if query fails
	 */
	public Optional<Person> findById(Long id) throws SQLException
	{
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL))
		{

			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery())
			{
				if (rs.next())
				{
					return Optional.of(mapResultSet(rs));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Finds all persons by cohort and survey type.
	 *
	 * @param cohort
	 *            the cohort code
	 * @param surveyType
	 *            PRE or POST
	 * @return list of matching persons
	 * @throws SQLException
	 *             if query fails
	 */
	public List<Person> findByCohortAndType(String cohort, SurveyType surveyType) throws SQLException
	{
		List<Person> result = new ArrayList<>();
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_COHORT_AND_TYPE_SQL))
		{

			stmt.setString(1, cohort);
			stmt.setString(2, surveyType.name());
			try (ResultSet rs = stmt.executeQuery())
			{
				while (rs.next())
				{
					result.add(mapResultSet(rs));
				}
			}
		}
		return result;
	}

	/**
	 * Finds a person by normalized email and survey type.
	 *
	 * @param normalizedEmail
	 *            the normalized email
	 * @param surveyType
	 *            PRE or POST
	 * @return Optional containing the person if found
	 * @throws SQLException
	 *             if query fails
	 */
	public Optional<Person> findByEmailAndType(String normalizedEmail, SurveyType surveyType) throws SQLException
	{
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_EMAIL_AND_TYPE_SQL))
		{

			stmt.setString(1, normalizedEmail);
			stmt.setString(2, surveyType.name());
			try (ResultSet rs = stmt.executeQuery())
			{
				if (rs.next())
				{
					return Optional.of(mapResultSet(rs));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Finds all persons of a given survey type.
	 *
	 * @param surveyType
	 *            PRE or POST
	 * @return list of all persons with that survey type
	 * @throws SQLException
	 *             if query fails
	 */
	public List<Person> findAllByType(SurveyType surveyType) throws SQLException
	{
		List<Person> result = new ArrayList<>();
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_BY_TYPE_SQL))
		{

			stmt.setString(1, surveyType.name());
			try (ResultSet rs = stmt.executeQuery())
			{
				while (rs.next())
				{
					result.add(mapResultSet(rs));
				}
			}
		}
		return result;
	}

	/**
	 * Finds all pre-survey persons that are not yet matched.
	 *
	 * @return list of unmatched pre-survey persons
	 * @throws SQLException
	 *             if query fails
	 */
	public List<Person> findUnmatchedPre() throws SQLException
	{
		List<Person> result = new ArrayList<>();
		try (Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(SELECT_UNMATCHED_PRE_SQL);
			ResultSet rs = stmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(mapResultSet(rs));
			}
		}
		return result;
	}

	/**
	 * Finds all post-survey persons that are not yet matched.
	 *
	 * @return list of unmatched post-survey persons
	 * @throws SQLException
	 *             if query fails
	 */
	public List<Person> findUnmatchedPost() throws SQLException
	{
		List<Person> result = new ArrayList<>();
		try (Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(SELECT_UNMATCHED_POST_SQL);
			ResultSet rs = stmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(mapResultSet(rs));
			}
		}
		return result;
	}

	/**
	 * Gets all distinct cohort codes from the database.
	 *
	 * @return list of cohort codes
	 * @throws SQLException
	 *             if query fails
	 */
	public List<String> findAllCohorts() throws SQLException
	{
		List<String> result = new ArrayList<>();
		String sql = "SELECT DISTINCT cohort FROM person ORDER BY cohort";
		try (Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(rs.getString("cohort"));
			}
		}
		return result;
	}

	private Person mapResultSet(ResultSet rs) throws SQLException
	{
		Person person = new Person();
		person.setId(rs.getLong("id"));
		person.setCohort(rs.getString("cohort"));
		person.setEmail(getNullableString(rs, "email"));
		person.setName(getNullableString(rs, "name"));
		person.setNormalizedEmail(getNullableString(rs, "normalized_email"));
		person.setRequiresManualMatch(rs.getBoolean("requires_manual_match"));
		person.setSurveyType(SurveyType.valueOf(rs.getString("survey_type")));
		person.setCreatedAt(getNullableTimestamp(rs, "created_at"));
		return person;
	}
}
