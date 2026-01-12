package org.kusalainstitute.surveys.pojo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

import org.kusalainstitute.surveys.pojo.enums.AppFrequency;
import org.kusalainstitute.surveys.pojo.enums.AppTimePerSession;
import org.kusalainstitute.surveys.pojo.enums.InterviewTypePreference;
import org.kusalainstitute.surveys.pojo.enums.ProgressAssessment;
import org.kusalainstitute.surveys.pojo.enums.YesNo;

/**
 * Post-survey response data collected after using the Latudio app. Contains app usage information,
 * English ability levels, and qualitative feedback.
 */
public class PostSurveyResponse
{

	private Long id;
	private Long personId;
	private LocalDateTime timestamp;
	private String sourceFile;
	private Integer rowNumber;

	// App usage Q1-Q4
	private AppTimePerSession appUsageDuration;
	private AppTimePerSession appTimePerSession;
	private AppFrequency appFrequency;
	private ProgressAssessment progressAssessment;

	// Q5 Free text
	private String whatHelpedMostOriginal;
	private String whatHelpedMostTranslated;

	// Speaking ability Q6 (11 situations) - 1-4 scale
	private Integer speakDirections;
	private Integer speakHealthcare;
	private Integer speakAuthorities;
	private Integer speakJobInterview;
	private Integer speakInformal;
	private Integer speakChildrenEducation;
	private Integer speakLandlord;
	private Integer speakSocialEvents;
	private Integer speakLocalServices;
	private Integer speakSupportOrgs;
	private Integer speakShopping;

	// Q7 Difficulty expressing (11 situations) - 1-5 scale
	private Integer difficultyDirections;
	private Integer difficultyHealthcare;
	private Integer difficultyAuthorities;
	private Integer difficultyJobInterview;
	private Integer difficultyInformal;
	private Integer difficultyChildrenEducation;
	private Integer difficultyLandlord;
	private Integer difficultySocialEvents;
	private Integer difficultyLocalServices;
	private Integer difficultySupportOrgs;
	private Integer difficultyShopping;

	// Q8 Free text
	private String mostDifficultOverallOriginal;
	private String mostDifficultOverallTranslated;

	// Q9 Free text
	private String mostDifficultForJobOriginal;
	private String mostDifficultForJobTranslated;

	// Q10 Free text
	private String emotionalDifficultiesOriginal;
	private String emotionalDifficultiesTranslated;

	// Q11 Free text
	private String avoidedSituationsOriginal;
	private String avoidedSituationsTranslated;

	// Q12
	private String hasEnoughSupport;

	// Q13 Free text
	private String desiredResourcesOriginal;
	private String desiredResourcesTranslated;

	// Q14-16 Interview questions
	private YesNo willingToInterview;
	private String interviewDeclineReasonOriginal;
	private String interviewDeclineReasonTranslated;
	private InterviewTypePreference preferredInterviewType;
	private String contactInfo;

	// Q17 Free text
	private String additionalCommentsOriginal;
	private String additionalCommentsTranslated;

	// Calculated averages
	private BigDecimal avgSpeakingAbility;
	private BigDecimal avgDifficultyExpressing;

	public PostSurveyResponse()
	{
	}

	/**
	 * Calculates and sets the average speaking ability and difficulty expressing from all 11 situations.
	 */
	public void calculateAverages()
	{
		this.avgSpeakingAbility = calculateSpeakingAverage();
		this.avgDifficultyExpressing = calculateDifficultyAverage();
	}

	/**
	 * Calculates the average speaking ability.
	 *
	 * @return average of all non-null speaking ability values, or null if all are null
	 */
	public BigDecimal calculateSpeakingAverage()
	{
		int sum = 0;
		int count = 0;

		Integer[] values = { speakDirections, speakHealthcare, speakAuthorities, speakJobInterview, speakInformal,
				speakChildrenEducation, speakLandlord, speakSocialEvents, speakLocalServices, speakSupportOrgs, speakShopping };

		for (Integer value : values)
		{
			if (value != null)
			{
				sum += value;
				count++;
			}
		}

		if (count == 0)
		{
			return null;
		}

		return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
	}

