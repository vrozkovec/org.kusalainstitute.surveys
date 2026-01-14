package org.kusalainstitute.surveys.pojo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import org.kusalainstitute.surveys.pojo.enums.ChildrenAgeGroup;
import org.kusalainstitute.surveys.pojo.enums.HowFoundKusala;
import org.kusalainstitute.surveys.pojo.enums.StudyDuration;

/**
 * Pre-survey response data collected before using the Latudio app. Contains demographics, English
 * confidence levels for speaking and understanding.
 */
public class PreSurveyResponse
{

	private Long id;
	private Long personId;
	private LocalDateTime timestamp;
	private String sourceFile;
	private Integer rowNumber;

	/**
	 * Q1: Comment avez-vous découvert l'Institut Kusala ?<br>
	 * How did you find out about Kusala Institute?
	 */
	private HowFoundKusala howFoundKusala;
	/**
	 * Q2: Depuis combien de temps étudiez-vous l'anglais avec un enseignant, soit en classe soit en ligne, de manière
	 * régulière ou irrégulière ?<br>
	 * How long have you been studying English with a teacher, either in class or online, regularly or irregularly?
	 */
	private StudyDuration studyWithTeacherDuration;
	/**
	 * Q3: Depuis combien de temps étudiez-vous l'anglais par vous-même avec un professeur particulier, un partenaire
	 * d'échange linguistique, des sites web ou une application, que ce soit de façon régulière ou irrégulière ?<br>
	 * How long have you been studying English on your own with a private teacher, language exchange partner, websites
	 * or an app, regularly or irregularly?
	 */
	private StudyDuration studyOnOwnDuration;
	/**
	 * Q4: Si vous êtes le parent ou le tuteur légal d'enfants en âge scolaire, quels sont leurs âges ?<br>
	 * If you are the parent or legal guardian of school-age children, what are their ages?
	 */
	private Set<ChildrenAgeGroup> childrenAges;

	/**
	 * Q5: Qu'est-ce qui est le plus difficile pour vous d'utiliser l'anglais ?<br>
	 * What is the most difficult thing about using English for you?
	 */
	private String mostDifficultThingOriginal;
	private String mostDifficultThingTranslated;
	/**
	 * Q6: Pourquoi voulez-vous améliorer votre anglais ?<br>
	 * Why do you want to improve your English?
	 */
	private String whyImproveEnglishOriginal;
	private String whyImproveEnglishTranslated;

	/*
	 * Q7: Dans quelle mesure pouvez-vous parler avec assurance aux gens dans les situations suivantes ?<br>
	 * How confidently can you speak to people in the following situations?<br>
	 * (11 situations, 1-4 scale: 1=Very confidently, 2=Somewhat confidently, 3=Not confidently, 4=I cannot speak)
	 */
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

	/**
	 * Q8: Dans quelles autres situations n'êtes-vous pas capable de parler suffisamment bien l'anglais ?<br>
	 * In what other situations are you unable to speak English well enough?
	 */
	private String otherSituationsOriginal;
	private String otherSituationsTranslated;

	/*
	 * Q9: Dans quelle mesure comprenez-vous ce que les gens disent dans les situations suivantes ?<br>
	 * How well do you understand what people say in the following situations?<br>
	 * (11 situations, 1-4 scale: 1=Very well, 2=Somewhat well, 3=Not very well, 4=I cannot understand)
	 */
	private Integer understandDirections;
	private Integer understandHealthcare;
	private Integer understandAuthorities;
	private Integer understandJobInterview;
	private Integer understandInformal;
	private Integer understandChildrenEducation;
	private Integer understandLandlord;
	private Integer understandSocialEvents;
	private Integer understandLocalServices;
	private Integer understandSupportOrgs;
	private Integer understandShopping;

	/**
	 * Q10: Qu'est-ce qui est le plus difficile quand vous ne pouvez pas vous exprimer ? Expliquez pourquoi.<br>
	 * What is the most difficult part when you cannot express yourself? Please explain why.
	 */
	private String difficultPartOriginal;
	private String difficultPartTranslated;

