package org.kusalainstitute.surveys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

import org.kusalainstitute.surveys.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base DAO class providing common database operations.
 */
public abstract class BaseDao
{

	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected final DatabaseConfig dbConfig;

	protected BaseDao()
	{
		this.dbConfig = DatabaseConfig.getInstance();
	}

	/**
	 * Gets a database connection from the pool.
	 *
	 * @return a database connection
	 * @throws SQLException
	 *             if connection cannot be obtained
	 */
	protected Connection getConnection() throws SQLException
	{
		return dbConfig.getConnection();
	}

	/**
	 * Sets a nullable string parameter on a prepared statement.
	 *
	 * @param stmt
	 *            the prepared statement
	 * @param index
	 *            the parameter index
	 * @param value
	 *            the string value (can be null)
	 * @throws SQLException
	 *             if parameter cannot be set
	 */
	protected void setNullableString(PreparedStatement stmt, int index, String value) throws SQLException
	{
		if (value != null)
		{
			stmt.setString(index, value);
		}
		else
		{
			stmt.setNull(index, Types.VARCHAR);
		}
	}

	/**
	 * Sets a nullable integer parameter on a prepared statement.
	 *
	 * @param stmt
	 *            the prepared statement
	 * @param index
	 *            the parameter index
	 * @param value
	 *            the integer value (can be null)
	 * @throws SQLException
	 *             if parameter cannot be set
	 */
	protected void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException
	{
		if (value != null)
		{
			stmt.setInt(index, value);
		}
		else
		{
			stmt.setNull(index, Types.INTEGER);
		}
	}

	/**
	 * Sets a nullable timestamp parameter from a LocalDateTime.
	 *
	 * @param stmt
	 *            the prepared statement
	 * @param index
	 *            the parameter index
	 * @param value
	 *            the LocalDateTime value (can be null)
	 * @throws SQLException
	 *             if parameter cannot be set
	 */
	protected void setNullableTimestamp(PreparedStatement stmt, int index, LocalDateTime value) throws SQLException
	{
		if (value != null)
		{
			stmt.setTimestamp(index, Timestamp.valueOf(value));
		}
		else
		{
			stmt.setNull(index, Types.TIMESTAMP);
		}
	}

	/**
	 * Sets a nullable decimal parameter.
	 *
	 * @param stmt
	 *            the prepared statement
	 * @param index
	 *            the parameter index
	 * @param value
	 *            the BigDecimal value (can be null)
	 * @throws SQLException
	 *             if parameter cannot be set
	 */
	protected void setNullableBigDecimal(PreparedStatement stmt, int index, java.math.BigDecimal value) throws SQLException
	{
		if (value != null)
		{
			stmt.setBigDecimal(index, value);
		}
		else
		{
			stmt.setNull(index, Types.DECIMAL);
		}
	}

	/**
	 * Gets a nullable string from a result set.
	 *
	 * @param rs
	 *            the result set
	 * @param columnName
	 *            the column name
	 * @return the string value or null
	 * @throws SQLException
	 *             if value cannot be retrieved
	 */
	protected String getNullableString(ResultSet rs, String columnName) throws SQLException
	{
		return rs.getString(columnName);
	}

	/**
	 * Gets a nullable integer from a result set.
	 *
	 * @param rs
	 *            the result set
	 * @param columnName
	 *            the column name
	 * @return the integer value or null
	 * @throws SQLException
	 *             if value cannot be retrieved
	 */
	protected Integer getNullableInt(ResultSet rs, String columnName) throws SQLException
	{
		int value = rs.getInt(columnName);
		return rs.wasNull() ? null : value;
	}

	/**
	 * Gets a nullable LocalDateTime from a result set timestamp column.
	 *
	 * @param rs
	 *            the result set
	 * @param columnName
	 *            the column name
	 * @return the LocalDateTime value or null
	 * @throws SQLException
	 *             if value cannot be retrieved
	 */
	protected LocalDateTime getNullableTimestamp(ResultSet rs, String columnName) throws SQLException
	{
		Timestamp timestamp = rs.getTimestamp(columnName);
		return timestamp != null ? timestamp.toLocalDateTime() : null;
	}

	/**
	 * Gets the generated key from an insert statement.
	 *
	 * @param stmt
	 *            the statement after execution
	 * @return the generated key
	 * @throws SQLException
	 *             if key cannot be retrieved
	 */
	protected Long getGeneratedKey(Statement stmt) throws SQLException
	{
		try (ResultSet generatedKeys = stmt.getGeneratedKeys())
		{
			if (generatedKeys.next())
			{
				return generatedKeys.getLong(1);
			}
			throw new SQLException("Failed to obtain generated key");
		}
	}
}
