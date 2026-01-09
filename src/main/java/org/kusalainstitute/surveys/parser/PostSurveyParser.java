package org.kusalainstitute.surveys.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;

/**
 * Parser for post-survey Excel files.
 *
 * Expected column layout (0-indexed): 0: Column indicator (post) 1: Cohort (YTI, YTC, etc.) 2:
 * Timestamp 3: Email Address 4: Name:Email Address 5: Q1 - App usage duration 6: Q2 - Time per
 * session 7: Q3 - App frequency 8: Q4 - Progress assessment 9: Q5 - What helped most 10-20: Q6 -
 * Speaking ability (11 situations) 21-42: Q7 - Difficulty expressing (11 free-text fields) 43: Q8 -
 * Most difficult overall 44: Q9 - Most difficult for job 45: Q10 - Emotional difficulties 46: Q11 -
 * Avoided situations 47: Q12 - Has enough support 48: Q13 - Desired resources 49: Q14 - Willing to
 * interview 50: Q15 - Interview decline reason 51: Q16 - Preferred interview type 52: Contact info
 * 53: Q17 - Additional comments
 */
public class PostSurveyParser extends ExcelParser
{

	// Column indices (0-based)
	private static final int COL_INDICATOR = 0;
	private static final int COL_COHORT = 1;
	private static final int COL_TIMESTAMP = 2;
	private static final int COL_EMAIL = 3;
	private static final int COL_NAME_EMAIL = 4;
	private static final int COL_APP_DURATION = 5;
	private static final int COL_TIME_PER_SESSION = 6;
	private static final int COL_FREQUENCY = 7;
	private static final int COL_PROGRESS = 8;
	private static final int COL_WHAT_HELPED = 9;

	// Speaking ability Q6: columns 10-20
	private static final int COL_SPEAK_START = 10;

	// Difficulty Q7: columns 21-31 (11 free-text fields)
	private static final int COL_DIFFICULTY_START = 21;

	// Remaining questions
	private static final int COL_DIFFICULT_OVERALL = 32;
	private static final int COL_DIFFICULT_JOB = 33;
	private static final int COL_EMOTIONAL = 34;
	private static final int COL_AVOIDED = 35;
	private static final int COL_SUPPORT = 36;
	private static final int COL_RESOURCES = 37;
	private static final int COL_WILLING_INTERVIEW = 38;
	private static final int COL_DECLINE_REASON = 39;
	private static final int COL_INTERVIEW_TYPE = 40;
	private static final int COL_CONTACT = 41;
	private static final int COL_ADDITIONAL = 42;

	/**
	 * Parses a post-survey Excel file.
	 *
	 * @param filePath
	 *            path to the Excel file
	 * @return list of parsed results (person + response pairs)
	 * @throws IOException
	 *             if file cannot be read
	 */
	public List<ParsedPostSurvey> parse(Path filePath) throws IOException
	{
		List<ParsedPostSurvey> results = new ArrayList<>();
		String fileName = filePath.getFileName().toString();

		try (Workbook workbook = openWorkbook(filePath))
		{
			Sheet sheet = getFirstSheet(workbook);
			int rowNum = 0;
			int skippedRows = 0;
			int parsedRows = 0;

			for (Row row : sheet)
			{
				rowNum++;

				// Skip header rows and empty rows
				if (isHeaderRow(row) || isEmptyRow(row))
				{
					skippedRows++;
					continue;
				}

				// Check if this is a post-survey row
				String indicator = getStringValue(row, COL_INDICATOR);
				if (!"post".equalsIgnoreCase(indicator))
				{
					skippedRows++;
					continue;
				}

				try
				{
					ParsedPostSurvey parsed = parseRow(row, rowNum, fileName);
					if (parsed != null)
					{
						results.add(parsed);
						parsedRows++;
					}
				}
				catch (Exception e)
				{
					log.warn("Error parsing row {}: {}", rowNum, e.getMessage());
				}
			}

			log.info("Parsed {} post-survey responses from {} (skipped {} rows)", parsedRows, fileName, skippedRows);
		}

		return results;
	}

