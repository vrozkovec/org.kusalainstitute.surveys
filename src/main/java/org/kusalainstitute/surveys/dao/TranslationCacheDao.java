package org.kusalainstitute.surveys.dao;

import java.util.Optional;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.kusalainstitute.surveys.pojo.TranslationCache;

/**
 * JDBI DAO interface for TranslationCache entities.
 */
@RegisterBeanMapper(TranslationCache.class)
public interface TranslationCacheDao
{

	/**
	 * Inserts a new translation cache entry.
	 *
	 * @param cache
	 *            the cache entry to insert
	 * @return the generated ID
	 */
	@SqlUpdate("""
		INSERT INTO translation_cache (source_text_hash, source_text, translated_text, source_language, target_language, created_at)
		VALUES (:sourceTextHash, :sourceText, :translatedText, :sourceLanguage, :targetLanguage, :createdAt)
		""")
	@GetGeneratedKeys
	long insert(@BindBean TranslationCache cache);

	/**
	 * Finds a cached translation by source text hash.
	 *
	 * @param sourceTextHash
	 *            SHA-256 hash of source text
	 * @return Optional containing the cache entry if found
	 */
	@SqlQuery("SELECT * FROM translation_cache WHERE source_text_hash = :sourceTextHash")
	Optional<TranslationCache> findByHash(@Bind("sourceTextHash") String sourceTextHash);

	/**
	 * Gets the count of cached translations.
	 *
	 * @return number of cached translations
	 */
	@SqlQuery("SELECT COUNT(*) FROM translation_cache")
	int count();
}
