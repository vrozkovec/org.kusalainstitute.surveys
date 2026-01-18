package org.kusalainstitute.surveys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

import org.flywaydb.core.Flyway;
import org.kusalainstitute.surveys.config.GuiceFactory;
import org.kusalainstitute.surveys.config.SurveysModule;
import org.kusalainstitute.surveys.dao.MatchDao;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.service.AnalysisService;
import org.kusalainstitute.surveys.service.ImportService;
import org.kusalainstitute.surveys.service.MatchingService;
import org.kusalainstitute.surveys.service.records.AnalysisResult;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * CLI application for Kusala Institute Survey Analysis.
 */
@Command(name = "surveys", mixinStandardHelpOptions = true, version = "1.0.0", description = "Kusala Institute Survey Analysis Tool", subcommands = {
		App.InitCommand.class, App.ImportCommand.class, App.MatchCommand.class, App.AnalyzeCommand.class,
		App.ReloadCommand.class })
public class App implements Callable<Integer>
{

	/**
	 * Main entry point for the CLI application.
	 *
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args)
	{
		Injector injector = Guice.createInjector(new SurveysModule());
		int exitCode = new CommandLine(new App(), new GuiceFactory(injector)).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call()
	{
		// Show help when no command specified
		CommandLine.usage(this, System.out);
		return 0;
	}

	/**
	 * Initialize database (run migrations).
	 */
	@Command(name = "init", description = "Initialize database schema")
	static class InitCommand implements Callable<Integer>
	{

		private final Flyway flyway;

		/**
		 * Creates a new InitCommand with injected Flyway.
		 *
		 * @param flyway
		 *            the Flyway instance for migrations
		 */
		@Inject
		public InitCommand(Flyway flyway)
		{
			this.flyway = flyway;
		}

		@Override
		public Integer call()
		{
			System.out.println("Initializing database...");
			try
			{
				flyway.migrate();
				System.out.println("Database initialized successfully.");
				return 0;
			}
			catch (Exception e)
			{
				System.err.println("Error initializing database: " + e.getMessage());
				e.printStackTrace();
				return 1;
			}
		}
	}

	/**
	 * Import survey data from Excel files or directories.
	 */
	@Command(name = "import", description = "Import survey data from Excel files or directories")
	static class ImportCommand implements Callable<Integer>
	{

		private final Flyway flyway;
		private final ImportService importService;

		@Option(names = { "--pre" }, description = "Path to pre-survey Excel file or directory containing Excel files")
		private Path prePath;

		@Option(names = { "--post" }, description = "Path to post-survey Excel file or directory containing Excel files")
		private Path postPath;

		/**
		 * Creates a new ImportCommand with injected dependencies.
		 *
		 * @param flyway
		 *            the Flyway instance for migrations
		 * @param importService
		 *            the import service
		 */
		@Inject
		public ImportCommand(Flyway flyway, ImportService importService)
		{
			this.flyway = flyway;
			this.importService = importService;
		}

		@Override
		public Integer call()
		{
			if (prePath == null && postPath == null)
			{
				System.err.println("Please specify --pre and/or --post file or directory paths");
				return 1;
			}

			try
			{
				// Ensure database is initialized
				flyway.migrate();

				int total = 0;

				if (prePath != null)
				{
					total += importPath(prePath, true);
				}

				if (postPath != null)
				{
					total += importPath(postPath, false);
				}

				System.out.println("Total imported: " + total + " records");
				return 0;

			}
			catch (IOException e)
			{
				System.err.println("Error importing data: " + e.getMessage());
				e.printStackTrace();
				return 1;
			}
		}

		/**
		 * Imports from a path, which can be either a file or directory.
		 *
		 * @param path
		 *            the file or directory path
		 * @param isPreSurvey
		 *            true for pre-survey, false for post-survey
		 * @return number of imported records
		 * @throws IOException
		 *             if import fails
		 */
		private int importPath(Path path, boolean isPreSurvey) throws IOException
		{
			if (Files.isDirectory(path))
			{
				return importDirectory(path, isPreSurvey);
			}
			else
			{
				return importFile(path, isPreSurvey);
			}
		}

		/**
		 * Imports all Excel files from a directory.
		 *
		 * @param dir
		 *            the directory path
		 * @param isPreSurvey
		 *            true for pre-survey, false for post-survey
		 * @return number of imported records
		 * @throws IOException
		 *             if import fails
		 */
		private int importDirectory(Path dir, boolean isPreSurvey) throws IOException
		{
			String type = isPreSurvey ? "pre-survey" : "post-survey";
			System.out.println("Importing " + type + " data from directory: " + dir);

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
				System.out.println("No .xlsx files found in " + dir);
				return 0;
			}

			System.out.println("Found " + excelFiles.size() + " Excel file(s)");

