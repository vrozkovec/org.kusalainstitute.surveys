package org.kusalainstitute.surveys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.kusalainstitute.surveys.pojo.PersonMatch;
import org.kusalainstitute.surveys.pojo.enums.MatchType;

/**
 * Data Access Object for PersonMatch entities.
 */
public class MatchDao extends BaseDao {

    private static final String INSERT_SQL = """
            INSERT INTO person_match (cohort, pre_person_id, post_person_id, match_type, confidence, matched_at, matched_by, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_PRE_PERSON_SQL = """
            SELECT * FROM person_match WHERE pre_person_id = ?
            """;

    private static final String SELECT_BY_POST_PERSON_SQL = """
            SELECT * FROM person_match WHERE post_person_id = ?
            """;

    private static final String SELECT_BY_COHORT_SQL = """
            SELECT * FROM person_match WHERE cohort = ? ORDER BY matched_at
            """;

    private static final String SELECT_ALL_SQL = """
            SELECT * FROM person_match ORDER BY cohort, matched_at
            """;

    private static final String EXISTS_SQL = """
            SELECT 1 FROM person_match WHERE pre_person_id = ? AND post_person_id = ?
            """;

    /**
     * Inserts a new person match.
     *
     * @param match the match to insert
     * @return the inserted match with generated ID
     * @throws SQLException if insertion fails
     */
    public PersonMatch insert(PersonMatch match) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, match.getCohort());
            stmt.setLong(2, match.getPrePersonId());
            stmt.setLong(3, match.getPostPersonId());
            stmt.setString(4, match.getMatchType().name());
            setNullableBigDecimal(stmt, 5, match.getConfidence());
            setNullableTimestamp(stmt, 6, match.getMatchedAt());
            setNullableString(stmt, 7, match.getMatchedBy());
            setNullableString(stmt, 8, match.getNotes());

            stmt.executeUpdate();
            match.setId(getGeneratedKey(stmt));

            log.debug("Inserted person match: {}", match);
            return match;
        }
    }

    /**
     * Finds a match by pre-survey person ID.
     *
     * @param prePersonId the pre-survey person ID
     * @return Optional containing the match if found
     * @throws SQLException if query fails
     */
    public Optional<PersonMatch> findByPrePersonId(Long prePersonId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_PRE_PERSON_SQL)) {

            stmt.setLong(1, prePersonId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a match by post-survey person ID.
     *
     * @param postPersonId the post-survey person ID
     * @return Optional containing the match if found
     * @throws SQLException if query fails
     */
    public Optional<PersonMatch> findByPostPersonId(Long postPersonId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_POST_PERSON_SQL)) {

            stmt.setLong(1, postPersonId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds all matches for a cohort.
     *
     * @param cohort the cohort code
     * @return list of matches in the cohort
     * @throws SQLException if query fails
     */
    public List<PersonMatch> findByCohort(String cohort) throws SQLException {
        List<PersonMatch> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_COHORT_SQL)) {

            stmt.setString(1, cohort);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSet(rs));
                }
            }
        }
        return result;
    }

    /**
     * Finds all matches.
     *
     * @return list of all matches
     * @throws SQLException if query fails
     */
    public List<PersonMatch> findAll() throws SQLException {
        List<PersonMatch> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        }
        return result;
    }

    /**
     * Checks if a match already exists between two persons.
     *
     * @param prePersonId  the pre-survey person ID
     * @param postPersonId the post-survey person ID
     * @return true if match exists
     * @throws SQLException if query fails
     */
    public boolean exists(Long prePersonId, Long postPersonId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_SQL)) {

            stmt.setLong(1, prePersonId);
            stmt.setLong(2, postPersonId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Gets statistics about matches.
     *
     * @return match statistics
     * @throws SQLException if query fails
     */
    public MatchStatistics getStatistics() throws SQLException {
        String sql = """
                SELECT
                    COUNT(*) as total_matches,
                    SUM(CASE WHEN match_type = 'AUTO_EMAIL' THEN 1 ELSE 0 END) as auto_email_matches,
                    SUM(CASE WHEN match_type = 'AUTO_NAME' THEN 1 ELSE 0 END) as auto_name_matches,
                    SUM(CASE WHEN match_type = 'MANUAL' THEN 1 ELSE 0 END) as manual_matches
                FROM person_match
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new MatchStatistics(
                        rs.getInt("total_matches"),
                        rs.getInt("auto_email_matches"),
                        rs.getInt("auto_name_matches"),
                        rs.getInt("manual_matches")
                );
            }
        }
        return new MatchStatistics(0, 0, 0, 0);
    }

    private PersonMatch mapResultSet(ResultSet rs) throws SQLException {
        PersonMatch match = new PersonMatch();
        match.setId(rs.getLong("id"));
        match.setCohort(rs.getString("cohort"));
        match.setPrePersonId(rs.getLong("pre_person_id"));
        match.setPostPersonId(rs.getLong("post_person_id"));
        match.setMatchType(MatchType.valueOf(rs.getString("match_type")));
        match.setConfidence(rs.getBigDecimal("confidence"));
        match.setMatchedAt(getNullableTimestamp(rs, "matched_at"));
        match.setMatchedBy(getNullableString(rs, "matched_by"));
        match.setNotes(getNullableString(rs, "notes"));
        return match;
    }

    /**
     * Statistics about person matches.
     */
    public record MatchStatistics(int totalMatches, int autoEmailMatches, int autoNameMatches, int manualMatches) {
    }
}
