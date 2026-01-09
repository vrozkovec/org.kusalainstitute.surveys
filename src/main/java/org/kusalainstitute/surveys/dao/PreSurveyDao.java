package org.kusalainstitute.surveys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.kusalainstitute.surveys.pojo.PreSurveyResponse;

/**
 * Data Access Object for PreSurveyResponse entities.
 */
public class PreSurveyDao extends BaseDao
{

	private static final String INSERT_SQL = """
		INSERT INTO pre_survey_response (
		    person_id, timestamp, source_file, row_number,
		    how_found_kusala, study_with_teacher_duration, study_on_own_duration, children_ages,
		    most_difficult_thing_original, most_difficult_thing_translated,
		    why_improve_english_original, why_improve_english_translated,
		    speak_directions, speak_healthcare, speak_authorities, speak_job_interview,
		    speak_informal, speak_children_education, speak_landlord, speak_social_events,
		    speak_local_services, speak_support_orgs, speak_shopping,
		    other_situations_original, other_situations_translated,
		    understand_directions, understand_healthcare, understand_authorities, understand_job_interview,
		    understand_informal, understand_children_education, understand_landlord, understand_social_events,
		    understand_local_services, understand_support_orgs, understand_shopping,
		    difficult_part_original, difficult_part_translated,
		    describe_situations_original, describe_situations_translated,
		    avg_speaking_confidence, avg_understanding_confidence
		) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
		""";

	private static final String SELECT_BY_PERSON_ID_SQL = """
		SELECT * FROM pre_survey_response WHERE person_id = ?
		""";

	private static final String SELECT_ALL_SQL = """
		SELECT * FROM pre_survey_response ORDER BY id
		""";

	private static final String UPDATE_TRANSLATIONS_SQL = """
		UPDATE pre_survey_response SET
		    most_difficult_thing_translated = ?,
		    why_improve_english_translated = ?,
		    other_situations_translated = ?,
		    difficult_part_translated = ?,
		    describe_situations_translated = ?
		WHERE id = ?
		""";

