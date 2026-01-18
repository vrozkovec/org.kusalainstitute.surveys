package org.kusalainstitute.surveys.service;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kusalainstitute.surveys.config.SurveysDatabaseConfig;
import org.kusalainstitute.surveys.pojo.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Service for persisting manual match entries to a properties file. This allows manual matches to
 * be restored after database rebuilds by using composite keys (cohort, timestamp, email, name)
 * rather than database IDs.
 */
@Singleton
public class ManualMatchPersistenceService
{

	private static final Logger LOG = LoggerFactory.getLogger(ManualMatchPersistenceService.class);

	private final Path manualMatchesFile;

	/**
	 * Creates a new ManualMatchPersistenceService with injected configuration.
	 *
	 * @param config
	 *            the application configuration
	 */
	@Inject
	public ManualMatchPersistenceService(SurveysDatabaseConfig config)
	{
		this.manualMatchesFile = Paths.get(config.getManualMatchesFile());
		LOG.info("Manual matches file: {}", manualMatchesFile);
	}

	/**
	 * Saves a manual match entry to the properties file.
	 *
	 * @param pre
	 *            the PRE survey person
	 * @param preTimestamp
	 *            the PRE survey response timestamp
	 * @param post
	 *            the POST survey person
	 * @param postTimestamp
	 *            the POST survey response timestamp
	 * @param notes
	 *            optional notes about the match
	 * @param createdBy
	 *            username who created the match
	 */
	public void saveManualMatch(Person pre, LocalDateTime preTimestamp, Person post, LocalDateTime postTimestamp,
		String notes, String createdBy)
	{
		var entry = new ManualMatchEntry(
			pre.getCohort(),
			preTimestamp,
			pre.getNormalizedEmail(),
			pre.getName(),
			post.getCohort(),
			postTimestamp,
			post.getNormalizedEmail(),
			post.getName(),
			notes,
			createdBy,
			LocalDateTime.now());

		Properties props = loadProperties();
		props.setProperty(entry.getKey(), entry.toPropertyValue());
		saveProperties(props);

		LOG.info("Saved manual match: {} -> {} (notes: {})", pre.getName(), post.getName(), notes);
	}

	/**
	 * Gets all stored manual match entries.
	 *
	 * @return list of all manual match entries
	 */
	public List<ManualMatchEntry> getAllEntries()
	{
		Properties props = loadProperties();
		List<ManualMatchEntry> entries = new ArrayList<>();

		for (String key : props.stringPropertyNames())
		{
			String value = props.getProperty(key);
			ManualMatchEntry entry = ManualMatchEntry.fromProperty(key, value);
			if (entry != null)
			{
				entries.add(entry);
			}
			else
			{
				LOG.warn("Failed to parse manual match entry: {} = {}", key, value);
			}
		}

		return entries;
	}

	/**
	 * Finds a stored match for a PRE person by composite key.
	 *
	 * @param cohort
	 *            the cohort
	 * @param timestamp
	 *            the survey timestamp
	 * @param email
	 *            the normalized email
	 * @param name
	 *            the person's name
	 * @return the ManualMatchEntry if found, null otherwise
	 */
	public ManualMatchEntry findMatchByPreKey(String cohort, LocalDateTime timestamp, String email, String name)
	{
		var lookupEntry = new ManualMatchEntry(cohort, timestamp, email, name, null, null, null, null, null, null, null);
		String key = lookupEntry.getKey();

		Properties props = loadProperties();
		String value = props.getProperty(key);

		if (value != null)
		{
			return ManualMatchEntry.fromProperty(key, value);
		}
		return null;
	}

	/**
	 * Loads the properties file, creating it if it doesn't exist.
	 *
	 * @return the loaded Properties
	 */
	private Properties loadProperties()
	{
		Properties props = new Properties();
		if (Files.exists(manualMatchesFile))
		{
			try (Reader reader = Files.newBufferedReader(manualMatchesFile, StandardCharsets.UTF_8))
			{
				props.load(reader);
			}
			catch (IOException e)
			{
				LOG.error("Failed to load manual matches file", e);
			}
		}
		return props;
	}

	/**
	 * Saves the properties to file.
	 *
	 * @param props
	 *            the properties to save
	 */
	private void saveProperties(Properties props)
	{
		try
		{
			Files.createDirectories(manualMatchesFile.getParent());
			try (Writer writer = Files.newBufferedWriter(manualMatchesFile, StandardCharsets.UTF_8))
			{
				props.store(writer, "Manual matches - composite key format for database rebuild recovery");
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to save manual matches file", e);
			throw new RuntimeException("Failed to save manual matches", e);
		}
	}
}
