package org.kusalainstitute.surveys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.kusalainstitute.surveys.pojo.PostSurveyResponse;

/**
 * Data Access Object for PostSurveyResponse entities.
 */
public class PostSurveyDao extends BaseDao {

    private static final String INSERT_SQL = """
            INSERT INTO post_survey_response (
                person_id, timestamp, source_file, row_number,
                app_usage_duration, app_time_per_session, app_frequency, progress_assessment,
                what_helped_most_original, what_helped_most_translated,
                speak_directions, speak_healthcare, speak_authorities, speak_job_interview,
                speak_informal, speak_children_education, speak_landlord, speak_social_events,
                speak_local_services, speak_support_orgs, speak_shopping,
                difficulty_directions_original, difficulty_directions_translated,
                difficulty_healthcare_original, difficulty_healthcare_translated,
                difficulty_authorities_original, difficulty_authorities_translated,
                difficulty_job_interview_original, difficulty_job_interview_translated,
                difficulty_informal_original, difficulty_informal_translated,
                difficulty_children_education_original, difficulty_children_education_translated,
                difficulty_landlord_original, difficulty_landlord_translated,
                difficulty_social_events_original, difficulty_social_events_translated,
                difficulty_local_services_original, difficulty_local_services_translated,
                difficulty_support_orgs_original, difficulty_support_orgs_translated,
                difficulty_shopping_original, difficulty_shopping_translated,
                most_difficult_overall_original, most_difficult_overall_translated,
                most_difficult_for_job_original, most_difficult_for_job_translated,
                emotional_difficulties_original, emotional_difficulties_translated,
                avoided_situations_original, avoided_situations_translated,
                has_enough_support,
                desired_resources_original, desired_resources_translated,
                willing_to_interview, interview_decline_reason_original, interview_decline_reason_translated,
                preferred_interview_type, contact_info,
                additional_comments_original, additional_comments_translated,
                avg_speaking_ability
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_PERSON_ID_SQL = """
            SELECT * FROM post_survey_response WHERE person_id = ?
            """;

    private static final String SELECT_ALL_SQL = """
            SELECT * FROM post_survey_response ORDER BY id
            """;

    /**
     * Inserts a new post-survey response.
     *
     * @param response the response to insert
     * @return the inserted response with generated ID
     * @throws SQLException if insertion fails
     */
    public PostSurveyResponse insert(PostSurveyResponse response) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            int idx = 1;
            stmt.setLong(idx++, response.getPersonId());
            setNullableTimestamp(stmt, idx++, response.getTimestamp());
            setNullableString(stmt, idx++, response.getSourceFile());
            setNullableInt(stmt, idx++, response.getRowNumber());

            // App usage
            setNullableString(stmt, idx++, response.getAppUsageDuration());
            setNullableString(stmt, idx++, response.getAppTimePerSession());
            setNullableString(stmt, idx++, response.getAppFrequency());
            setNullableString(stmt, idx++, response.getProgressAssessment());
            setNullableString(stmt, idx++, response.getWhatHelpedMostOriginal());
            setNullableString(stmt, idx++, response.getWhatHelpedMostTranslated());

            // Speaking ability
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

            // Difficulty fields (11 pairs)
            setNullableString(stmt, idx++, response.getDifficultyDirectionsOriginal());
            setNullableString(stmt, idx++, response.getDifficultyDirectionsTranslated());
            setNullableString(stmt, idx++, response.getDifficultyHealthcareOriginal());
            setNullableString(stmt, idx++, response.getDifficultyHealthcareTranslated());
            setNullableString(stmt, idx++, response.getDifficultyAuthoritiesOriginal());
            setNullableString(stmt, idx++, response.getDifficultyAuthoritiesTranslated());
            setNullableString(stmt, idx++, response.getDifficultyJobInterviewOriginal());
            setNullableString(stmt, idx++, response.getDifficultyJobInterviewTranslated());
            setNullableString(stmt, idx++, response.getDifficultyInformalOriginal());
            setNullableString(stmt, idx++, response.getDifficultyInformalTranslated());
            setNullableString(stmt, idx++, response.getDifficultyChildrenEducationOriginal());
            setNullableString(stmt, idx++, response.getDifficultyChildrenEducationTranslated());
            setNullableString(stmt, idx++, response.getDifficultyLandlordOriginal());
            setNullableString(stmt, idx++, response.getDifficultyLandlordTranslated());
            setNullableString(stmt, idx++, response.getDifficultySocialEventsOriginal());
            setNullableString(stmt, idx++, response.getDifficultySocialEventsTranslated());
            setNullableString(stmt, idx++, response.getDifficultyLocalServicesOriginal());
            setNullableString(stmt, idx++, response.getDifficultyLocalServicesTranslated());
            setNullableString(stmt, idx++, response.getDifficultySupportOrgsOriginal());
            setNullableString(stmt, idx++, response.getDifficultySupportOrgsTranslated());
            setNullableString(stmt, idx++, response.getDifficultyShoppingOriginal());
            setNullableString(stmt, idx++, response.getDifficultyShoppingTranslated());

            // Other free text
            setNullableString(stmt, idx++, response.getMostDifficultOverallOriginal());
            setNullableString(stmt, idx++, response.getMostDifficultOverallTranslated());
            setNullableString(stmt, idx++, response.getMostDifficultForJobOriginal());
            setNullableString(stmt, idx++, response.getMostDifficultForJobTranslated());
            setNullableString(stmt, idx++, response.getEmotionalDifficultiesOriginal());
            setNullableString(stmt, idx++, response.getEmotionalDifficultiesTranslated());
            setNullableString(stmt, idx++, response.getAvoidedSituationsOriginal());
            setNullableString(stmt, idx++, response.getAvoidedSituationsTranslated());

            // Support
            setNullableString(stmt, idx++, response.getHasEnoughSupport());
            setNullableString(stmt, idx++, response.getDesiredResourcesOriginal());
            setNullableString(stmt, idx++, response.getDesiredResourcesTranslated());

            // Interview
            setNullableString(stmt, idx++, response.getWillingToInterview());
            setNullableString(stmt, idx++, response.getInterviewDeclineReasonOriginal());
            setNullableString(stmt, idx++, response.getInterviewDeclineReasonTranslated());
            setNullableString(stmt, idx++, response.getPreferredInterviewType());
            setNullableString(stmt, idx++, response.getContactInfo());

            // Additional comments
            setNullableString(stmt, idx++, response.getAdditionalCommentsOriginal());
            setNullableString(stmt, idx++, response.getAdditionalCommentsTranslated());

            // Average
            setNullableBigDecimal(stmt, idx++, response.getAvgSpeakingAbility());

            stmt.executeUpdate();
            response.setId(getGeneratedKey(stmt));

            log.debug("Inserted post-survey response: {}", response);
            return response;
        }
    }

    /**
     * Finds a post-survey response by person ID.
     *
     * @param personId the person ID
     * @return Optional containing the response if found
     * @throws SQLException if query fails
     */
    public Optional<PostSurveyResponse> findByPersonId(Long personId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_PERSON_ID_SQL)) {

            stmt.setLong(1, personId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds all post-survey responses.
     *
     * @return list of all responses
     * @throws SQLException if query fails
     */
    public List<PostSurveyResponse> findAll() throws SQLException {
        List<PostSurveyResponse> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        }
        return result;
    }

    private PostSurveyResponse mapResultSet(ResultSet rs) throws SQLException {
        PostSurveyResponse response = new PostSurveyResponse();
        response.setId(rs.getLong("id"));
        response.setPersonId(rs.getLong("person_id"));
        response.setTimestamp(getNullableTimestamp(rs, "timestamp"));
        response.setSourceFile(getNullableString(rs, "source_file"));
        response.setRowNumber(getNullableInt(rs, "row_number"));

        // App usage
        response.setAppUsageDuration(getNullableString(rs, "app_usage_duration"));
        response.setAppTimePerSession(getNullableString(rs, "app_time_per_session"));
        response.setAppFrequency(getNullableString(rs, "app_frequency"));
        response.setProgressAssessment(getNullableString(rs, "progress_assessment"));
        response.setWhatHelpedMostOriginal(getNullableString(rs, "what_helped_most_original"));
        response.setWhatHelpedMostTranslated(getNullableString(rs, "what_helped_most_translated"));

        // Speaking ability
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

        // Difficulty fields
        response.setDifficultyDirectionsOriginal(getNullableString(rs, "difficulty_directions_original"));
        response.setDifficultyDirectionsTranslated(getNullableString(rs, "difficulty_directions_translated"));
        response.setDifficultyHealthcareOriginal(getNullableString(rs, "difficulty_healthcare_original"));
        response.setDifficultyHealthcareTranslated(getNullableString(rs, "difficulty_healthcare_translated"));
        response.setDifficultyAuthoritiesOriginal(getNullableString(rs, "difficulty_authorities_original"));
        response.setDifficultyAuthoritiesTranslated(getNullableString(rs, "difficulty_authorities_translated"));
        response.setDifficultyJobInterviewOriginal(getNullableString(rs, "difficulty_job_interview_original"));
        response.setDifficultyJobInterviewTranslated(getNullableString(rs, "difficulty_job_interview_translated"));
        response.setDifficultyInformalOriginal(getNullableString(rs, "difficulty_informal_original"));
        response.setDifficultyInformalTranslated(getNullableString(rs, "difficulty_informal_translated"));
        response.setDifficultyChildrenEducationOriginal(getNullableString(rs, "difficulty_children_education_original"));
        response.setDifficultyChildrenEducationTranslated(getNullableString(rs, "difficulty_children_education_translated"));
        response.setDifficultyLandlordOriginal(getNullableString(rs, "difficulty_landlord_original"));
        response.setDifficultyLandlordTranslated(getNullableString(rs, "difficulty_landlord_translated"));
        response.setDifficultySocialEventsOriginal(getNullableString(rs, "difficulty_social_events_original"));
        response.setDifficultySocialEventsTranslated(getNullableString(rs, "difficulty_social_events_translated"));
        response.setDifficultyLocalServicesOriginal(getNullableString(rs, "difficulty_local_services_original"));
        response.setDifficultyLocalServicesTranslated(getNullableString(rs, "difficulty_local_services_translated"));
        response.setDifficultySupportOrgsOriginal(getNullableString(rs, "difficulty_support_orgs_original"));
        response.setDifficultySupportOrgsTranslated(getNullableString(rs, "difficulty_support_orgs_translated"));
        response.setDifficultyShoppingOriginal(getNullableString(rs, "difficulty_shopping_original"));
        response.setDifficultyShoppingTranslated(getNullableString(rs, "difficulty_shopping_translated"));

        // Other free text
        response.setMostDifficultOverallOriginal(getNullableString(rs, "most_difficult_overall_original"));
        response.setMostDifficultOverallTranslated(getNullableString(rs, "most_difficult_overall_translated"));
        response.setMostDifficultForJobOriginal(getNullableString(rs, "most_difficult_for_job_original"));
        response.setMostDifficultForJobTranslated(getNullableString(rs, "most_difficult_for_job_translated"));
        response.setEmotionalDifficultiesOriginal(getNullableString(rs, "emotional_difficulties_original"));
        response.setEmotionalDifficultiesTranslated(getNullableString(rs, "emotional_difficulties_translated"));
        response.setAvoidedSituationsOriginal(getNullableString(rs, "avoided_situations_original"));
        response.setAvoidedSituationsTranslated(getNullableString(rs, "avoided_situations_translated"));

        // Support
        response.setHasEnoughSupport(getNullableString(rs, "has_enough_support"));
        response.setDesiredResourcesOriginal(getNullableString(rs, "desired_resources_original"));
        response.setDesiredResourcesTranslated(getNullableString(rs, "desired_resources_translated"));

        // Interview
        response.setWillingToInterview(getNullableString(rs, "willing_to_interview"));
        response.setInterviewDeclineReasonOriginal(getNullableString(rs, "interview_decline_reason_original"));
        response.setInterviewDeclineReasonTranslated(getNullableString(rs, "interview_decline_reason_translated"));
        response.setPreferredInterviewType(getNullableString(rs, "preferred_interview_type"));
        response.setContactInfo(getNullableString(rs, "contact_info"));

        // Additional comments
        response.setAdditionalCommentsOriginal(getNullableString(rs, "additional_comments_original"));
        response.setAdditionalCommentsTranslated(getNullableString(rs, "additional_comments_translated"));

        // Average
        response.setAvgSpeakingAbility(rs.getBigDecimal("avg_speaking_ability"));

        return response;
    }
}