	/**
	 * Inserts a new pre-survey response.
	 *
	 * @param response
	 *            the response to insert
	 * @return the inserted response with generated ID
	 * @throws SQLException
	 *             if insertion fails
	 */
	public PreSurveyResponse insert(PreSurveyResponse response) throws SQLException
	{
		try (Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS))
		{

			int idx = 1;
			stmt.setLong(idx++, response.getPersonId());
			setNullableTimestamp(stmt, idx++, response.getTimestamp());
			setNullableString(stmt, idx++, response.getSourceFile());
			setNullableInt(stmt, idx++, response.getRowNumber());

			// Demographics
			setNullableString(stmt, idx++, response.getHowFoundKusala());
			setNullableString(stmt, idx++, response.getStudyWithTeacherDuration());
			setNullableString(stmt, idx++, response.getStudyOnOwnDuration());
			setNullableString(stmt, idx++, response.getChildrenAges());
			setNullableString(stmt, idx++, response.getMostDifficultThingOriginal());
			setNullableString(stmt, idx++, response.getMostDifficultThingTranslated());
			setNullableString(stmt, idx++, response.getWhyImproveEnglishOriginal());
			setNullableString(stmt, idx++, response.getWhyImproveEnglishTranslated());

			// Speaking confidence
			setNullableInt(stmt, idx++, response.getSpeakDirections());
			setNullableInt(stmt, idx++, response.getSpeakHealthcare());
			setNullableInt(stmt, idx++, response.getSpeakAuthorities());
			setNullableInt(stmt, idx++, response.getSpeakJobInterview());
			setNullableInt(stmt, idx++, response.getSpeakInformal());
			setNullableInt(stmt, idx++, response.getSpeakChildrenEducation());
			setNullableInt(stmt, idx++, response.getSpeakLandlord());
			setNullableInt(stmt, idx++, response.getSpeakSocialEvents());
			setNullableInt(stmt, idx++, response.getSpeakLocalServices());
			setNullableInt(stmt, idx++, response.getSpeakSupportOrgs());
			setNullableInt(stmt, idx++, response.getSpeakShopping());

			// Other situations
			setNullableString(stmt, idx++, response.getOtherSituationsOriginal());
			setNullableString(stmt, idx++, response.getOtherSituationsTranslated());

			// Understanding confidence
			setNullableInt(stmt, idx++, response.getUnderstandDirections());
			setNullableInt(stmt, idx++, response.getUnderstandHealthcare());
			setNullableInt(stmt, idx++, response.getUnderstandAuthorities());
			setNullableInt(stmt, idx++, response.getUnderstandJobInterview());
			setNullableInt(stmt, idx++, response.getUnderstandInformal());
			setNullableInt(stmt, idx++, response.getUnderstandChildrenEducation());
			setNullableInt(stmt, idx++, response.getUnderstandLandlord());
			setNullableInt(stmt, idx++, response.getUnderstandSocialEvents());
			setNullableInt(stmt, idx++, response.getUnderstandLocalServices());
			setNullableInt(stmt, idx++, response.getUnderstandSupportOrgs());
			setNullableInt(stmt, idx++, response.getUnderstandShopping());

			// Difficult part
			setNullableString(stmt, idx++, response.getDifficultPartOriginal());
			setNullableString(stmt, idx++, response.getDifficultPartTranslated());

			// Describe situations
			setNullableString(stmt, idx++, response.getDescribeSituationsOriginal());
			setNullableString(stmt, idx++, response.getDescribeSituationsTranslated());

			// Averages
			setNullableBigDecimal(stmt, idx++, response.getAvgSpeakingConfidence());
			setNullableBigDecimal(stmt, idx++, response.getAvgUnderstandingConfidence());

			stmt.executeUpdate();
			response.setId(getGeneratedKey(stmt));

			log.debug("Inserted pre-survey response: {}", response);
			return response;
		}
	}

	/**
	 * Finds a pre-survey response by person ID.
	 *
	 * @param personId
	 *            the person ID
	 * @return Optional containing the response if found
	 * @throws SQLException
	 *             if query fails
	 */
	public Optional<PreSurveyResponse> findByPersonId(Long personId) throws SQLException
	{
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_PERSON_ID_SQL))
		{

			stmt.setLong(1, personId);
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
	 * Finds all pre-survey responses.
	 *
	 * @return list of all responses
	 * @throws SQLException
	 *             if query fails
	 */
	public List<PreSurveyResponse> findAll() throws SQLException
	{
		List<PreSurveyResponse> result = new ArrayList<>();
		try (Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
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
	 * Updates only the translated fields of a pre-survey response.
	 *
	 * @param response
	 *            the response with translations to update
	 * @throws SQLException
	 *             if update fails
	 */
	public void updateTranslations(PreSurveyResponse response) throws SQLException
	{
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_TRANSLATIONS_SQL))
		{

			setNullableString(stmt, 1, response.getMostDifficultThingTranslated());
			setNullableString(stmt, 2, response.getWhyImproveEnglishTranslated());
			setNullableString(stmt, 3, response.getOtherSituationsTranslated());
			setNullableString(stmt, 4, response.getDifficultPartTranslated());
			setNullableString(stmt, 5, response.getDescribeSituationsTranslated());
			stmt.setLong(6, response.getId());

			stmt.executeUpdate();
			log.debug("Updated translations for pre-survey response: {}", response.getId());
		}
	}

	private PreSurveyResponse mapResultSet(ResultSet rs) throws SQLException
	{
		PreSurveyResponse response = new PreSurveyResponse();
		response.setId(rs.getLong("id"));
		response.setPersonId(rs.getLong("person_id"));
		response.setTimestamp(getNullableTimestamp(rs, "timestamp"));
		response.setSourceFile(getNullableString(rs, "source_file"));
		response.setRowNumber(getNullableInt(rs, "row_number"));

		// Demographics
		response.setHowFoundKusala(getNullableString(rs, "how_found_kusala"));
		response.setStudyWithTeacherDuration(getNullableString(rs, "study_with_teacher_duration"));
		response.setStudyOnOwnDuration(getNullableString(rs, "study_on_own_duration"));
		response.setChildrenAges(getNullableString(rs, "children_ages"));
		response.setMostDifficultThingOriginal(getNullableString(rs, "most_difficult_thing_original"));
		response.setMostDifficultThingTranslated(getNullableString(rs, "most_difficult_thing_translated"));
		response.setWhyImproveEnglishOriginal(getNullableString(rs, "why_improve_english_original"));
		response.setWhyImproveEnglishTranslated(getNullableString(rs, "why_improve_english_translated"));

		// Speaking confidence
		response.setSpeakDirections(getNullableInt(rs, "speak_directions"));
		response.setSpeakHealthcare(getNullableInt(rs, "speak_healthcare"));
		response.setSpeakAuthorities(getNullableInt(rs, "speak_authorities"));
		response.setSpeakJobInterview(getNullableInt(rs, "speak_job_interview"));
		response.setSpeakInformal(getNullableInt(rs, "speak_informal"));
		response.setSpeakChildrenEducation(getNullableInt(rs, "speak_children_education"));
		response.setSpeakLandlord(getNullableInt(rs, "speak_landlord"));
		response.setSpeakSocialEvents(getNullableInt(rs, "speak_social_events"));
		response.setSpeakLocalServices(getNullableInt(rs, "speak_local_services"));
		response.setSpeakSupportOrgs(getNullableInt(rs, "speak_support_orgs"));
		response.setSpeakShopping(getNullableInt(rs, "speak_shopping"));

		// Other situations
		response.setOtherSituationsOriginal(getNullableString(rs, "other_situations_original"));
		response.setOtherSituationsTranslated(getNullableString(rs, "other_situations_translated"));

		// Understanding confidence
		response.setUnderstandDirections(getNullableInt(rs, "understand_directions"));
		response.setUnderstandHealthcare(getNullableInt(rs, "understand_healthcare"));
		response.setUnderstandAuthorities(getNullableInt(rs, "understand_authorities"));
		response.setUnderstandJobInterview(getNullableInt(rs, "understand_job_interview"));
		response.setUnderstandInformal(getNullableInt(rs, "understand_informal"));
		response.setUnderstandChildrenEducation(getNullableInt(rs, "understand_children_education"));
		response.setUnderstandLandlord(getNullableInt(rs, "understand_landlord"));
		response.setUnderstandSocialEvents(getNullableInt(rs, "understand_social_events"));
		response.setUnderstandLocalServices(getNullableInt(rs, "understand_local_services"));
		response.setUnderstandSupportOrgs(getNullableInt(rs, "understand_support_orgs"));
		response.setUnderstandShopping(getNullableInt(rs, "understand_shopping"));

		// Difficult part
		response.setDifficultPartOriginal(getNullableString(rs, "difficult_part_original"));
		response.setDifficultPartTranslated(getNullableString(rs, "difficult_part_translated"));

		// Describe situations
		response.setDescribeSituationsOriginal(getNullableString(rs, "describe_situations_original"));
		response.setDescribeSituationsTranslated(getNullableString(rs, "describe_situations_translated"));

		// Averages
		response.setAvgSpeakingConfidence(rs.getBigDecimal("avg_speaking_confidence"));
		response.setAvgUnderstandingConfidence(rs.getBigDecimal("avg_understanding_confidence"));

		return response;
	}
}
