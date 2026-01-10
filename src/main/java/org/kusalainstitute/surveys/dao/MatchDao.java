package org.kusalainstitute.surveys.dao;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.kusalainstitute.surveys.pojo.PersonMatch;

/**
 * JDBI DAO interface for PersonMatch entities.
 */
@RegisterBeanMapper(PersonMatch.class)
public interface MatchDao
{

	/**
	 * Inserts a new person match.
	 *
	 * @param match
	 *            the match to insert
	 * @return the generated ID
	 */
	@SqlUpdate("""
		INSERT INTO person_match (cohort, pre_person_id, post_person_id, match_type, confidence, matched_at, matched_by, notes)
		VALUES (:cohort, :prePersonId, :postPersonId, :matchType, :confidence, :matchedAt, :matchedBy, :notes)
		""")
	@GetGeneratedKeys
	long insert(@BindBean PersonMatch match);

	/**
	 * Finds a match by pre-survey person ID.
	 *
	 * @param prePersonId
	 *            the pre-survey person ID
	 * @return Optional containing the match if found
	 */
	@SqlQuery("SELECT * FROM person_match WHERE pre_person_id = :prePersonId")
	Optional<PersonMatch> findByPrePersonId(@Bind("prePersonId") long prePersonId);

	/**
	 * Finds a match by post-survey person ID.
	 *
	 * @param postPersonId
	 *            the post-survey person ID
	 * @return Optional containing the match if found
	 */
	@SqlQuery("SELECT * FROM person_match WHERE post_person_id = :postPersonId")
	Optional<PersonMatch> findByPostPersonId(@Bind("postPersonId") long postPersonId);

	/**
	 * Finds all matches for a cohort.
	 *
	 * @param cohort
	 *            the cohort code
	 * @return list of matches in the cohort
	 */
	@SqlQuery("SELECT * FROM person_match WHERE cohort = :cohort ORDER BY matched_at")
	List<PersonMatch> findByCohort(@Bind("cohort") String cohort);

	/**
	 * Finds all matches.
	 *
	 * @return list of all matches
	 */
	@SqlQuery("SELECT * FROM person_match ORDER BY cohort, matched_at")
	List<PersonMatch> findAll();

	/**
	 * Checks if a match already exists between two persons.
	 *
	 * @param prePersonId
	 *            the pre-survey person ID
	 * @param postPersonId
	 *            the post-survey person ID
	 * @return true if match exists
	 */
	@SqlQuery("SELECT EXISTS(SELECT 1 FROM person_match WHERE pre_person_id = :prePersonId AND post_person_id = :postPersonId)")
	boolean exists(@Bind("prePersonId") long prePersonId, @Bind("postPersonId") long postPersonId);

	/**
	 * Gets the total number of matches.
	 *
	 * @return total matches
	 */
	@SqlQuery("SELECT COUNT(*) FROM person_match")
	int countTotal();

	/**
	 * Gets the number of auto-email matches.
	 *
	 * @return auto-email match count
	 */
	@SqlQuery("SELECT COUNT(*) FROM person_match WHERE match_type = 'AUTO_EMAIL'")
	int countAutoEmail();

	/**
	 * Gets the number of auto-name matches.
	 *
	 * @return auto-name match count
	 */
	@SqlQuery("SELECT COUNT(*) FROM person_match WHERE match_type = 'AUTO_NAME'")
	int countAutoName();

	/**
	 * Gets the number of manual matches.
	 *
	 * @return manual match count
	 */
	@SqlQuery("SELECT COUNT(*) FROM person_match WHERE match_type = 'MANUAL'")
	int countManual();

	/**
	 * Gets statistics about matches. This is a default method that combines the count queries.
	 *
	 * @return match statistics
	 */
	default MatchStatistics getStatistics()
	{
		return new MatchStatistics(countTotal(), countAutoEmail(), countAutoName(), countManual());
	}

	/**
	 * Statistics about person matches.
	 */
	record MatchStatistics(int totalMatches, int autoEmailMatches, int autoNameMatches, int manualMatches)
	{
	}
}