	/**
	 * Q11: Veuillez décrire toutes les situations où vous n'avez pas pu parler assez bien et ce que vous vouliez dire
	 * et discuter (de la manière la plus détaillée possible).<br>
	 * Please describe all situations where you could not speak well enough and what you wanted to say and discuss (in
	 * as much detail as possible).
	 */
	private String describeSituationsOriginal;
	private String describeSituationsTranslated;

	// Calculated averages
	private BigDecimal avgSpeakingConfidence;
	private BigDecimal avgUnderstandingConfidence;

	public PreSurveyResponse()
	{
	}

	/**
	 * Calculates and sets the average speaking confidence from all 11 situations.
	 */
	public void calculateAverages()
	{
		this.avgSpeakingConfidence = calculateSpeakingAverage();
		this.avgUnderstandingConfidence = calculateUnderstandingAverage();
	}

	/**
	 * Calculates the average speaking confidence.
	 *
	 * @return average of all non-null speaking confidence values, or null if all are null
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
	 * Calculates the average understanding confidence.
	 *
	 * @return average of all non-null understanding confidence values, or null if all are null
	 */
	public BigDecimal calculateUnderstandingAverage()
	{
		int sum = 0;
		int count = 0;

		Integer[] values = { understandDirections, understandHealthcare, understandAuthorities, understandJobInterview,
				understandInformal, understandChildrenEducation, understandLandlord, understandSocialEvents,
				understandLocalServices, understandSupportOrgs, understandShopping };

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

	public HowFoundKusala getHowFoundKusala()
	{
		return howFoundKusala;
	}

	public void setHowFoundKusala(HowFoundKusala howFoundKusala)
	{
		this.howFoundKusala = howFoundKusala;
	}

	public StudyDuration getStudyWithTeacherDuration()
	{
		return studyWithTeacherDuration;
	}

	public void setStudyWithTeacherDuration(StudyDuration studyWithTeacherDuration)
	{
		this.studyWithTeacherDuration = studyWithTeacherDuration;
	}

	public StudyDuration getStudyOnOwnDuration()
	{
		return studyOnOwnDuration;
	}

	public void setStudyOnOwnDuration(StudyDuration studyOnOwnDuration)
	{
		this.studyOnOwnDuration = studyOnOwnDuration;
	}

	public Set<ChildrenAgeGroup> getChildrenAges()
	{
		return childrenAges;
	}

	public void setChildrenAges(Set<ChildrenAgeGroup> childrenAges)
	{
		this.childrenAges = childrenAges;
	}

	public String getMostDifficultThingOriginal()
	{
		return mostDifficultThingOriginal;
	}

	public void setMostDifficultThingOriginal(String mostDifficultThingOriginal)
	{
		this.mostDifficultThingOriginal = mostDifficultThingOriginal;
	}

	public String getMostDifficultThingTranslated()
	{
		return mostDifficultThingTranslated;
	}

	public void setMostDifficultThingTranslated(String mostDifficultThingTranslated)
	{
		this.mostDifficultThingTranslated = mostDifficultThingTranslated;
	}

	public String getWhyImproveEnglishOriginal()
	{
		return whyImproveEnglishOriginal;
	}

	public void setWhyImproveEnglishOriginal(String whyImproveEnglishOriginal)
	{
		this.whyImproveEnglishOriginal = whyImproveEnglishOriginal;
	}

	public String getWhyImproveEnglishTranslated()
	{
		return whyImproveEnglishTranslated;
	}

	public void setWhyImproveEnglishTranslated(String whyImproveEnglishTranslated)
	{
		this.whyImproveEnglishTranslated = whyImproveEnglishTranslated;
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

	public String getOtherSituationsOriginal()
	{
		return otherSituationsOriginal;
	}

	public void setOtherSituationsOriginal(String otherSituationsOriginal)
	{
		this.otherSituationsOriginal = otherSituationsOriginal;
	}

	public String getOtherSituationsTranslated()
	{
		return otherSituationsTranslated;
	}

	public void setOtherSituationsTranslated(String otherSituationsTranslated)
	{
		this.otherSituationsTranslated = otherSituationsTranslated;
	}

	public Integer getUnderstandDirections()
	{
		return understandDirections;
	}

	public void setUnderstandDirections(Integer understandDirections)
	{
		this.understandDirections = understandDirections;
	}

	public Integer getUnderstandHealthcare()
	{
		return understandHealthcare;
	}

	public void setUnderstandHealthcare(Integer understandHealthcare)
	{
		this.understandHealthcare = understandHealthcare;
	}

	public Integer getUnderstandAuthorities()
	{
		return understandAuthorities;
	}

	public void setUnderstandAuthorities(Integer understandAuthorities)
	{
		this.understandAuthorities = understandAuthorities;
	}

	public Integer getUnderstandJobInterview()
	{
		return understandJobInterview;
	}

	public void setUnderstandJobInterview(Integer understandJobInterview)
	{
		this.understandJobInterview = understandJobInterview;
	}

	public Integer getUnderstandInformal()
	{
		return understandInformal;
	}

	public void setUnderstandInformal(Integer understandInformal)
	{
		this.understandInformal = understandInformal;
	}

	public Integer getUnderstandChildrenEducation()
	{
		return understandChildrenEducation;
	}

	public void setUnderstandChildrenEducation(Integer understandChildrenEducation)
	{
		this.understandChildrenEducation = understandChildrenEducation;
	}

	public Integer getUnderstandLandlord()
	{
		return understandLandlord;
	}

	public void setUnderstandLandlord(Integer understandLandlord)
	{
		this.understandLandlord = understandLandlord;
	}

	public Integer getUnderstandSocialEvents()
	{
		return understandSocialEvents;
	}

	public void setUnderstandSocialEvents(Integer understandSocialEvents)
	{
		this.understandSocialEvents = understandSocialEvents;
	}

	public Integer getUnderstandLocalServices()
	{
		return understandLocalServices;
	}

	public void setUnderstandLocalServices(Integer understandLocalServices)
	{
		this.understandLocalServices = understandLocalServices;
	}

	public Integer getUnderstandSupportOrgs()
	{
		return understandSupportOrgs;
	}

	public void setUnderstandSupportOrgs(Integer understandSupportOrgs)
	{
		this.understandSupportOrgs = understandSupportOrgs;
	}

	public Integer getUnderstandShopping()
	{
		return understandShopping;
	}

	public void setUnderstandShopping(Integer understandShopping)
	{
		this.understandShopping = understandShopping;
	}

	public String getDifficultPartOriginal()
	{
		return difficultPartOriginal;
	}

	public void setDifficultPartOriginal(String difficultPartOriginal)
	{
		this.difficultPartOriginal = difficultPartOriginal;
	}

	public String getDifficultPartTranslated()
	{
		return difficultPartTranslated;
	}

	public void setDifficultPartTranslated(String difficultPartTranslated)
	{
		this.difficultPartTranslated = difficultPartTranslated;
	}

	public String getDescribeSituationsOriginal()
	{
		return describeSituationsOriginal;
	}

	public void setDescribeSituationsOriginal(String describeSituationsOriginal)
	{
		this.describeSituationsOriginal = describeSituationsOriginal;
	}

	public String getDescribeSituationsTranslated()
	{
		return describeSituationsTranslated;
	}

	public void setDescribeSituationsTranslated(String describeSituationsTranslated)
	{
		this.describeSituationsTranslated = describeSituationsTranslated;
	}

	public BigDecimal getAvgSpeakingConfidence()
	{
		return avgSpeakingConfidence;
	}

	public void setAvgSpeakingConfidence(BigDecimal avgSpeakingConfidence)
	{
		this.avgSpeakingConfidence = avgSpeakingConfidence;
	}

	public BigDecimal getAvgUnderstandingConfidence()
	{
		return avgUnderstandingConfidence;
	}

	public void setAvgUnderstandingConfidence(BigDecimal avgUnderstandingConfidence)
	{
		this.avgUnderstandingConfidence = avgUnderstandingConfidence;
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
		PreSurveyResponse that = (PreSurveyResponse)o;
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
		return "PreSurveyResponse{" + "id=" + id + ", personId=" + personId + ", timestamp=" + timestamp
			+ ", avgSpeakingConfidence=" + avgSpeakingConfidence + ", avgUnderstandingConfidence=" + avgUnderstandingConfidence
			+ '}';
	}
}
