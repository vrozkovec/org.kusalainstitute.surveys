package org.kusalainstitute.surveys.service;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

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

/**
 * Service for importing survey data from Excel files into the database.
 */
public class ImportService
{

	private static final Logger LOG = LoggerFactory.getLogger(ImportService.class);

	private final PersonDao personDao;
	private final PreSurveyDao preSurveyDao;
	private final PostSurveyDao postSurveyDao;
	private final PreSurveyParser preSurveyParser;
	private final PostSurveyParser postSurveyParser;

	public ImportService()
	{
		this.personDao = new PersonDao();
		this.preSurveyDao = new PreSurveyDao();
		this.postSurveyDao = new PostSurveyDao();
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
	 * @throws SQLException
	 *             if database operation fails
	 */
	public int importPreSurvey(Path filePath) throws IOException, SQLException
	{
		LOG.info("Importing pre-survey data from: {}", filePath);

		List<ParsedPreSurvey> parsed = preSurveyParser.parse(filePath);
		int imported = 0;

		for (ParsedPreSurvey p : parsed)
		{
			try
			{
				// Insert person
				Person person = personDao.insert(p.person());

				// Insert response linked to person
				PreSurveyResponse response = p.response();
				response.setPersonId(person.getId());
				preSurveyDao.insert(response);

				imported++;
			}
			catch (SQLException e)
			{
				LOG.warn("Error importing row {}: {}", p.response().getRowNumber(), e.getMessage());
			}
		}

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
	 * @throws SQLException
	 *             if database operation fails
	 */
	public int importPostSurvey(Path filePath) throws IOException, SQLException
	{
		LOG.info("Importing post-survey data from: {}", filePath);

		List<ParsedPostSurvey> parsed = postSurveyParser.parse(filePath);
		int imported = 0;

		for (ParsedPostSurvey p : parsed)
		{
			try
			{
				// Insert person
				Person person = personDao.insert(p.person());

				// Insert response linked to person
				PostSurveyResponse response = p.response();
				response.setPersonId(person.getId());
				postSurveyDao.insert(response);

				imported++;
			}
			catch (SQLException e)
			{
				LOG.warn("Error importing row {}: {}", p.response().getRowNumber(), e.getMessage());
			}
		}

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
	 * @throws SQLException
	 *             if database operation fails
	 */
	public int importAll(Path preFilePath, Path postFilePath) throws IOException, SQLException
	{
		int preCount = importPreSurvey(preFilePath);
		int postCount = importPostSurvey(postFilePath);
		return preCount + postCount;
	}
}
