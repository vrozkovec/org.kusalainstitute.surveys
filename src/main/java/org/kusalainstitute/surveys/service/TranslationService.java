package org.kusalainstitute.surveys.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;
import org.kusalainstitute.surveys.utils.translations.deepl.DeeplSourceLanguage;
import org.kusalainstitute.surveys.utils.translations.deepl.DeeplTargetLanguage;
import org.kusalainstitute.surveys.utils.translations.deepl.IDeepl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Service for translating survey text fields from French to English using DeepL API. Maintains a
 * file-based cache of translations to avoid redundant API calls.
 * <p>
 * The cache is loaded from two sources:
 * <ol>
 * <li>Classpath resource {@code /data/translations.properties} (bundled in JAR)</li>
 * <li>Filesystem file {@code translations.properties} in working directory (for runtime additions)</li>
 * </ol>
 * New translations are saved to the filesystem file only.
 */
@Singleton
public class TranslationService
{

	private static final Logger LOG = LoggerFactory.getLogger(TranslationService.class);

	/** Classpath resource for bundled translations (read-only, inside JAR). */
	private static final String CLASSPATH_CACHE = "/data/translations.properties";

	/** Filesystem file for local development (used when running from IDE). */
	private static final Path FILESYSTEM_CACHE = Path.of("src/main/resources/data/translations.properties");

	private final IDeepl deepl;
	private final Properties cache;

	/**
	 * Creates a new TranslationService with injected DeepL client.
	 *
	 * @param deepl
	 *            the DeepL translation client
	 */
	@Inject
	public TranslationService(IDeepl deepl)
	{
		this.deepl = deepl;
		cache = loadCache();
		LOG.info("TranslationService initialized with {} cached translations", cache.size());
	}

	/**
	 * Translates text from French to English. Returns cached translation if available, otherwise
	 * calls DeepL API and caches the result. On API failure, returns original text.
	 *
	 * @param text
	 *            the French text to translate
	 * @return the English translation, or the original text if blank/null or on error
	 */
	public String translate(String text)
	{
		if (StringUtils.isBlank(text))
		{
			return text;
		}

		String hash = DigestUtils.sha256Hex(text);

		// Check cache first
		if (cache.containsKey(hash))
		{
			LOG.debug("Cache hit for text hash: {}", hash.substring(0, 8));
			return cache.getProperty(hash);
		}

		// Call DeepL API with rate limit handling
		try
		{
			// Small delay to avoid rate limiting
			Thread.sleep(5000);

			LOG.debug("Translating text (hash: {}): {}", hash.substring(0, 8), StringUtils.abbreviate(text, 50));
			String translated = deepl.translate(text, DeeplSourceLanguage.FRENCH, DeeplTargetLanguage.ENGLISH_AMERICAN);

			if (translated != null)
			{
				cache.setProperty(hash, translated);
				saveCache();
				LOG.debug("Translation cached: {} -> {}", StringUtils.abbreviate(text, 30), StringUtils.abbreviate(translated, 30));
				return translated;
			}
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			LOG.warn("Translation interrupted for text: {}", StringUtils.abbreviate(text, 30));
		}
		catch (Exception e)
		{
			LOG.warn("Translation failed for text ({}): {} - returning original", StringUtils.abbreviate(text, 30), e.getMessage());
		}

		// Return original text on failure
		return text;
	}

	/**
	 * Translates all text fields in a pre-survey response from French to English.
	 *
	 * @param response
	 *            the pre-survey response to translate
	 */
	public void translateAll(PreSurveyResponse response)
	{
		response.setMostDifficultThingTranslated(translate(response.getMostDifficultThingOriginal()));
		response.setWhyImproveEnglishTranslated(translate(response.getWhyImproveEnglishOriginal()));
		response.setOtherSituationsTranslated(translate(response.getOtherSituationsOriginal()));
		response.setDifficultPartTranslated(translate(response.getDifficultPartOriginal()));
		response.setDescribeSituationsTranslated(translate(response.getDescribeSituationsOriginal()));
	}

