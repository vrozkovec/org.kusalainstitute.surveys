package org.kusalainstitute.surveys.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.jdbi.v3.core.Jdbi;
import org.kusalainstitute.surveys.dao.PersonDao;
import org.kusalainstitute.surveys.dao.PostSurveyDao;
import org.kusalainstitute.surveys.dao.PreSurveyDao;
import org.kusalainstitute.surveys.parser.PostSurveyParser;
import org.kusalainstitute.surveys.parser.PostSurveyParser.ParsedPostSurvey;
import org.kusalainstitute.surveys.parser.PreSurveyParser;
import org.kusalainstitute.surveys.parser.PreSurveyParser.ParsedPreSurvey;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Service for importing survey data from Excel files into the database.
 */
@Singleton
public class ImportService
{

	private static final Logger LOG = LoggerFactory.getLogger(ImportService.class);

	private final Jdbi jdbi;
	private final PreSurveyParser preSurveyParser;
	private final PostSurveyParser postSurveyParser;

	/**
	 * Creates a new ImportService with injected JDBI instance.
	 *
	 * @param jdbi
	 *            the JDBI instance for database access
	 */
	@Inject
	public ImportService(Jdbi jdbi)
	{
		this.jdbi = jdbi;
		this.preSurveyParser = new PreSurveyParser();
		this.postSurveyParser = new PostSurveyParser();
	}

	/**
	 * Imports pre-survey data from an Excel file.
	 *
	 * @param filePath
	 *            path to the pre-survey Excel file
	 * @return number of imported records
	 * @throws IOException
	 *             if file cannot be read
	 */
	public int importPreSurvey(Path filePath) throws IOException
	{
		LOG.info("Importing pre-survey data from: {}", filePath);

		List<ParsedPreSurvey> parsed = preSurveyParser.parse(filePath);

		int imported = jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			PreSurveyDao preSurveyDao = handle.attach(PreSurveyDao.class);

			int count = 0;
			int skipped = 0;
			for (ParsedPreSurvey p : parsed)
			{
				try
				{
					Person person = p.person();
					PreSurveyResponse response = p.response();

					// Check if record already exists (idempotent import)
					if (preSurveyDao.exists(person.getCohort(), response.getTimestamp(),
						person.getName(), person.getNormalizedEmail()))
					{
						skipped++;
						LOG.debug("Skipping duplicate pre-survey row {}: {} / {}", response.getRowNumber(),
							person.getName(), person.getEmail());
						continue;
					}

					// Insert person
					long personId = personDao.insert(person);
					person.setId(personId);

					// Insert response linked to person
					response.setPersonId(personId);
					long responseId = preSurveyDao.insert(response);
					response.setId(responseId);

					count++;
				}
				catch (Exception e)
				{
					LOG.warn("Error importing row {}: {}", p.response().getRowNumber(), e.getMessage());
				}
			}
			if (skipped > 0)
			{
				LOG.info("Skipped {} duplicate pre-survey records", skipped);
			}
			return count;
		});

		LOG.info("Imported {} pre-survey records", imported);
		return imported;
	}

	/**
	 * Imports post-survey data from an Excel file.
	 *
	 * @param filePath
	 *            path to the post-survey Excel file
	 * @return number of imported records
	 * @throws IOException
	 *             if file cannot be read
	 */
	public int importPostSurvey(Path filePath) throws IOException
	{
		LOG.info("Importing post-survey data from: {}", filePath);

		List<ParsedPostSurvey> parsed = postSurveyParser.parse(filePath);

		int imported = jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			PostSurveyDao postSurveyDao = handle.attach(PostSurveyDao.class);

			int count = 0;
			int skipped = 0;
			for (ParsedPostSurvey p : parsed)
			{
				try
				{
					Person person = p.person();
					PostSurveyResponse response = p.response();

					// Check if record already exists (idempotent import)
					if (postSurveyDao.exists(person.getCohort(), response.getTimestamp(),
						person.getName(), person.getNormalizedEmail()))
					{
						skipped++;
						LOG.debug("Skipping duplicate post-survey row {}: {} / {}", response.getRowNumber(),
							person.getName(), person.getEmail());
						continue;
					}

					// Insert person
					long personId = personDao.insert(person);
					person.setId(personId);

					// Insert response linked to person
					response.setPersonId(personId);
					long responseId = postSurveyDao.insert(response);
					response.setId(responseId);

					count++;
				}
				catch (Exception e)
				{
					LOG.warn("Error importing row {}: {}", p.response().getRowNumber(), e.getMessage());
				}
			}
			if (skipped > 0)
			{
				LOG.info("Skipped {} duplicate post-survey records", skipped);
			}
			return count;
		});

		LOG.info("Imported {} post-survey records", imported);
		return imported;
	}

	/**
	 * Imports both pre and post surveys from their respective files.
	 *
	 * @param preFilePath
	 *            path to pre-survey file
	 * @param postFilePath
	 *            path to post-survey file
	 * @return total number of imported records
	 * @throws IOException
	 *             if files cannot be read
	 */
	public int importAll(Path preFilePath, Path postFilePath) throws IOException
	{
		int preCount = importPreSurvey(preFilePath);
		int postCount = importPostSurvey(postFilePath);
		return preCount + postCount;
	}
}
