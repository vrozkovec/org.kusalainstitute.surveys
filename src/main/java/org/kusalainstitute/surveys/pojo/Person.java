package org.kusalainstitute.surveys.pojo;

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;

/**
 * Represents a survey respondent. A person is uniquely identified by cohort + survey type +
 * email/name combination.
 */
public class Person
{

	private Long id;
	private String cohort;
	private String email;
	private String name;
	private String normalizedEmail;
	private boolean requiresManualMatch;
	private SurveyType surveyType;
	private LocalDateTime createdAt;

	public Person()
	{
		this.createdAt = LocalDateTime.now();
	}

	/**
	 * Creates a new Person with the given parameters.
	 *
	 * @param cohort
	 *            the cohort code (YTI, YTC, etc.)
	 * @param email
	 *            the email address
	 * @param name
	 *            the person's name
	 * @param surveyType
	 *            PRE or POST survey
	 */
	public Person(String cohort, String email, String name, SurveyType surveyType)
	{
		this();
		this.cohort = cohort;
		this.email = email;
		this.name = name;
		this.surveyType = surveyType;
		this.normalizedEmail = normalizeEmail(email);
		this.requiresManualMatch = "?".equals(cohort);
	}

	/**
	 * Normalizes an email address for matching purposes. Converts to lowercase and removes extra
	 * whitespace.
	 *
	 * @param email
	 *            the email to normalize
	 * @return normalized email or null if blank
	 */
	public static String normalizeEmail(String email)
	{
		if (StringUtils.isBlank(email))
		{
			return null;
		}
		return email.toLowerCase().trim().replaceAll("\\s+", "");
	}

	// Getters and setters

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getCohort()
	{
		return cohort;
	}

	public void setCohort(String cohort)
	{
		this.cohort = cohort;
		this.requiresManualMatch = "?".equals(cohort);
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
		this.normalizedEmail = normalizeEmail(email);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getNormalizedEmail()
	{
		return normalizedEmail;
	}

	public void setNormalizedEmail(String normalizedEmail)
	{
		this.normalizedEmail = normalizedEmail;
	}

	public boolean isRequiresManualMatch()
	{
		return requiresManualMatch;
	}

	public void setRequiresManualMatch(boolean requiresManualMatch)
	{
		this.requiresManualMatch = requiresManualMatch;
	}

	public SurveyType getSurveyType()
	{
		return surveyType;
	}

	public void setSurveyType(SurveyType surveyType)
	{
		this.surveyType = surveyType;
	}

	public LocalDateTime getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt)
	{
		this.createdAt = createdAt;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		Person person = (Person)o;
		return Objects.equals(id, person.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "Person{" + "id=" + id + ", cohort='" + cohort + '\'' + ", email='" + email + '\'' + ", name='" + name + '\''
			+ ", surveyType=" + surveyType + '}';
	}
}
