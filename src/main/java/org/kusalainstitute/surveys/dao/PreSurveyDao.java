package org.kusalainstitute.surveys.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;

/**
 * JDBI DAO interface for PreSurveyResponse entities.
 */
@RegisterBeanMapper(PreSurveyResponse.class)
public interface PreSurveyDao
{

	/**
	 * Inserts a new pre-survey response.
	 *
	 * @param response
	 *            the response to insert
	 * @return the generated ID
	 */
	@SqlUpdate("""
		INSERT INTO pre_survey_response (
		    person_id, timestamp, source_file, row_num,
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
		) VALUES (
		    :personId, :timestamp, :sourceFile, :rowNumber,
		    :howFoundKusala, :studyWithTeacherDuration, :studyOnOwnDuration, :childrenAges,
		    :mostDifficultThingOriginal, :mostDifficultThingTranslated,
		    :whyImproveEnglishOriginal, :whyImproveEnglishTranslated,
		    :speakDirections, :speakHealthcare, :speakAuthorities, :speakJobInterview,
		    :speakInformal, :speakChildrenEducation, :speakLandlord, :speakSocialEvents,
		    :speakLocalServices, :speakSupportOrgs, :speakShopping,
		    :otherSituationsOriginal, :otherSituationsTranslated,
		    :understandDirections, :understandHealthcare, :understandAuthorities, :understandJobInterview,
		    :understandInformal, :understandChildrenEducation, :understandLandlord, :understandSocialEvents,
		    :understandLocalServices, :understandSupportOrgs, :understandShopping,
		    :difficultPartOriginal, :difficultPartTranslated,
		    :describeSituationsOriginal, :describeSituationsTranslated,
		    :avgSpeakingConfidence, :avgUnderstandingConfidence
		)
		""")
	@GetGeneratedKeys
	long insert(@BindBean PreSurveyResponse response);

	/**
	 * Finds a pre-survey response by person ID.
	 *
	 * @param personId
	 *            the person ID
	 * @return Optional containing the response if found
	 */
	@SqlQuery("SELECT * FROM pre_survey_response WHERE person_id = :personId")
	Optional<PreSurveyResponse> findByPersonId(@Bind("personId") long personId);

	/**
	 * Finds all pre-survey responses.
	 *
	 * @return list of all responses
	 */
	@SqlQuery("SELECT * FROM pre_survey_response ORDER BY id")
	List<PreSurveyResponse> findAll();

	/**
	 * Updates only the translated fields of a pre-survey response.
	 *
	 * @param response
	 *            the response with translations to update
	 */
	@SqlUpdate("""
		UPDATE pre_survey_response SET
		    most_difficult_thing_translated = :mostDifficultThingTranslated,
		    why_improve_english_translated = :whyImproveEnglishTranslated,
		    other_situations_translated = :otherSituationsTranslated,
		    difficult_part_translated = :difficultPartTranslated,
		    describe_situations_translated = :describeSituationsTranslated
		WHERE id = :id
		""")
	void updateTranslations(@BindBean PreSurveyResponse response);

	/**
	 * Checks if a pre-survey response already exists for the given cohort, timestamp, and name or
	 * email combination.
	 *
	 * @param cohort
	 *            the cohort code
	 * @param timestamp
	 *            the survey response timestamp
	 * @param name
	 *            the respondent's name
	 * @param normalizedEmail
	 *            the normalized email address
	 * @return true if a matching record exists
	 */
	@SqlQuery("""
		SELECT EXISTS(
		    SELECT 1 FROM pre_survey_response r
		    JOIN person p ON r.person_id = p.id
		    WHERE p.cohort = :cohort
		    AND r.timestamp = :timestamp
		    AND (p.name = :name OR p.normalized_email = :normalizedEmail)
		)
		""")
	boolean exists(String cohort, LocalDateTime timestamp, String name, String normalizedEmail);
}