			int total = 0;
			for (Path file : excelFiles)
			{
				total += importFile(file, isPreSurvey);
			}
			return total;
		}

		/**
		 * Imports a single Excel file.
		 *
		 * @param file
		 *            the file path
		 * @param isPreSurvey
		 *            true for pre-survey, false for post-survey
		 * @return number of imported records
		 * @throws IOException
		 *             if import fails
		 */
		private int importFile(Path file, boolean isPreSurvey) throws IOException
		{
			String type = isPreSurvey ? "pre-survey" : "post-survey";
			System.out.println("Importing " + type + " data from: " + file.getFileName());

			int count = isPreSurvey
				? importService.importPreSurvey(file)
				: importService.importPostSurvey(file);

			System.out.println("Imported " + count + " " + type + " records from " + file.getFileName());
			return count;
		}
	}

	/**
	 * Match pre and post survey respondents.
	 */
	@Command(name = "match", description = "Match pre and post survey respondents")
	static class MatchCommand implements Callable<Integer>
	{

		private final MatchingService matchingService;

		@Option(names = { "--auto" }, description = "Run automatic matching")
		private boolean auto;

		@Option(names = { "--status" }, description = "Show matching status")
		private boolean status;

		@Option(names = { "--unmatched" }, description = "List unmatched persons")
		private boolean unmatched;

		/**
		 * Creates a new MatchCommand with injected MatchingService.
		 *
		 * @param matchingService
		 *            the matching service
		 */
		@Inject
		public MatchCommand(MatchingService matchingService)
		{
			this.matchingService = matchingService;
		}

		@Override
		public Integer call()
		{
			if (status)
			{
				showStatus();
				return 0;
			}

			if (unmatched)
			{
				showUnmatched();
				return 0;
			}

			if (auto)
			{
				System.out.println("Running automatic matching...");
				MatchingService.MatchResult result = matchingService.runAutoMatch();
				System.out.println("Email matches: " + result.emailMatches());
				System.out.println("Name matches: " + result.nameMatches());
				System.out.println("Total new matches: " + result.totalMatches());
				return 0;
			}

			// Default: show status
			showStatus();
			return 0;
		}

		private void showStatus()
		{
			MatchDao.MatchStatistics stats = matchingService.getStatistics();
			System.out.println("=== Matching Status ===");
			System.out.println("Total matches: " + stats.totalMatches());
			System.out.println("  - Auto (email): " + stats.autoEmailMatches());
			System.out.println("  - Auto (name): " + stats.autoNameMatches());
			System.out.println("  - Manual: " + stats.manualMatches());

			List<Person> unmatchedPre = matchingService.getUnmatchedPre();
			List<Person> unmatchedPost = matchingService.getUnmatchedPost();
			System.out.println("Unmatched pre-survey: " + unmatchedPre.size());
			System.out.println("Unmatched post-survey: " + unmatchedPost.size());
		}

		private void showUnmatched()
		{
			List<Person> unmatchedPre = matchingService.getUnmatchedPre();
			List<Person> unmatchedPost = matchingService.getUnmatchedPost();

			System.out.println("=== Unmatched Pre-Survey (" + unmatchedPre.size() + ") ===");
			for (Person p : unmatchedPre)
			{
				System.out.printf("  [%d] %s | %s | %s%n", p.getId(), p.getCohort(), p.getName(), p.getEmail());
			}

			System.out.println();
			System.out.println("=== Unmatched Post-Survey (" + unmatchedPost.size() + ") ===");
			for (Person p : unmatchedPost)
			{
				System.out.printf("  [%d] %s | %s | %s%n", p.getId(), p.getCohort(), p.getName(), p.getEmail());
			}
		}
	}

	/**
	 * Analyze survey data.
	 */
	@Command(name = "analyze", description = "Analyze survey data and generate statistics")
	static class AnalyzeCommand implements Callable<Integer>
	{

		private final AnalysisService analysisService;

		/**
		 * Creates a new AnalyzeCommand with injected AnalysisService.
		 *
		 * @param analysisService
		 *            the analysis service
		 */
		@Inject
		public AnalyzeCommand(AnalysisService analysisService)
		{
			this.analysisService = analysisService;
		}

		@Override
		public Integer call()
		{
			System.out.println("Analyzing survey data...");
			AnalysisResult result = analysisService.analyze();

			System.out.println();
			System.out.println("=== Survey Analysis Results ===");
			System.out.println();
			System.out.println("Response Counts:");
			System.out.println("  Pre-survey responses: " + result.preCount());
			System.out.println("  Post-survey responses: " + result.postCount());
			System.out.println("  Matched pairs: " + result.matchedCount());
			System.out.println();
			System.out.println("Cohorts: " + String.join(", ", result.cohorts()));
			System.out.println();
			System.out.println("Pre-Survey Averages (1-4 scale):");
			System.out.println("  Speaking confidence: " + result.avgPreSpeaking());
			System.out.println("  Understanding confidence: " + result.avgPreUnderstanding());
			System.out.println();
			System.out.println("Post-Survey Averages (1-5 scale):");
			System.out.println("  Speaking ability: " + result.avgPostSpeaking());
			System.out.println("  Difficulty expressing: " + result.avgPostDifficulty());
			System.out.println();
			System.out.println("Matched Pair Analysis:");
			System.out.println("  Average speaking change (Pre Q7 vs Post Q6): " + result.avgSpeakingChange());
			System.out.println("  Average understanding change (Pre Q9 vs Post Q7): " + result.avgUnderstandingChange());
			System.out.println();

			if (!result.matchedPairAnalyses().isEmpty())
			{
				System.out.println("Individual Matched Pairs:");
				System.out.println("Cohort | Name | Pre Speaking | Post Speaking | Speaking Change | Understanding Change");
				System.out.println("-".repeat(90));
				for (var analysis : result.matchedPairAnalyses())
				{
					System.out.printf("%s | %s | %s | %s | %s | %s%n", analysis.cohort(), truncate(analysis.name(), 20),
						analysis.preSpeakingConfidence(), analysis.postSpeakingAbility(), analysis.speakingChange(),
						analysis.understandingChange());
				}
			}

			return 0;
		}

		private String truncate(String s, int maxLen)
		{
			if (s == null)
			{
				return "";
			}
			return s.length() > maxLen ? s.substring(0, maxLen - 3) + "..." : s;
		}
	}

	/**
	 * Reload all data: init database, import surveys, and run matching.
	 */
	@Command(name = "reload", description = "Initialize database, import all surveys from data/ directory, and run auto-matching")
	static class ReloadCommand implements Callable<Integer>
	{

		private final Flyway flyway;
		private final ImportService importService;
		private final MatchingService matchingService;

		@Option(names = { "--pre" }, description = "Path to pre-survey directory", defaultValue = "data/pre")
		private Path prePath;

		@Option(names = { "--post" }, description = "Path to post-survey directory", defaultValue = "data/post")
		private Path postPath;

		/**
		 * Creates a new ReloadCommand with injected dependencies.
		 *
		 * @param flyway
		 *            the Flyway instance for migrations
		 * @param importService
		 *            the import service
		 * @param matchingService
		 *            the matching service
		 */
		@Inject
		public ReloadCommand(Flyway flyway, ImportService importService, MatchingService matchingService)
		{
			this.flyway = flyway;
			this.importService = importService;
			this.matchingService = matchingService;
		}

		@Override
		public Integer call()
		{
			try
			{
				// Step 1: Initialize database
				System.out.println("=== Step 1: Initializing database ===");
				flyway.migrate();
				System.out.println("Database initialized successfully.");
				System.out.println();

				// Step 2: Import surveys
				System.out.println("=== Step 2: Importing surveys ===");
				int preCount = importDirectory(prePath, true);
				int postCount = importDirectory(postPath, false);
				System.out.println("Imported " + preCount + " pre-survey and " + postCount + " post-survey records.");
				System.out.println();

				// Step 3: Run automatic matching
				System.out.println("=== Step 3: Running automatic matching ===");
				MatchingService.MatchResult result = matchingService.runAutoMatch();
				System.out.println("Email matches: " + result.emailMatches());
				System.out.println("Name matches: " + result.nameMatches());
				System.out.println("Total new matches: " + result.totalMatches());
				System.out.println();

				System.out.println("=== Reload complete ===");
				return 0;

			}
			catch (Exception e)
			{
				System.err.println("Error during reload: " + e.getMessage());
				e.printStackTrace();
				return 1;
			}
		}

		/**
		 * Imports all Excel files from a directory.
		 *
		 * @param dir
		 *            the directory path
		 * @param isPreSurvey
		 *            true for pre-survey, false for post-survey
		 * @return number of imported records
		 * @throws IOException
		 *             if import fails
		 */
		private int importDirectory(Path dir, boolean isPreSurvey) throws IOException
		{
			String type = isPreSurvey ? "pre-survey" : "post-survey";

			if (!Files.exists(dir))
			{
				System.out.println("Directory not found: " + dir + ", skipping " + type + " import.");
				return 0;
			}

			System.out.println("Importing " + type + " data from directory: " + dir);

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
				System.out.println("No .xlsx files found in " + dir);
				return 0;
			}

			System.out.println("Found " + excelFiles.size() + " Excel file(s)");

			int total = 0;
			for (Path file : excelFiles)
			{
				int count = isPreSurvey
					? importService.importPreSurvey(file)
					: importService.importPostSurvey(file);
				System.out.println("  Imported " + count + " records from " + file.getFileName());
				total += count;
			}
			return total;
		}
	}
}
