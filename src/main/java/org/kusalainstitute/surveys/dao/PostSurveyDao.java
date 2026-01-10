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
import org.kusalainstitute.surveys.pojo.PostSurveyResponse;

/**
 * JDBI DAO interface for PostSurveyResponse entities.
 */
@RegisterBeanMapper(PostSurveyResponse.class)
public interface PostSurveyDao
{

	/**
	 * Inserts a new post-survey response.
	 *
	 * @param response
	 *            the response to insert
	 * @return the generated ID
	 */
	@SqlUpdate("""
		INSERT INTO post_survey_response (
		    person_id, timestamp, source_file, row_num,
		    app_usage_duration, app_time_per_session, app_frequency, progress_assessment,
		    what_helped_most_original, what_helped_most_translated,
		    speak_directions, speak_healthcare, speak_authorities, speak_job_interview,
		    speak_informal, speak_children_education, speak_landlord, speak_social_events,
		    speak_local_services, speak_support_orgs, speak_shopping,
		    difficulty_directions, difficulty_healthcare, difficulty_authorities,
		    difficulty_job_interview, difficulty_informal, difficulty_children_education,
		    difficulty_landlord, difficulty_social_events, difficulty_local_services,
		    difficulty_support_orgs, difficulty_shopping,
		    most_difficult_overall_original, most_difficult_overall_translated,
		    most_difficult_for_job_original, most_difficult_for_job_translated,
		    emotional_difficulties_original, emotional_difficulties_translated,
		    avoided_situations_original, avoided_situations_translated,
		    has_enough_support,
		    desired_resources_original, desired_resources_translated,
		    willing_to_interview, interview_decline_reason_original, interview_decline_reason_translated,
		    preferred_interview_type, contact_info,
		    additional_comments_original, additional_comments_translated,
		    avg_speaking_ability, avg_difficulty_expressing
		) VALUES (
		    :personId, :timestamp, :sourceFile, :rowNumber,
		    :appUsageDuration, :appTimePerSession, :appFrequency, :progressAssessment,
		    :whatHelpedMostOriginal, :whatHelpedMostTranslated,
		    :speakDirections, :speakHealthcare, :speakAuthorities, :speakJobInterview,
		    :speakInformal, :speakChildrenEducation, :speakLandlord, :speakSocialEvents,
		    :speakLocalServices, :speakSupportOrgs, :speakShopping,
		    :difficultyDirections, :difficultyHealthcare, :difficultyAuthorities,
		    :difficultyJobInterview, :difficultyInformal, :difficultyChildrenEducation,
		    :difficultyLandlord, :difficultySocialEvents, :difficultyLocalServices,
		    :difficultySupportOrgs, :difficultyShopping,
		    :mostDifficultOverallOriginal, :mostDifficultOverallTranslated,
		    :mostDifficultForJobOriginal, :mostDifficultForJobTranslated,
		    :emotionalDifficultiesOriginal, :emotionalDifficultiesTranslated,
		    :avoidedSituationsOriginal, :avoidedSituationsTranslated,
		    :hasEnoughSupport,
		    :desiredResourcesOriginal, :desiredResourcesTranslated,
		    :willingToInterview, :interviewDeclineReasonOriginal, :interviewDeclineReasonTranslated,
		    :preferredInterviewType, :contactInfo,
		    :additionalCommentsOriginal, :additionalCommentsTranslated,
		    :avgSpeakingAbility, :avgDifficultyExpressing
		)
		""")
	@GetGeneratedKeys
	long insert(@BindBean PostSurveyResponse response);

	/**
	 * Finds a post-survey response by person ID.
	 *
	 * @param personId
	 *            the person ID
	 * @return Optional containing the response if found
	 */
	@SqlQuery("SELECT * FROM post_survey_response WHERE person_id = :personId")
	Optional<PostSurveyResponse> findByPersonId(@Bind("personId") long personId);

	/**
	 * Finds all post-survey responses.
	 *
	 * @return list of all responses
	 */
	@SqlQuery("SELECT * FROM post_survey_response ORDER BY id")
	List<PostSurveyResponse> findAll();

	/**
	 * Checks if a post-survey response already exists for the given cohort, timestamp, and name or
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
		    SELECT 1 FROM post_survey_response r
		    JOIN person p ON r.person_id = p.id
		    WHERE p.cohort = :cohort
		    AND r.timestamp = :timestamp
		    AND (p.name = :name OR p.normalized_email = :normalizedEmail)
		)
		""")
	boolean exists(String cohort, LocalDateTime timestamp, String name, String normalizedEmail);
}