	/**
	 * Calculates the average difficulty expressing.
	 *
	 * @return average of all non-null difficulty expressing values, or null if all are null
	 */
	public BigDecimal calculateDifficultyAverage()
	{
		int sum = 0;
		int count = 0;

		Integer[] values = { difficultyDirections, difficultyHealthcare, difficultyAuthorities, difficultyJobInterview,
				difficultyInformal, difficultyChildrenEducation, difficultyLandlord, difficultySocialEvents,
				difficultyLocalServices, difficultySupportOrgs, difficultyShopping };

		for (Integer value : values)
		{
			if (value != null)
			{
				sum += value;
				count++;
			}
		}

		if (count == 0)
		{
			return null;
		}

		return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
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

	public Long getPersonId()
	{
		return personId;
	}

	public void setPersonId(Long personId)
	{
		this.personId = personId;
	}

	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getSourceFile()
	{
		return sourceFile;
	}

	public void setSourceFile(String sourceFile)
	{
		this.sourceFile = sourceFile;
	}

	public Integer getRowNumber()
	{
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber)
	{
		this.rowNumber = rowNumber;
	}

	public AppTimePerSession getAppUsageDuration()
	{
		return appUsageDuration;
	}

	public void setAppUsageDuration(AppTimePerSession appUsageDuration)
	{
		this.appUsageDuration = appUsageDuration;
	}

	public AppTimePerSession getAppTimePerSession()
	{
		return appTimePerSession;
	}

	public void setAppTimePerSession(AppTimePerSession appTimePerSession)
	{
		this.appTimePerSession = appTimePerSession;
	}

	public AppFrequency getAppFrequency()
	{
		return appFrequency;
	}

	public void setAppFrequency(AppFrequency appFrequency)
	{
		this.appFrequency = appFrequency;
	}

	public ProgressAssessment getProgressAssessment()
	{
		return progressAssessment;
	}

	public void setProgressAssessment(ProgressAssessment progressAssessment)
	{
		this.progressAssessment = progressAssessment;
	}

	public String getWhatHelpedMostOriginal()
	{
		return whatHelpedMostOriginal;
	}

	public void setWhatHelpedMostOriginal(String whatHelpedMostOriginal)
	{
		this.whatHelpedMostOriginal = whatHelpedMostOriginal;
	}

	public String getWhatHelpedMostTranslated()
	{
		return whatHelpedMostTranslated;
	}

	public void setWhatHelpedMostTranslated(String whatHelpedMostTranslated)
	{
		this.whatHelpedMostTranslated = whatHelpedMostTranslated;
	}

	public Integer getSpeakDirections()
	{
		return speakDirections;
	}

	public void setSpeakDirections(Integer speakDirections)
	{
		this.speakDirections = speakDirections;
	}

	public Integer getSpeakHealthcare()
	{
		return speakHealthcare;
	}

	public void setSpeakHealthcare(Integer speakHealthcare)
	{
		this.speakHealthcare = speakHealthcare;
	}

	public Integer getSpeakAuthorities()
	{
		return speakAuthorities;
	}

	public void setSpeakAuthorities(Integer speakAuthorities)
	{
		this.speakAuthorities = speakAuthorities;
	}

	public Integer getSpeakJobInterview()
	{
		return speakJobInterview;
	}

	public void setSpeakJobInterview(Integer speakJobInterview)
	{
		this.speakJobInterview = speakJobInterview;
	}

	public Integer getSpeakInformal()
	{
		return speakInformal;
	}

	public void setSpeakInformal(Integer speakInformal)
	{
		this.speakInformal = speakInformal;
	}

	public Integer getSpeakChildrenEducation()
	{
		return speakChildrenEducation;
	}

	public void setSpeakChildrenEducation(Integer speakChildrenEducation)
	{
		this.speakChildrenEducation = speakChildrenEducation;
	}

	public Integer getSpeakLandlord()
	{
		return speakLandlord;
	}

	public void setSpeakLandlord(Integer speakLandlord)
	{
		this.speakLandlord = speakLandlord;
	}

	public Integer getSpeakSocialEvents()
	{
		return speakSocialEvents;
	}

	public void setSpeakSocialEvents(Integer speakSocialEvents)
	{
		this.speakSocialEvents = speakSocialEvents;
	}

	public Integer getSpeakLocalServices()
	{
		return speakLocalServices;
	}

	public void setSpeakLocalServices(Integer speakLocalServices)
	{
		this.speakLocalServices = speakLocalServices;
	}

	public Integer getSpeakSupportOrgs()
	{
		return speakSupportOrgs;
	}

	public void setSpeakSupportOrgs(Integer speakSupportOrgs)
	{
		this.speakSupportOrgs = speakSupportOrgs;
	}

	public Integer getSpeakShopping()
	{
		return speakShopping;
	}

	public void setSpeakShopping(Integer speakShopping)
	{
		this.speakShopping = speakShopping;
	}

	public Integer getDifficultyDirections()
	{
		return difficultyDirections;
	}

	public void setDifficultyDirections(Integer difficultyDirections)
	{
		this.difficultyDirections = difficultyDirections;
	}

	public Integer getDifficultyHealthcare()
	{
		return difficultyHealthcare;
	}

	public void setDifficultyHealthcare(Integer difficultyHealthcare)
	{
		this.difficultyHealthcare = difficultyHealthcare;
	}

	public Integer getDifficultyAuthorities()
	{
		return difficultyAuthorities;
	}

	public void setDifficultyAuthorities(Integer difficultyAuthorities)
	{
		this.difficultyAuthorities = difficultyAuthorities;
	}

	public Integer getDifficultyJobInterview()
	{
		return difficultyJobInterview;
	}

	public void setDifficultyJobInterview(Integer difficultyJobInterview)
	{
		this.difficultyJobInterview = difficultyJobInterview;
	}

	public Integer getDifficultyInformal()
	{
		return difficultyInformal;
	}

	public void setDifficultyInformal(Integer difficultyInformal)
	{
		this.difficultyInformal = difficultyInformal;
	}

	public Integer getDifficultyChildrenEducation()
	{
		return difficultyChildrenEducation;
	}

	public void setDifficultyChildrenEducation(Integer difficultyChildrenEducation)
	{
		this.difficultyChildrenEducation = difficultyChildrenEducation;
	}

	public Integer getDifficultyLandlord()
	{
		return difficultyLandlord;
	}

	public void setDifficultyLandlord(Integer difficultyLandlord)
	{
		this.difficultyLandlord = difficultyLandlord;
	}

	public Integer getDifficultySocialEvents()
	{
		return difficultySocialEvents;
	}

	public void setDifficultySocialEvents(Integer difficultySocialEvents)
	{
		this.difficultySocialEvents = difficultySocialEvents;
	}

	public Integer getDifficultyLocalServices()
	{
		return difficultyLocalServices;
	}

	public void setDifficultyLocalServices(Integer difficultyLocalServices)
	{
		this.difficultyLocalServices = difficultyLocalServices;
	}

	public Integer getDifficultySupportOrgs()
	{
		return difficultySupportOrgs;
	}

	public void setDifficultySupportOrgs(Integer difficultySupportOrgs)
	{
		this.difficultySupportOrgs = difficultySupportOrgs;
	}

	public Integer getDifficultyShopping()
	{
		return difficultyShopping;
	}

	public void setDifficultyShopping(Integer difficultyShopping)
	{
		this.difficultyShopping = difficultyShopping;
	}

	public String getMostDifficultOverallOriginal()
	{
		return mostDifficultOverallOriginal;
	}

	public void setMostDifficultOverallOriginal(String mostDifficultOverallOriginal)
	{
		this.mostDifficultOverallOriginal = mostDifficultOverallOriginal;
	}

	public String getMostDifficultOverallTranslated()
	{
		return mostDifficultOverallTranslated;
	}

	public void setMostDifficultOverallTranslated(String mostDifficultOverallTranslated)
	{
		this.mostDifficultOverallTranslated = mostDifficultOverallTranslated;
	}

	public String getMostDifficultForJobOriginal()
	{
		return mostDifficultForJobOriginal;
	}

	public void setMostDifficultForJobOriginal(String mostDifficultForJobOriginal)
	{
		this.mostDifficultForJobOriginal = mostDifficultForJobOriginal;
	}

	public String getMostDifficultForJobTranslated()
	{
		return mostDifficultForJobTranslated;
	}

	public void setMostDifficultForJobTranslated(String mostDifficultForJobTranslated)
	{
		this.mostDifficultForJobTranslated = mostDifficultForJobTranslated;
	}

	public String getEmotionalDifficultiesOriginal()
	{
		return emotionalDifficultiesOriginal;
	}

	public void setEmotionalDifficultiesOriginal(String emotionalDifficultiesOriginal)
	{
		this.emotionalDifficultiesOriginal = emotionalDifficultiesOriginal;
	}

	public String getEmotionalDifficultiesTranslated()
	{
		return emotionalDifficultiesTranslated;
	}

	public void setEmotionalDifficultiesTranslated(String emotionalDifficultiesTranslated)
	{
		this.emotionalDifficultiesTranslated = emotionalDifficultiesTranslated;
	}

	public String getAvoidedSituationsOriginal()
	{
		return avoidedSituationsOriginal;
	}

	public void setAvoidedSituationsOriginal(String avoidedSituationsOriginal)
	{
		this.avoidedSituationsOriginal = avoidedSituationsOriginal;
	}

	public String getAvoidedSituationsTranslated()
	{
		return avoidedSituationsTranslated;
	}

	public void setAvoidedSituationsTranslated(String avoidedSituationsTranslated)
	{
		this.avoidedSituationsTranslated = avoidedSituationsTranslated;
	}

	public String getHasEnoughSupport()
	{
		return hasEnoughSupport;
	}

	public void setHasEnoughSupport(String hasEnoughSupport)
	{
		this.hasEnoughSupport = hasEnoughSupport;
	}

	public String getDesiredResourcesOriginal()
	{
		return desiredResourcesOriginal;
	}

	public void setDesiredResourcesOriginal(String desiredResourcesOriginal)
	{
		this.desiredResourcesOriginal = desiredResourcesOriginal;
	}

	public String getDesiredResourcesTranslated()
	{
		return desiredResourcesTranslated;
	}

	public void setDesiredResourcesTranslated(String desiredResourcesTranslated)
	{
		this.desiredResourcesTranslated = desiredResourcesTranslated;
	}

	public YesNo getWillingToInterview()
	{
		return willingToInterview;
	}

	public void setWillingToInterview(YesNo willingToInterview)
	{
		this.willingToInterview = willingToInterview;
	}

	public String getInterviewDeclineReasonOriginal()
	{
		return interviewDeclineReasonOriginal;
	}

	public void setInterviewDeclineReasonOriginal(String interviewDeclineReasonOriginal)
	{
		this.interviewDeclineReasonOriginal = interviewDeclineReasonOriginal;
	}

	public String getInterviewDeclineReasonTranslated()
	{
		return interviewDeclineReasonTranslated;
	}

	public void setInterviewDeclineReasonTranslated(String interviewDeclineReasonTranslated)
	{
		this.interviewDeclineReasonTranslated = interviewDeclineReasonTranslated;
	}

	public InterviewTypePreference getPreferredInterviewType()
	{
		return preferredInterviewType;
	}

	public void setPreferredInterviewType(InterviewTypePreference preferredInterviewType)
	{
		this.preferredInterviewType = preferredInterviewType;
	}

	public String getContactInfo()
	{
		return contactInfo;
	}

	public void setContactInfo(String contactInfo)
	{
		this.contactInfo = contactInfo;
	}

	public String getAdditionalCommentsOriginal()
	{
		return additionalCommentsOriginal;
	}

	public void setAdditionalCommentsOriginal(String additionalCommentsOriginal)
	{
		this.additionalCommentsOriginal = additionalCommentsOriginal;
	}

	public String getAdditionalCommentsTranslated()
	{
		return additionalCommentsTranslated;
	}

	public void setAdditionalCommentsTranslated(String additionalCommentsTranslated)
	{
		this.additionalCommentsTranslated = additionalCommentsTranslated;
	}

	public BigDecimal getAvgSpeakingAbility()
	{
		return avgSpeakingAbility;
	}

	public void setAvgSpeakingAbility(BigDecimal avgSpeakingAbility)
	{
		this.avgSpeakingAbility = avgSpeakingAbility;
	}

	public BigDecimal getAvgDifficultyExpressing()
	{
		return avgDifficultyExpressing;
	}

	public void setAvgDifficultyExpressing(BigDecimal avgDifficultyExpressing)
	{
		this.avgDifficultyExpressing = avgDifficultyExpressing;
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
		PostSurveyResponse that = (PostSurveyResponse)o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "PostSurveyResponse{" + "id=" + id + ", personId=" + personId + ", timestamp=" + timestamp + ", avgSpeakingAbility="
			+ avgSpeakingAbility + '}';
	}
}
