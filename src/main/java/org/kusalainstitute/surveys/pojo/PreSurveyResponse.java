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
	 * Q2: Depuis combien de temps étudiez-vous l'anglais avec un enseignant, soit en classe soit en
	 * ligne, de manière régulière ou irrégulière ?<br>
	 * How long have you been studying English with a teacher, either in class or online, regularly
	 * or irregularly?
	 */
	private StudyDuration studyWithTeacherDuration;
	/**
	 * Q3: Depuis combien de temps étudiez-vous l'anglais par vous-même avec un professeur
	 * particulier, un partenaire d'échange linguistique, des sites web ou une application, que ce
	 * soit de façon régulière ou irrégulière ?<br>
	 * How long have you been studying English on your own with a private teacher, language exchange
	 * partner, websites or an app, regularly or irregularly?
	 */
	private StudyDuration studyOnOwnDuration;
	/**
	 * Q4: Si vous êtes le parent ou le tuteur légal d'enfants en âge scolaire, quels sont leurs
	 * âges ?<br>
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
	 * Q7: Dans quelle mesure pouvez-vous parler avec assurance aux gens dans les situations
	 * suivantes ?<br> How confidently can you speak to people in the following situations?<br> (11
	 * situations, 1-4 scale: 1=Very confidently, 2=Somewhat confidently, 3=Not confidently, 4=I
	 * cannot speak)
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
	 * Q8: Dans quelles autres situations n'êtes-vous pas capable de parler suffisamment bien
	 * l'anglais ?<br>
	 * In what other situations are you unable to speak English well enough?
	 */
	private String otherSituationsOriginal;
	private String otherSituationsTranslated;

	/*
	 * Q9: Dans quelle mesure comprenez-vous ce que les gens disent dans les situations suivantes
	 * ?<br> How well do you understand what people say in the following situations?<br> (11
	 * situations, 1-4 scale: 1=Very well, 2=Somewhat well, 3=Not very well, 4=I cannot understand)
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
	 * Q10: Qu'est-ce qui est le plus difficile quand vous ne pouvez pas vous exprimer ? Expliquez
	 * pourquoi.<br>
	 * What is the most difficult part when you cannot express yourself? Please explain why.
	 */
	private String difficultPartOriginal;
	private String difficultPartTranslated;

	/**
	 * Q11: Veuillez décrire toutes les situations où vous n'avez pas pu parler assez bien et ce que
	 * vous vouliez dire et discuter (de la manière la plus détaillée possible).<br>
	 * Please describe all situations where you could not speak well enough and what you wanted to
	 * say and discuss (in as much detail as possible).
	 */
	private String describeSituationsOriginal;
	private String describeSituationsTranslated;

	// Calculated averages
	private BigDecimal avgSpeakingConfidence;
	private BigDecimal avgUnderstandingConfidence;

	/**
	 * Construct.
	 */
	public PreSurveyResponse()
	{
	}

	/**
	 * Calculates and sets the average speaking confidence from all 11 situations.
	 */
	public void calculateAverages()
	{
		avgSpeakingConfidence = calculateSpeakingAverage();
		avgUnderstandingConfidence = calculateUnderstandingAverage();
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
				speakChildrenEducation, speakLandlord, speakSocialEvents, speakLocalServices, speakSupportOrgs,
				speakShopping };

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

	/**
	 * Gets id.
	 *
	 * @return id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets id.
	 *
	 * @param id
	 *            id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets personId.
	 *
	 * @return personId
	 */
	public Long getPersonId()
	{
		return personId;
	}

	/**
	 * Sets personId.
	 *
	 * @param personId
	 *            personId
	 */
	public void setPersonId(Long personId)
	{
		this.personId = personId;
	}

	/**
	 * Gets timestamp.
	 *
	 * @return timestamp
	 */
	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}

	/**
	 * Sets timestamp.
	 *
	 * @param timestamp
	 *            timestamp
	 */
	public void setTimestamp(LocalDateTime timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * Gets sourceFile.
	 *
	 * @return sourceFile
	 */
	public String getSourceFile()
	{
		return sourceFile;
	}

	/**
	 * Sets sourceFile.
	 *
	 * @param sourceFile
	 *            sourceFile
	 */
	public void setSourceFile(String sourceFile)
	{
		this.sourceFile = sourceFile;
	}

	/**
	 * Gets rowNumber.
	 *
	 * @return rowNumber
	 */
	public Integer getRowNumber()
	{
		return rowNumber;
	}

	/**
	 * Sets rowNumber.
	 *
	 * @param rowNumber
	 *            rowNumber
	 */
	public void setRowNumber(Integer rowNumber)
	{
		this.rowNumber = rowNumber;
	}

	/**
	 * Gets howFoundKusala.
	 *
	 * @return howFoundKusala
	 */
	public HowFoundKusala getHowFoundKusala()
	{
		return howFoundKusala;
	}

	/**
	 * Sets howFoundKusala.
	 *
	 * @param howFoundKusala
	 *            howFoundKusala
	 */
	public void setHowFoundKusala(HowFoundKusala howFoundKusala)
	{
		this.howFoundKusala = howFoundKusala;
	}

	/**
	 * Gets studyWithTeacherDuration.
	 *
	 * @return studyWithTeacherDuration
	 */
	public StudyDuration getStudyWithTeacherDuration()
	{
		return studyWithTeacherDuration;
	}

	/**
	 * Sets studyWithTeacherDuration.
	 *
	 * @param studyWithTeacherDuration
	 *            studyWithTeacherDuration
	 */
	public void setStudyWithTeacherDuration(StudyDuration studyWithTeacherDuration)
	{
		this.studyWithTeacherDuration = studyWithTeacherDuration;
	}

	/**
	 * Gets studyOnOwnDuration.
	 *
	 * @return studyOnOwnDuration
	 */
	public StudyDuration getStudyOnOwnDuration()
	{
		return studyOnOwnDuration;
	}

	/**
	 * Sets studyOnOwnDuration.
	 *
	 * @param studyOnOwnDuration
	 *            studyOnOwnDuration
	 */
	public void setStudyOnOwnDuration(StudyDuration studyOnOwnDuration)
	{
		this.studyOnOwnDuration = studyOnOwnDuration;
	}

	/**
	 * Gets childrenAges.
	 *
	 * @return childrenAges
	 */
	public Set<ChildrenAgeGroup> getChildrenAges()
	{
		return childrenAges;
	}

	/**
	 * Sets childrenAges.
	 *
	 * @param childrenAges
	 *            childrenAges
	 */
	public void setChildrenAges(Set<ChildrenAgeGroup> childrenAges)
	{
		this.childrenAges = childrenAges;
	}

	/**
	 * Gets mostDifficultThingOriginal.
	 *
	 * @return mostDifficultThingOriginal
	 */
	public String getMostDifficultThingOriginal()
	{
		return mostDifficultThingOriginal;
	}

	/**
	 * Sets mostDifficultThingOriginal.
	 *
	 * @param mostDifficultThingOriginal
	 *            mostDifficultThingOriginal
	 */
	public void setMostDifficultThingOriginal(String mostDifficultThingOriginal)
	{
		this.mostDifficultThingOriginal = mostDifficultThingOriginal;
	}

	/**
	 * Gets mostDifficultThingTranslated.
	 *
	 * @return mostDifficultThingTranslated
	 */
	public String getMostDifficultThingTranslated()
	{
		return mostDifficultThingTranslated;
	}

	/**
	 * Sets mostDifficultThingTranslated.
	 *
	 * @param mostDifficultThingTranslated
	 *            mostDifficultThingTranslated
	 */
	public void setMostDifficultThingTranslated(String mostDifficultThingTranslated)
	{
		this.mostDifficultThingTranslated = mostDifficultThingTranslated;
	}

	/**
	 * Gets whyImproveEnglishOriginal.
	 *
	 * @return whyImproveEnglishOriginal
	 */
	public String getWhyImproveEnglishOriginal()
	{
		return whyImproveEnglishOriginal;
	}

	/**
	 * Sets whyImproveEnglishOriginal.
	 *
	 * @param whyImproveEnglishOriginal
	 *            whyImproveEnglishOriginal
	 */
	public void setWhyImproveEnglishOriginal(String whyImproveEnglishOriginal)
	{
		this.whyImproveEnglishOriginal = whyImproveEnglishOriginal;
	}

	/**
	 * Gets whyImproveEnglishTranslated.
	 *
	 * @return whyImproveEnglishTranslated
	 */
	public String getWhyImproveEnglishTranslated()
	{
		return whyImproveEnglishTranslated;
	}

	/**
	 * Sets whyImproveEnglishTranslated.
	 *
	 * @param whyImproveEnglishTranslated
	 *            whyImproveEnglishTranslated
	 */
	public void setWhyImproveEnglishTranslated(String whyImproveEnglishTranslated)
	{
		this.whyImproveEnglishTranslated = whyImproveEnglishTranslated;
	}

	/**
	 * Gets speakDirections.
	 *
	 * @return speakDirections
	 */
	public Integer getSpeakDirections()
	{
		return speakDirections;
	}

	/**
	 * Sets speakDirections.
	 *
	 * @param speakDirections
	 *            speakDirections
	 */
	public void setSpeakDirections(Integer speakDirections)
	{
		this.speakDirections = speakDirections;
	}

	/**
	 * Gets speakHealthcare.
	 *
	 * @return speakHealthcare
	 */
	public Integer getSpeakHealthcare()
	{
		return speakHealthcare;
	}

	/**
	 * Sets speakHealthcare.
	 *
	 * @param speakHealthcare
	 *            speakHealthcare
	 */
	public void setSpeakHealthcare(Integer speakHealthcare)
	{
		this.speakHealthcare = speakHealthcare;
	}

	/**
	 * Gets speakAuthorities.
	 *
	 * @return speakAuthorities
	 */
	public Integer getSpeakAuthorities()
	{
		return speakAuthorities;
	}

	/**
	 * Sets speakAuthorities.
	 *
	 * @param speakAuthorities
	 *            speakAuthorities
	 */
	public void setSpeakAuthorities(Integer speakAuthorities)
	{
		this.speakAuthorities = speakAuthorities;
	}

	/**
	 * Gets speakJobInterview.
	 *
	 * @return speakJobInterview
	 */
	public Integer getSpeakJobInterview()
	{
		return speakJobInterview;
	}

	/**
	 * Sets speakJobInterview.
	 *
	 * @param speakJobInterview
	 *            speakJobInterview
	 */
	public void setSpeakJobInterview(Integer speakJobInterview)
	{
		this.speakJobInterview = speakJobInterview;
	}

	/**
	 * Gets speakInformal.
	 *
	 * @return speakInformal
	 */
	public Integer getSpeakInformal()
	{
		return speakInformal;
	}

	/**
	 * Sets speakInformal.
	 *
	 * @param speakInformal
	 *            speakInformal
	 */
	public void setSpeakInformal(Integer speakInformal)
	{
		this.speakInformal = speakInformal;
	}

	/**
	 * Gets speakChildrenEducation.
	 *
	 * @return speakChildrenEducation
	 */
	public Integer getSpeakChildrenEducation()
	{
		return speakChildrenEducation;
	}

	/**
	 * Sets speakChildrenEducation.
	 *
	 * @param speakChildrenEducation
	 *            speakChildrenEducation
	 */
	public void setSpeakChildrenEducation(Integer speakChildrenEducation)
	{
		this.speakChildrenEducation = speakChildrenEducation;
	}

	/**
	 * Gets speakLandlord.
	 *
	 * @return speakLandlord
	 */
	public Integer getSpeakLandlord()
	{
		return speakLandlord;
	}

	/**
	 * Sets speakLandlord.
	 *
	 * @param speakLandlord
	 *            speakLandlord
	 */
	public void setSpeakLandlord(Integer speakLandlord)
	{
		this.speakLandlord = speakLandlord;
	}

	/**
	 * Gets speakSocialEvents.
	 *
	 * @return speakSocialEvents
	 */
	public Integer getSpeakSocialEvents()
	{
		return speakSocialEvents;
	}

	/**
	 * Sets speakSocialEvents.
	 *
	 * @param speakSocialEvents
	 *            speakSocialEvents
	 */
	public void setSpeakSocialEvents(Integer speakSocialEvents)
	{
		this.speakSocialEvents = speakSocialEvents;
	}

	/**
	 * Gets speakLocalServices.
	 *
	 * @return speakLocalServices
	 */
	public Integer getSpeakLocalServices()
	{
		return speakLocalServices;
	}

	/**
	 * Sets speakLocalServices.
	 *
	 * @param speakLocalServices
	 *            speakLocalServices
	 */
	public void setSpeakLocalServices(Integer speakLocalServices)
	{
		this.speakLocalServices = speakLocalServices;
	}

	/**
	 * Gets speakSupportOrgs.
	 *
	 * @return speakSupportOrgs
	 */
	public Integer getSpeakSupportOrgs()
	{
		return speakSupportOrgs;
	}

	/**
	 * Sets speakSupportOrgs.
	 *
	 * @param speakSupportOrgs
	 *            speakSupportOrgs
	 */
	public void setSpeakSupportOrgs(Integer speakSupportOrgs)
	{
		this.speakSupportOrgs = speakSupportOrgs;
	}

	/**
	 * Gets speakShopping.
	 *
	 * @return speakShopping
	 */
	public Integer getSpeakShopping()
	{
		return speakShopping;
	}

	/**
	 * Sets speakShopping.
	 *
	 * @param speakShopping
	 *            speakShopping
	 */
	public void setSpeakShopping(Integer speakShopping)
	{
		this.speakShopping = speakShopping;
	}

	/**
	 * Gets otherSituationsOriginal.
	 *
	 * @return otherSituationsOriginal
	 */
	public String getOtherSituationsOriginal()
	{
		return otherSituationsOriginal;
	}

	/**
	 * Sets otherSituationsOriginal.
	 *
	 * @param otherSituationsOriginal
	 *            otherSituationsOriginal
	 */
	public void setOtherSituationsOriginal(String otherSituationsOriginal)
	{
		this.otherSituationsOriginal = otherSituationsOriginal;
	}

	/**
	 * Gets otherSituationsTranslated.
	 *
	 * @return otherSituationsTranslated
	 */
	public String getOtherSituationsTranslated()
	{
		return otherSituationsTranslated;
	}

	/**
	 * Sets otherSituationsTranslated.
	 *
	 * @param otherSituationsTranslated
	 *            otherSituationsTranslated
	 */
	public void setOtherSituationsTranslated(String otherSituationsTranslated)
	{
		this.otherSituationsTranslated = otherSituationsTranslated;
	}

	/**
	 * Gets understandDirections.
	 *
	 * @return understandDirections
	 */
	public Integer getUnderstandDirections()
	{
		return understandDirections;
	}

	/**
	 * Sets understandDirections.
	 *
	 * @param understandDirections
	 *            understandDirections
	 */
	public void setUnderstandDirections(Integer understandDirections)
	{
		this.understandDirections = understandDirections;
	}

	/**
	 * Gets understandHealthcare.
	 *
	 * @return understandHealthcare
	 */
	public Integer getUnderstandHealthcare()
	{
		return understandHealthcare;
	}

	/**
	 * Sets understandHealthcare.
	 *
	 * @param understandHealthcare
	 *            understandHealthcare
	 */
	public void setUnderstandHealthcare(Integer understandHealthcare)
	{
		this.understandHealthcare = understandHealthcare;
	}

	/**
	 * Gets understandAuthorities.
	 *
	 * @return understandAuthorities
	 */
	public Integer getUnderstandAuthorities()
	{
		return understandAuthorities;
	}

	/**
	 * Sets understandAuthorities.
	 *
	 * @param understandAuthorities
	 *            understandAuthorities
	 */
	public void setUnderstandAuthorities(Integer understandAuthorities)
	{
		this.understandAuthorities = understandAuthorities;
	}

	/**
	 * Gets understandJobInterview.
	 *
	 * @return understandJobInterview
	 */
	public Integer getUnderstandJobInterview()
	{
		return understandJobInterview;
	}

	/**
	 * Sets understandJobInterview.
	 *
	 * @param understandJobInterview
	 *            understandJobInterview
	 */
	public void setUnderstandJobInterview(Integer understandJobInterview)
	{
		this.understandJobInterview = understandJobInterview;
	}

	/**
	 * Gets understandInformal.
	 *
	 * @return understandInformal
	 */
	public Integer getUnderstandInformal()
	{
		return understandInformal;
	}

	/**
	 * Sets understandInformal.
	 *
	 * @param understandInformal
	 *            understandInformal
	 */
	public void setUnderstandInformal(Integer understandInformal)
	{
		this.understandInformal = understandInformal;
	}

	/**
	 * Gets understandChildrenEducation.
	 *
	 * @return understandChildrenEducation
	 */
	public Integer getUnderstandChildrenEducation()
	{
		return understandChildrenEducation;
	}

	/**
	 * Sets understandChildrenEducation.
	 *
	 * @param understandChildrenEducation
	 *            understandChildrenEducation
	 */
	public void setUnderstandChildrenEducation(Integer understandChildrenEducation)
	{
		this.understandChildrenEducation = understandChildrenEducation;
	}

	/**
	 * Gets understandLandlord.
	 *
	 * @return understandLandlord
	 */
	public Integer getUnderstandLandlord()
	{
		return understandLandlord;
	}

	/**
	 * Sets understandLandlord.
	 *
	 * @param understandLandlord
	 *            understandLandlord
	 */
	public void setUnderstandLandlord(Integer understandLandlord)
	{
		this.understandLandlord = understandLandlord;
	}

	/**
	 * Gets understandSocialEvents.
	 *
	 * @return understandSocialEvents
	 */
	public Integer getUnderstandSocialEvents()
	{
		return understandSocialEvents;
	}

	/**
	 * Sets understandSocialEvents.
	 *
	 * @param understandSocialEvents
	 *            understandSocialEvents
	 */
	public void setUnderstandSocialEvents(Integer understandSocialEvents)
	{
		this.understandSocialEvents = understandSocialEvents;
	}

	/**
	 * Gets understandLocalServices.
	 *
	 * @return understandLocalServices
	 */
	public Integer getUnderstandLocalServices()
	{
		return understandLocalServices;
	}

	/**
	 * Sets understandLocalServices.
	 *
	 * @param understandLocalServices
	 *            understandLocalServices
	 */
	public void setUnderstandLocalServices(Integer understandLocalServices)
	{
		this.understandLocalServices = understandLocalServices;
	}

	/**
	 * Gets understandSupportOrgs.
	 *
	 * @return understandSupportOrgs
	 */
	public Integer getUnderstandSupportOrgs()
	{
		return understandSupportOrgs;
	}

	/**
	 * Sets understandSupportOrgs.
	 *
	 * @param understandSupportOrgs
	 *            understandSupportOrgs
	 */
	public void setUnderstandSupportOrgs(Integer understandSupportOrgs)
	{
		this.understandSupportOrgs = understandSupportOrgs;
	}

	/**
	 * Gets understandShopping.
	 *
	 * @return understandShopping
	 */
	public Integer getUnderstandShopping()
	{
		return understandShopping;
	}

	/**
	 * Sets understandShopping.
	 *
	 * @param understandShopping
	 *            understandShopping
	 */
	public void setUnderstandShopping(Integer understandShopping)
	{
		this.understandShopping = understandShopping;
	}

	/**
	 * Gets difficultPartOriginal.
	 *
	 * @return difficultPartOriginal
	 */
	public String getDifficultPartOriginal()
	{
		return difficultPartOriginal;
	}

	/**
	 * Sets difficultPartOriginal.
	 *
	 * @param difficultPartOriginal
	 *            difficultPartOriginal
	 */
	public void setDifficultPartOriginal(String difficultPartOriginal)
	{
		this.difficultPartOriginal = difficultPartOriginal;
	}

	/**
	 * Gets difficultPartTranslated.
	 *
	 * @return difficultPartTranslated
	 */
	public String getDifficultPartTranslated()
	{
		return difficultPartTranslated;
	}

	/**
	 * Sets difficultPartTranslated.
	 *
	 * @param difficultPartTranslated
	 *            difficultPartTranslated
	 */
	public void setDifficultPartTranslated(String difficultPartTranslated)
	{
		this.difficultPartTranslated = difficultPartTranslated;
	}

	/**
	 * Gets describeSituationsOriginal.
	 *
	 * @return describeSituationsOriginal
	 */
	public String getDescribeSituationsOriginal()
	{
		return describeSituationsOriginal;
	}

	/**
	 * Sets describeSituationsOriginal.
	 *
	 * @param describeSituationsOriginal
	 *            describeSituationsOriginal
	 */
	public void setDescribeSituationsOriginal(String describeSituationsOriginal)
	{
		this.describeSituationsOriginal = describeSituationsOriginal;
	}

	/**
	 * Gets describeSituationsTranslated.
	 *
	 * @return describeSituationsTranslated
	 */
	public String getDescribeSituationsTranslated()
	{
		return describeSituationsTranslated;
	}

	/**
	 * Sets describeSituationsTranslated.
	 *
	 * @param describeSituationsTranslated
	 *            describeSituationsTranslated
	 */
	public void setDescribeSituationsTranslated(String describeSituationsTranslated)
	{
		this.describeSituationsTranslated = describeSituationsTranslated;
	}

	/**
	 * Gets avgSpeakingConfidence.
	 *
	 * @return avgSpeakingConfidence
	 */
	public BigDecimal getAvgSpeakingConfidence()
	{
		return avgSpeakingConfidence;
	}

	/**
	 * Sets avgSpeakingConfidence.
	 *
	 * @param avgSpeakingConfidence
	 *            avgSpeakingConfidence
	 */
	public void setAvgSpeakingConfidence(BigDecimal avgSpeakingConfidence)
	{
		this.avgSpeakingConfidence = avgSpeakingConfidence;
	}

	/**
	 * Gets avgUnderstandingConfidence.
	 *
	 * @return avgUnderstandingConfidence
	 */
	public BigDecimal getAvgUnderstandingConfidence()
	{
		return avgUnderstandingConfidence;
	}

	/**
	 * Sets avgUnderstandingConfidence.
	 *
	 * @param avgUnderstandingConfidence
	 *            avgUnderstandingConfidence
	 */
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
			+ ", avgSpeakingConfidence=" + avgSpeakingConfidence + ", avgUnderstandingConfidence="
			+ avgUnderstandingConfidence + '}';
	}
}
