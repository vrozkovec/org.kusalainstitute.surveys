package org.kusalainstitute.surveys.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kusalainstitute.surveys.pojo.enums.ConfidenceLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for parsing Excel survey files. Provides common utilities for cell value extraction.
 */
public abstract class ExcelParser
{

	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Opens an Excel workbook from a file path.
	 *
	 * @param filePath
	 *            path to the Excel file
	 * @return the opened Workbook
	 * @throws IOException
	 *             if file cannot be read
	 */
	protected Workbook openWorkbook(Path filePath) throws IOException
	{
		log.info("Opening Excel file: {}", filePath);
		return new XSSFWorkbook(new FileInputStream(filePath.toFile()));
	}

	/**
	 * Gets the first sheet from a workbook.
	 *
	 * @param workbook
	 *            the workbook
	 * @return the first sheet
	 */
	protected Sheet getFirstSheet(Workbook workbook)
	{
		return workbook.getSheetAt(0);
	}

	/**
	 * Gets a string value from a cell, handling various cell types.
	 *
	 * @param row
	 *            the row
	 * @param cellIndex
	 *            the cell index (0-based)
	 * @return the string value or null if empty
	 */
	protected String getStringValue(Row row, int cellIndex)
	{
		if (row == null)
		{
			return null;
		}
		Cell cell = row.getCell(cellIndex);
		if (cell == null)
		{
			return null;
		}

		String value = switch (cell.getCellType())
		{
			case STRING -> cell.getStringCellValue();
			case NUMERIC -> {
				if (DateUtil.isCellDateFormatted(cell))
				{
					yield cell.getLocalDateTimeCellValue().toString();
				}
				// Avoid scientific notation for numbers
				double numValue = cell.getNumericCellValue();
				if (numValue == Math.floor(numValue))
				{
					yield String.valueOf((long)numValue);
				}
				yield String.valueOf(numValue);
			}
			case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
			case FORMULA -> {
				try
				{
					yield cell.getStringCellValue();
				}
				catch (Exception e)
				{
					try
					{
						yield String.valueOf(cell.getNumericCellValue());
					}
					catch (Exception e2)
					{
						yield null;
					}
				}
			}
			case BLANK, ERROR, _NONE -> null;
		};

		return StringUtils.isBlank(value) ? null : value.trim();
	}

	/**
	 * Gets a LocalDateTime value from a cell.
	 *
	 * @param row
	 *            the row
	 * @param cellIndex
	 *            the cell index (0-based)
	 * @return the LocalDateTime value or null
	 */
	protected LocalDateTime getDateTimeValue(Row row, int cellIndex)
	{
		if (row == null)
		{
			return null;
		}
		Cell cell = row.getCell(cellIndex);
		if (cell == null)
		{
			return null;
		}

		try
		{
			if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell))
			{
				return cell.getLocalDateTimeCellValue();
			}
			else if (cell.getCellType() == CellType.STRING)
			{
				// Try to parse common date formats
				String dateStr = cell.getStringCellValue();
				return parseDateTime(dateStr);
			}
		}
		catch (Exception e)
		{
			log.warn("Could not parse date from cell at index {}: {}", cellIndex, e.getMessage());
		}
		return null;
	}

	/**
	 * Parses a date string in common formats.
	 *
	 * @param dateStr
	 *            the date string
	 * @return LocalDateTime or null if unparseable
	 */
	protected LocalDateTime parseDateTime(String dateStr)
	{
		if (StringUtils.isBlank(dateStr))
		{
			return null;
		}

		// Handle common date formats: M/d/yyyy H:mm:ss or similar
		try
		{
			// Try parsing with DateTimeFormatter patterns
			String[] parts = dateStr.split(" ");
			if (parts.length >= 2)
			{
				String[] dateParts = parts[0].split("/");
				String[] timeParts = parts[1].split(":");

				if (dateParts.length >= 3 && timeParts.length >= 2)
				{
					int month = Integer.parseInt(dateParts[0]);
					int day = Integer.parseInt(dateParts[1]);
					int year = Integer.parseInt(dateParts[2]);
					int hour = Integer.parseInt(timeParts[0]);
					int minute = Integer.parseInt(timeParts[1]);
					int second = timeParts.length > 2 ? Integer.parseInt(timeParts[2]) : 0;

					return LocalDateTime.of(year, month, day, hour, minute, second);
				}
			}
		}
		catch (Exception e)
		{
			log.trace("Could not parse date string '{}': {}", dateStr, e.getMessage());
		}
		return null;
	}

	/**
	 * Gets an integer confidence level value from a cell. Parses French confidence labels to
	 * numeric values (1-4).
	 *
	 * @param row
	 *            the row
	 * @param cellIndex
	 *            the cell index (0-based)
	 * @return confidence value (1-4) or null
	 */
	protected Integer getConfidenceValue(Row row, int cellIndex)
	{
		String text = getStringValue(row, cellIndex);
		if (StringUtils.isBlank(text))
		{
			return null;
		}

		ConfidenceLevel level = ConfidenceLevel.fromFrench(text);
		if (level == null)
		{
			level = ConfidenceLevel.fromEnglish(text);
		}

		return level != null ? level.getValue() : null;
	}

	/**
	 * Determines if a row is a header row based on content.
	 *
	 * @param row
	 *            the row to check
	 * @return true if this appears to be a header row
	 */
	protected boolean isHeaderRow(Row row)
	{
		if (row == null)
		{
			return false;
		}
		String firstCell = getStringValue(row, 0);
		String secondCell = getStringValue(row, 1);

		// Check for common header patterns
		if (firstCell != null && (firstCell.toLowerCase().contains("column")))
		{
			return true;
		}
		if (secondCell != null && (secondCell.toLowerCase().contains("column") || secondCell.equalsIgnoreCase("timestamp")))
		{
			return true;
		}
		return false;
	}

	/**
	 * Checks if a row is empty (all cells null or blank).
	 *
	 * @param row
	 *            the row to check
	 * @return true if row is empty
	 */
	protected boolean isEmptyRow(Row row)
	{
		if (row == null)
		{
			return true;
		}
		for (int i = 0; i < 10; i++)
		{
			String value = getStringValue(row, i);
			if (StringUtils.isNotBlank(value))
			{
				return false;
			}
		}
		return true;
	}
}