	private ParsedPostSurvey parseRow(Row row, int rowNum, String fileName)
	{
		// Extract cohort
		String cohort = getStringValue(row, COL_COHORT);
		if (cohort == null)
		{
			log.warn("Row {} has no cohort value, skipping", rowNum);
			return null;
		}

		// Extract person info
		String email = getStringValue(row, COL_EMAIL);
		String nameEmail = getStringValue(row, COL_NAME_EMAIL);
		String name = extractName(nameEmail);

		// Create person
		Person person = new Person(cohort, email, name, SurveyType.POST);

		// Create response
		PostSurveyResponse response = new PostSurveyResponse();
		response.setTimestamp(getDateTimeValue(row, COL_TIMESTAMP));
		response.setSourceFile(fileName);
		response.setRowNumber(rowNum);

		// App usage Q1-Q4
		response.setAppUsageDuration(getStringValue(row, COL_APP_DURATION));
		response.setAppTimePerSession(getStringValue(row, COL_TIME_PER_SESSION));
		response.setAppFrequency(getStringValue(row, COL_FREQUENCY));
		response.setProgressAssessment(getStringValue(row, COL_PROGRESS));

		// Q5 - What helped most
		response.setWhatHelpedMostOriginal(getStringValue(row, COL_WHAT_HELPED));

		// Speaking ability (Q6) - 11 situations
		response.setSpeakDirections(getConfidenceValue(row, COL_SPEAK_START));
		response.setSpeakHealthcare(getConfidenceValue(row, COL_SPEAK_START + 1));
		response.setSpeakAuthorities(getConfidenceValue(row, COL_SPEAK_START + 2));
		response.setSpeakJobInterview(getConfidenceValue(row, COL_SPEAK_START + 3));
		response.setSpeakInformal(getConfidenceValue(row, COL_SPEAK_START + 4));
		response.setSpeakChildrenEducation(getConfidenceValue(row, COL_SPEAK_START + 5));
		response.setSpeakLandlord(getConfidenceValue(row, COL_SPEAK_START + 6));
		response.setSpeakSocialEvents(getConfidenceValue(row, COL_SPEAK_START + 7));
		response.setSpeakLocalServices(getConfidenceValue(row, COL_SPEAK_START + 8));
		response.setSpeakSupportOrgs(getConfidenceValue(row, COL_SPEAK_START + 9));
		response.setSpeakShopping(getConfidenceValue(row, COL_SPEAK_START + 10));

		// Difficulty expressing (Q7) - 11 free-text fields
		response.setDifficultyDirectionsOriginal(getStringValue(row, COL_DIFFICULTY_START));
		response.setDifficultyHealthcareOriginal(getStringValue(row, COL_DIFFICULTY_START + 1));
		response.setDifficultyAuthoritiesOriginal(getStringValue(row, COL_DIFFICULTY_START + 2));
		response.setDifficultyJobInterviewOriginal(getStringValue(row, COL_DIFFICULTY_START + 3));
		response.setDifficultyInformalOriginal(getStringValue(row, COL_DIFFICULTY_START + 4));
		response.setDifficultyChildrenEducationOriginal(getStringValue(row, COL_DIFFICULTY_START + 5));
		response.setDifficultyLandlordOriginal(getStringValue(row, COL_DIFFICULTY_START + 6));
		response.setDifficultySocialEventsOriginal(getStringValue(row, COL_DIFFICULTY_START + 7));
		response.setDifficultyLocalServicesOriginal(getStringValue(row, COL_DIFFICULTY_START + 8));
		response.setDifficultySupportOrgsOriginal(getStringValue(row, COL_DIFFICULTY_START + 9));
		response.setDifficultyShoppingOriginal(getStringValue(row, COL_DIFFICULTY_START + 10));

		// Remaining questions Q8-Q17
		response.setMostDifficultOverallOriginal(getStringValue(row, COL_DIFFICULT_OVERALL));
		response.setMostDifficultForJobOriginal(getStringValue(row, COL_DIFFICULT_JOB));
		response.setEmotionalDifficultiesOriginal(getStringValue(row, COL_EMOTIONAL));
		response.setAvoidedSituationsOriginal(getStringValue(row, COL_AVOIDED));
		response.setHasEnoughSupport(getStringValue(row, COL_SUPPORT));
		response.setDesiredResourcesOriginal(getStringValue(row, COL_RESOURCES));
		response.setWillingToInterview(getStringValue(row, COL_WILLING_INTERVIEW));
		response.setInterviewDeclineReasonOriginal(getStringValue(row, COL_DECLINE_REASON));
		response.setPreferredInterviewType(getStringValue(row, COL_INTERVIEW_TYPE));
		response.setContactInfo(getStringValue(row, COL_CONTACT));
		response.setAdditionalCommentsOriginal(getStringValue(row, COL_ADDITIONAL));

		// Calculate averages
		response.calculateAverages();

		return new ParsedPostSurvey(person, response);
	}

	/**
	 * Extracts name from a combined "Name:Email" field.
	 *
	 * @param nameEmail
	 *            the combined field value
	 * @return extracted name or the original value
	 */
	private String extractName(String nameEmail)
	{
		if (nameEmail == null)
		{
			return null;
		}

		// Try to extract just the name (before the email)
		int atIndex = nameEmail.indexOf('@');
		if (atIndex > 0)
		{
			String beforeAt = nameEmail.substring(0, atIndex);
			int lastSpace = beforeAt.lastIndexOf(' ');
			if (lastSpace > 0)
			{
				return nameEmail.substring(0, lastSpace).trim();
			}
		}

		return nameEmail;
	}

	/**
	 * Container for parsed post-survey data.
	 */
	public record ParsedPostSurvey(Person person, PostSurveyResponse response)
	{
	}
}
