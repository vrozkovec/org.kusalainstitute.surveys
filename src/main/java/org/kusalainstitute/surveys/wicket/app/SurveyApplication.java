package org.kusalainstitute.surveys.wicket.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.UrlResourceReference;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.util.lang.Bytes;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.kusalainstitute.surveys.config.SurveysDatabaseConfig;
import org.kusalainstitute.surveys.config.SurveysModule;
import org.kusalainstitute.surveys.service.ImportService;
import org.kusalainstitute.surveys.service.MatchingService;
import org.kusalainstitute.surveys.wicket.pages.AIExportPage;
import org.kusalainstitute.surveys.wicket.pages.AnswersPage;
import org.kusalainstitute.surveys.wicket.pages.HomePage;
import org.kusalainstitute.surveys.wicket.pages.MatchManagementPage;
import org.kusalainstitute.surveys.wicket.pages.SituationAnalysisPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 */
public class SurveyApplication extends WebApplication
{
	private static final Logger LOG = LoggerFactory.getLogger(SurveyApplication.class);

	/**
	 *
	 */
	public static final String MOUNTPOINT_LOGOUT = "/odhlaseni";

	private Injector injector;

	/**
	 * Construct.
	 */
	public SurveyApplication()
	{
		super();
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();

		// Initialize Guice
		injector = Guice.createInjector(new SurveysModule());

		// Drop all tables and reload data
		reloadData();

		getCspSettings().blocking().disabled();

		getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_EXCEPTION_PAGE);

		getApplicationSettings().setDefaultMaximumUploadSize(Bytes.megabytes(108));

		/***************************************************
		 * BOOTSTRAP
		 ****************************************************/
		BootstrapSettings settings = new BootstrapSettings();
		settings.setJsResourceFilterName("footer-container");
		settings.setCssResourceReference(
			new UrlResourceReference(Url.parse("https://cdn.jsdelivr.net/npm/bootstrap@5/dist/css/bootstrap.min.css")));
		settings.setJsResourceReference(new UrlResourceReference(
			Url.parse("https://cdn.jsdelivr.net/npm/bootstrap@5/dist/js/bootstrap.bundle.min.js")));
		Bootstrap.install(this, settings);
		getHeaderResponseDecorators()
			.add(response -> new JavaScriptFilteredIntoFooterHeaderResponse(response, "footer-container"));

		mountPage("analysis", SituationAnalysisPage.class);
		mountPage("answers", AnswersPage.class);
		mountPage("matches", MatchManagementPage.class);
		mountPage("ai-export", AIExportPage.class);

	}

	/**
	 * Returns the JDBI instance for database access.
	 *
	 * @return the JDBI instance
	 */
	public Jdbi getJdbi()
	{
		return injector.getInstance(Jdbi.class);
	}

	/**
	 * Returns the Guice injector for accessing services.
	 *
	 * @return the Guice injector
	 */
	public Injector getInjector()
	{
		return injector;
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @return this application
	 */
	public static SurveyApplication get()
	{
		return (SurveyApplication)Application.get();
	}

	/**
	 * Drops all database tables and reloads data from Excel files.
	 * This method performs the following steps:
	 * <ol>
	 * <li>Drop all tables using flyway.clean()</li>
	 * <li>Run migrations to recreate the schema</li>
	 * <li>Import pre-survey data from configured data directory</li>
	 * <li>Import post-survey data from configured data directory</li>
	 * <li>Run automatic matching</li>
	 * <li>Restore manual matches from persistence file</li>
	 * </ol>
	 */
	private void reloadData()
	{
		try
		{
			Flyway flyway = injector.getInstance(Flyway.class);
			ImportService importService = injector.getInstance(ImportService.class);
			MatchingService matchingService = injector.getInstance(MatchingService.class);
			SurveysDatabaseConfig config = injector.getInstance(SurveysDatabaseConfig.class);

			String dataDir = config.getDataDir();
			Path prePath = Paths.get(dataDir, "pre");
			Path postPath = Paths.get(dataDir, "post");

			// Step 1: Drop all tables
			LOG.info("=== Step 1: Dropping all database tables ===");
			flyway.clean();
			LOG.info("Database cleaned successfully.");

			// Step 2: Run migrations
			LOG.info("=== Step 2: Running database migrations ===");
			flyway.migrate();
			LOG.info("Database migrations completed successfully.");

			// Step 3: Import pre-survey data
			LOG.info("=== Step 3: Importing surveys ===");
			int preCount = importDirectory(importService, prePath, true);
			int postCount = importDirectory(importService, postPath, false);
			LOG.info("Imported {} pre-survey and {} post-survey records.", preCount, postCount);

			// Step 4: Run automatic matching
			LOG.info("=== Step 4: Running automatic matching ===");
			MatchingService.MatchResult result = matchingService.runAutoMatch();
			LOG.info("Email matches: {}", result.emailMatches());
			LOG.info("Name matches: {}", result.nameMatches());
			LOG.info("Total new matches: {}", result.totalMatches());

			// Step 5: Restore manual matches
			LOG.info("=== Step 5: Restoring manual matches ===");
			int restoredCount = matchingService.applyStoredManualMatches();
			LOG.info("Restored {} manual match(es) from persistence file.", restoredCount);

			LOG.info("=== Data reload complete ===");
		}
		catch (Exception e)
		{
			LOG.error("Error during data reload: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to reload data on startup", e);
		}
	}

	/**
	 * Imports all Excel files from a directory.
	 *
	 * @param importService
	 *            the import service to use
	 * @param dir
	 *            the directory path
	 * @param isPreSurvey
	 *            true for pre-survey, false for post-survey
	 * @return number of imported records
	 * @throws IOException
	 *             if import fails
	 */
	private int importDirectory(ImportService importService, Path dir, boolean isPreSurvey) throws IOException
	{
		String type = isPreSurvey ? "pre-survey" : "post-survey";

		if (!Files.exists(dir))
		{
			LOG.warn("Directory not found: {}, skipping {} import.", dir, type);
			return 0;
		}

		LOG.info("Importing {} data from directory: {}", type, dir);

		List<Path> excelFiles;
		try (var stream = Files.list(dir))
		{
			excelFiles = stream
				.filter(p -> p.toString().toLowerCase().endsWith(".xlsx"))
				.filter(Files::isRegularFile)
				.sorted()
				.toList();
		}

		if (excelFiles.isEmpty())
		{
			LOG.info("No .xlsx files found in {}", dir);
			return 0;
		}

		LOG.info("Found {} Excel file(s)", excelFiles.size());

		int total = 0;
		for (Path file : excelFiles)
		{
			int count = isPreSurvey
				? importService.importPreSurvey(file)
				: importService.importPostSurvey(file);
			LOG.info("  Imported {} records from {}", count, file.getFileName());
			total += count;
		}
		return total;
	}
}