	/**
	 * Translates all text fields in a post-survey response from French to English.
	 *
	 * @param response
	 *            the post-survey response to translate
	 */
	public void translateAll(PostSurveyResponse response)
	{
		response.setAppUsageDurationTranslated(translate(response.getAppUsageDurationOriginal()));
		response.setWhatHelpedMostTranslated(translate(response.getWhatHelpedMostOriginal()));
		response.setMostDifficultOverallTranslated(translate(response.getMostDifficultOverallOriginal()));
		response.setMostDifficultForJobTranslated(translate(response.getMostDifficultForJobOriginal()));
		response.setEmotionalDifficultiesTranslated(translate(response.getEmotionalDifficultiesOriginal()));
		response.setAvoidedSituationsTranslated(translate(response.getAvoidedSituationsOriginal()));
		response.setHasEnoughSupportTranslated(translate(response.getHasEnoughSupportOriginal()));
		response.setDesiredResourcesTranslated(translate(response.getDesiredResourcesOriginal()));
		response.setInterviewDeclineReasonTranslated(translate(response.getInterviewDeclineReasonOriginal()));
		response.setAdditionalCommentsTranslated(translate(response.getAdditionalCommentsOriginal()));
	}

	/**
	 * Loads the translation cache from both classpath and filesystem. Classpath translations
	 * (bundled in JAR) are loaded first, then filesystem translations are merged on top.
	 *
	 * @return the merged properties from both sources
	 */
	private Properties loadCache()
	{
		Properties props = new Properties();

		// 1. Load from classpath (bundled translations)
		loadFromClasspath(props);

		// 2. Load from filesystem (runtime additions) - these override classpath entries
		loadFromFilesystem(props);

		return props;
	}

	/**
	 * Loads translations from the classpath resource (bundled in JAR).
	 *
	 * @param props
	 *            the properties to load into
	 */
	private void loadFromClasspath(Properties props)
	{
		try (InputStream is = getClass().getResourceAsStream(CLASSPATH_CACHE))
		{
			if (is != null)
			{
				props.load(is);
				LOG.info("Loaded {} translations from classpath: {}", props.size(), CLASSPATH_CACHE);
			}
			else
			{
				LOG.info("No bundled translations found at classpath: {}", CLASSPATH_CACHE);
			}
		}
		catch (IOException e)
		{
			LOG.warn("Failed to load translations from classpath {}: {}", CLASSPATH_CACHE, e.getMessage());
		}
	}

	/**
	 * Loads translations from the filesystem cache file (runtime additions).
	 *
	 * @param props
	 *            the properties to load into (existing entries may be overwritten)
	 */
	private void loadFromFilesystem(Properties props)
	{
		if (Files.exists(FILESYSTEM_CACHE))
		{
			try (Reader reader = Files.newBufferedReader(FILESYSTEM_CACHE, StandardCharsets.UTF_8))
			{
				int beforeSize = props.size();
				props.load(reader);
				int added = props.size() - beforeSize;
				LOG.info("Loaded {} additional translations from filesystem: {}", added, FILESYSTEM_CACHE);
			}
			catch (IOException e)
			{
				LOG.warn("Failed to load translation cache from {}: {}", FILESYSTEM_CACHE, e.getMessage());
			}
		}
		else
		{
			LOG.debug("No filesystem cache file found at: {}", FILESYSTEM_CACHE.toAbsolutePath());
		}
	}

	/**
	 * Saves the translation cache to the filesystem. Only the filesystem cache is written to, as
	 * the classpath resource is read-only (inside JAR).
	 */
	private void saveCache()
	{
		try (Writer writer = Files.newBufferedWriter(FILESYSTEM_CACHE, StandardCharsets.UTF_8))
		{
			cache.store(writer, "Translation cache - SHA256 hash -> translated text");
			LOG.debug("Saved {} translations to filesystem cache: {}", cache.size(), FILESYSTEM_CACHE);
		}
		catch (IOException e)
		{
			LOG.error("Failed to save translation cache to {}: {}", FILESYSTEM_CACHE, e.getMessage());
		}
	}

	/**
	 * Returns the number of cached translations.
	 *
	 * @return cache size
	 */
	public int getCacheSize()
	{
		return cache.size();
	}
}
