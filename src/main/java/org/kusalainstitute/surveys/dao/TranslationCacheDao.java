package org.kusalainstitute.surveys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import org.kusalainstitute.surveys.pojo.TranslationCache;

/**
 * Data Access Object for TranslationCache entities.
 */
public class TranslationCacheDao extends BaseDao {

    private static final String INSERT_SQL = """
            INSERT INTO translation_cache (source_text_hash, source_text, translated_text, source_language, target_language, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_HASH_SQL = """
            SELECT * FROM translation_cache WHERE source_text_hash = ?
            """;

    private static final String COUNT_SQL = """
            SELECT COUNT(*) FROM translation_cache
            """;

    /**
     * Inserts a new translation cache entry.
     *
     * @param cache the cache entry to insert
     * @return the inserted entry with generated ID
     * @throws SQLException if insertion fails
     */
    public TranslationCache insert(TranslationCache cache) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cache.getSourceTextHash());
            stmt.setString(2, cache.getSourceText());
            stmt.setString(3, cache.getTranslatedText());
            stmt.setString(4, cache.getSourceLanguage());
            stmt.setString(5, cache.getTargetLanguage());
            setNullableTimestamp(stmt, 6, cache.getCreatedAt());

            stmt.executeUpdate();
            cache.setId(getGeneratedKey(stmt));

            log.debug("Inserted translation cache: hash={}", cache.getSourceTextHash());
            return cache;
        }
    }

    /**
     * Finds a cached translation by source text hash.
     *
     * @param sourceTextHash SHA-256 hash of source text
     * @return Optional containing the cache entry if found
     * @throws SQLException if query fails
     */
    public Optional<TranslationCache> findByHash(String sourceTextHash) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_HASH_SQL)) {

            stmt.setString(1, sourceTextHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the count of cached translations.
     *
     * @return number of cached translations
     * @throws SQLException if query fails
     */
    public int count() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_SQL);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private TranslationCache mapResultSet(ResultSet rs) throws SQLException {
        TranslationCache cache = new TranslationCache();
        cache.setId(rs.getLong("id"));
        cache.setSourceTextHash(rs.getString("source_text_hash"));
        cache.setSourceText(rs.getString("source_text"));
        cache.setTranslatedText(rs.getString("translated_text"));
        cache.setSourceLanguage(rs.getString("source_language"));
        cache.setTargetLanguage(rs.getString("target_language"));
        cache.setCreatedAt(getNullableTimestamp(rs, "created_at"));
        return cache;
    }
}
