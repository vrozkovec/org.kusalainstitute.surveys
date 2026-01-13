package org.kusalainstitute.surveys.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.kusalainstitute.surveys.mapper.ChildrenAgeGroupMapper;
import org.kusalainstitute.surveys.mapper.HowFoundKusalaMapper;
import org.kusalainstitute.surveys.mapper.StudyDurationMapper;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;

/**
 * Parser for pre-survey Excel files.
 *
 * Expected column layout (0-indexed): 0: Column 36 (pre/post indicator) 1: Column 35 (cohort: YTI,
 * YTC, etc.) 2: Timestamp 3: Email Address 4: Name and email address 5: Q1 - How did you find out
 * about Kusala Institute 6: Q2 - Study with teacher duration 7: Q3 - Study on own duration 8: Q4 -
 * Children ages 9: Q5 - Most difficult thing 10: Q6 - Why improve English 11-21: Q7 - Speaking
 * confidence (11 situations) 22: Q8 - Other situations 23-33: Q9 - Understanding confidence (11
 * situations) 34: Q10 - Most difficult part 35: Q11 - Describe situations
 */
public class PreSurveyParser extends ExcelParser
{

	// Column indices (0-based)
	private static final int COL_INDICATOR = 0;
	private static final int COL_COHORT = 1;
	private static final int COL_TIMESTAMP = 2;
	private static final int COL_EMAIL = 3;
	private static final int COL_NAME_EMAIL = 4;
	private static final int COL_HOW_FOUND = 5;
	private static final int COL_STUDY_TEACHER = 6;
	private static final int COL_STUDY_OWN = 7;
	private static final int COL_CHILDREN_AGES = 8;
	private static final int COL_MOST_DIFFICULT = 9;
	private static final int COL_WHY_IMPROVE = 10;

	// Speaking confidence Q7: columns 11-21
	private static final int COL_SPEAK_START = 11;

	// Other situations Q8: column 22
	private static final int COL_OTHER_SITUATIONS = 22;

	// Understanding confidence Q9: columns 23-33
	private static final int COL_UNDERSTAND_START = 23;

	// Q10: column 34, Q11: column 35
	private static final int COL_DIFFICULT_PART = 34;
	private static final int COL_DESCRIBE_SITUATIONS = 35;

	// Mappers for enum conversion
	private final HowFoundKusalaMapper howFoundKusalaMapper = new HowFoundKusalaMapper();
	private final StudyDurationMapper studyDurationMapper = new StudyDurationMapper();
	private final ChildrenAgeGroupMapper childrenAgeGroupMapper = new ChildrenAgeGroupMapper();

	/**
	 * Parses a pre-survey Excel file.
	 *
	 * @param filePath
	 *            path to the Excel file
	 * @return list of parsed results (person + response pairs)
	 * @throws IOException
	 *             if file cannot be read
	 */
	public List<ParsedPreSurvey> parse(Path filePath) throws IOException
	{
		List<ParsedPreSurvey> results = new ArrayList<>();
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

				// Check if this is a pre-survey row
				String indicator = getStringValue(row, COL_INDICATOR);
				if (!"pre".equalsIgnoreCase(indicator))
				{
					skippedRows++;
					continue;
				}

				try
				{
					ParsedPreSurvey parsed = parseRow(row, rowNum, fileName);
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

			log.info("Parsed {} pre-survey responses from {} (skipped {} rows)", parsedRows, fileName, skippedRows);
		}

		return results;
	}

	private ParsedPreSurvey parseRow(Row row, int rowNum, String fileName)
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
		Person person = new Person(cohort, email, name, SurveyType.PRE);

		// Create response
		PreSurveyResponse response = new PreSurveyResponse();
		response.setTimestamp(getDateTimeValue(row, COL_TIMESTAMP));
		response.setSourceFile(fileName);
		response.setRowNumber(rowNum);

		// Demographics
		response.setHowFoundKusala(howFoundKusalaMapper.map(getStringValue(row, COL_HOW_FOUND)));
		response.setStudyWithTeacherDuration(studyDurationMapper.map(getStringValue(row, COL_STUDY_TEACHER)));
		response.setStudyOnOwnDuration(studyDurationMapper.map(getStringValue(row, COL_STUDY_OWN)));
		response.setChildrenAges(childrenAgeGroupMapper.mapMultiple(getStringValue(row, COL_CHILDREN_AGES)));
		response.setMostDifficultThingOriginal(getStringValue(row, COL_MOST_DIFFICULT));
		response.setWhyImproveEnglishOriginal(getStringValue(row, COL_WHY_IMPROVE));

		// Speaking confidence (Q7) - 11 situations
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

		// Other situations (Q8)
		response.setOtherSituationsOriginal(getStringValue(row, COL_OTHER_SITUATIONS));

		// Understanding confidence (Q9) - 11 situations
		response.setUnderstandDirections(getConfidenceValue(row, COL_UNDERSTAND_START));
		response.setUnderstandHealthcare(getConfidenceValue(row, COL_UNDERSTAND_START + 1));
		response.setUnderstandAuthorities(getConfidenceValue(row, COL_UNDERSTAND_START + 2));
		response.setUnderstandJobInterview(getConfidenceValue(row, COL_UNDERSTAND_START + 3));
		response.setUnderstandInformal(getConfidenceValue(row, COL_UNDERSTAND_START + 4));
		response.setUnderstandChildrenEducation(getConfidenceValue(row, COL_UNDERSTAND_START + 5));
		response.setUnderstandLandlord(getConfidenceValue(row, COL_UNDERSTAND_START + 6));
		response.setUnderstandSocialEvents(getConfidenceValue(row, COL_UNDERSTAND_START + 7));
		response.setUnderstandLocalServices(getConfidenceValue(row, COL_UNDERSTAND_START + 8));
		response.setUnderstandSupportOrgs(getConfidenceValue(row, COL_UNDERSTAND_START + 9));
		response.setUnderstandShopping(getConfidenceValue(row, COL_UNDERSTAND_START + 10));

		// Q10 and Q11
		response.setDifficultPartOriginal(getStringValue(row, COL_DIFFICULT_PART));
		response.setDescribeSituationsOriginal(getStringValue(row, COL_DESCRIBE_SITUATIONS));

		// Calculate averages
		response.calculateAverages();

		return new ParsedPreSurvey(person, response);
	}

	/**
	 * Extracts name from a combined "Name email" field. The field format is typically "FirstName
	 * LastName email@example.com"
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
			// Find where the email starts (first space before @)
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
	 * Container for parsed pre-survey data.
	 */
	public record ParsedPreSurvey(Person person, PreSurveyResponse response)
	{
	}
}
