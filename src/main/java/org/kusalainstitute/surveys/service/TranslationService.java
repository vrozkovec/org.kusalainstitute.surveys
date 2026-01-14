package org.kusalainstitute.surveys.service;

import java.io.IOException;
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
 */
@Singleton
public class TranslationService
{

	private static final Logger LOG = LoggerFactory.getLogger(TranslationService.class);
	private static final Path CACHE_FILE = Path.of("data/translations.properties");

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
	 * Loads the translation cache from the file system.
	 *
	 * @return the loaded properties, or empty properties if file doesn't exist
	 */
	private Properties loadCache()
	{
		Properties props = new Properties();

		if (Files.exists(CACHE_FILE))
		{
			try (Reader reader = Files.newBufferedReader(CACHE_FILE, StandardCharsets.UTF_8))
			{
				props.load(reader);
				LOG.info("Loaded {} translations from cache file: {}", props.size(), CACHE_FILE);
			}
			catch (IOException e)
			{
				LOG.warn("Failed to load translation cache from {}: {}", CACHE_FILE, e.getMessage());
			}
		}
		else
		{
			LOG.info("Translation cache file not found, starting with empty cache: {}", CACHE_FILE);
		}

		return props;
	}

	/**
	 * Saves the translation cache to the file system.
	 */
	private void saveCache()
	{
		try
		{
			// Ensure parent directory exists
			Files.createDirectories(CACHE_FILE.getParent());

			try (Writer writer = Files.newBufferedWriter(CACHE_FILE, StandardCharsets.UTF_8))
			{
				cache.store(writer, "Translation cache - SHA256 hash -> translated text");
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to save translation cache to {}: {}", CACHE_FILE, e.getMessage());
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
