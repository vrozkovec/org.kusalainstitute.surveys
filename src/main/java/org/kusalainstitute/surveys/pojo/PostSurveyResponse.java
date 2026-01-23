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

	/**
	 * Q1: Depuis combien de temps utilisez-vous l'application pour apprendre l'anglais ?<br>
	 * How long have you been using the app to learn English? If you don't use the app, please
	 * explain why.
	 */
	private String appUsageDurationOriginal;
	private String appUsageDurationTranslated;
	/**
	 * Q2: Combien de temps passez-vous sur l'application lors de chaque session ?<br>
	 * How much time do you spend on the app at each session?
	 */
	private AppTimePerSession appTimePerSession;
	/**
	 * Q3: A quelle fréquence utilisez-vous l'application ?<br>
	 * How often do you use the app?
	 */
	private AppFrequency appFrequency;
	/**
	 * Q4: Avez-vous vu des progrès depuis que vous avez commencé à utiliser l'application ?<br>
	 * Have you seen progress since you started using the app?
	 */
	private ProgressAssessment progressAssessment;

	/**
	 * Q5: Depuis que vous utilisez l'application, qu'est-ce qui vous a le plus aidé ? Écrivez
	 * autant que possible afin que nous sachions comment expliquer les avantages de l'application à
	 * d'autres personnes.<br>
	 * Since you have been using the app, what has helped you the most? Please write as much as
	 * possible so we know how to explain the app's benefits to others.
	 */
	private String whatHelpedMostOriginal;
	private String whatHelpedMostTranslated;

	/*
	 * Q6: Depuis que vous utilisez l'appli, veuillez évaluer votre capacité à PARLER anglais dans
	 * les situations suivantes<br> Since you started using the app, please rate your ability to
	 * SPEAK English in the following situations (11 situations, 1-4 scale: 1=Very well, 2=Somewhat
	 * well, 3=Not very well, 4=I cannot speak)
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

	/*
	 * Q7: À quel point est-il difficile de s'exprimer dans les situations suivantes ?<br> How hard
	 * is it to express yourself in the following situations? (11 situations, 1-5 scale: 1=Very
	 * easy, 2=Easy, 3=Somewhat difficult, 4=Difficult, 5=Very difficult)
	 */
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

	/**
	 * Q8: Qu'est-ce qui est le plus difficile quand vous ne pouvez pas vous exprimer ? Expliquez
	 * pourquoi.<br>
	 * What is the most difficult part when you cannot express yourself? Please explain why.
	 */
	private String mostDifficultOverallOriginal;
	private String mostDifficultOverallTranslated;

	/**
	 * Q9: Selon vous, quelle est la chose la plus difficile pour apprendre suffisamment bien
	 * l'anglais pour trouver un emploi ou se sentir intégré dans la communauté ?<br>
	 * In your opinion, what is the most difficult thing about learning English well enough to find
	 * a job or feel part of the community?
	 */
	private String mostDifficultForJobOriginal;
	private String mostDifficultForJobTranslated;

	/**
	 * Q10: Outre l'apprentissage de la grammaire et du vocabulaire, trouvez-vous l'apprentissage
	 * des langues difficile d'autres manières ? Par exemple, d'une manière qui affecte vos émotions
	 * et vos expériences sociales ? Expliquez.<br>
	 * Besides learning new grammar and vocabulary, do you find language learning difficult in other
	 * ways? For example, in a way that affects your emotions and social experiences? Please
	 * explain.
	 */
	private String emotionalDifficultiesOriginal;
	private String emotionalDifficultiesTranslated;

	/**
	 * Q11: Avez-vous déjà évité une situation parce que vous n'étiez pas à l'aise avec votre niveau
	 * d'anglais ? Décrivez les circonstances.<br>
	 * Have you ever avoided a situation because you were not comfortable with your level of
	 * English? Please describe the circumstances.
	 */
	private String avoidedSituationsOriginal;
	private String avoidedSituationsTranslated;

	/**
	 * Q12: Avez-vous suffisamment de soutien de la part des gens pour vous aider à apprendre
	 * l'anglais ?<br>
	 * Do you have enough support from people to help you learn English?
	 */
	private String hasEnoughSupportOriginal;
	private String hasEnoughSupportTranslated;

	/**
	 * Q13: Décrivez les types de ressources linguistiques que vous aimeriez avoir pour vous aider à
	 * progresser plus rapidement.<br>
	 * Please describe the types of language resources you would like to have to help you improve
	 * faster.
	 */
	private String desiredResourcesOriginal;
	private String desiredResourcesTranslated;

	/**
	 * Q14: Seriez-vous prêt(e) à participer à un entretien téléphonique ou vidéo avec nous ?<br>
	 * Would you be willing to participate in a telephone or video interview with us?
	 */
	private YesNo willingToInterview;
	/**
	 * Q15: Si vous avez répondu « Non » à la question précédente, veuillez nous indiquer la raison
	 * pour laquelle vous ne souhaitez pas participer à un entretien téléphonique ou vidéo.<br>
	 * If you answered "No" to the previous question, please tell us the reason why you do not wish
	 * to participate in a telephone or video interview.
	 */
	private String interviewDeclineReasonOriginal;
	private String interviewDeclineReasonTranslated;
	/**
	 * Q16: Comment préférez-vous faire votre entretien ?<br>
	 * How do you prefer to do your interview?
	 */
	private InterviewTypePreference preferredInterviewType;
	private String contactInfo;

	/**
	 * Q17: Y a-t-il autre chose que vous aimeriez partager ?<br>
	 * Is there anything else you would like to share?
	 */
	private String additionalCommentsOriginal;
	private String additionalCommentsTranslated;

	// Calculated averages
	private BigDecimal avgSpeakingAbility;
	private BigDecimal avgDifficultyExpressing;

	/**
	 * Construct.
	 */
	public PostSurveyResponse()
	{
	}

	/**
	 * Calculates and sets the average speaking ability and difficulty expressing from all 11
	 * situations.
	 */
	public void calculateAverages()
	{
		avgSpeakingAbility = calculateSpeakingAverage();
		avgDifficultyExpressing = calculateDifficultyAverage();
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
	 * Gets appUsageDurationOriginal.
	 *
	 * @return appUsageDurationOriginal
	 */
	public String getAppUsageDurationOriginal()
	{
		return appUsageDurationOriginal;
	}

	/**
	 * Sets appUsageDurationOriginal.
	 *
	 * @param appUsageDurationOriginal
	 *            appUsageDurationOriginal
	 */
	public void setAppUsageDurationOriginal(String appUsageDurationOriginal)
	{
		this.appUsageDurationOriginal = appUsageDurationOriginal;
	}

	/**
	 * Gets appUsageDurationTranslated.
	 *
	 * @return appUsageDurationTranslated
	 */
	public String getAppUsageDurationTranslated()
	{
		return appUsageDurationTranslated;
	}

	/**
	 * Sets appUsageDurationTranslated.
	 *
	 * @param appUsageDurationTranslated
	 *            appUsageDurationTranslated
	 */
	public void setAppUsageDurationTranslated(String appUsageDurationTranslated)
	{
		this.appUsageDurationTranslated = appUsageDurationTranslated;
	}

	/**
	 * Gets appTimePerSession.
	 *
	 * @return appTimePerSession
	 */
	public AppTimePerSession getAppTimePerSession()
	{
		return appTimePerSession;
	}

	/**
	 * Sets appTimePerSession.
	 *
	 * @param appTimePerSession
	 *            appTimePerSession
	 */
	public void setAppTimePerSession(AppTimePerSession appTimePerSession)
	{
		this.appTimePerSession = appTimePerSession;
	}

	/**
	 * Gets appFrequency.
	 *
	 * @return appFrequency
	 */
	public AppFrequency getAppFrequency()
	{
		return appFrequency;
	}

	/**
	 * Sets appFrequency.
	 *
	 * @param appFrequency
	 *            appFrequency
	 */
	public void setAppFrequency(AppFrequency appFrequency)
	{
		this.appFrequency = appFrequency;
	}

	/**
	 * Gets progressAssessment.
	 *
	 * @return progressAssessment
	 */
	public ProgressAssessment getProgressAssessment()
	{
		return progressAssessment;
	}

	/**
	 * Sets progressAssessment.
	 *
	 * @param progressAssessment
	 *            progressAssessment
	 */
	public void setProgressAssessment(ProgressAssessment progressAssessment)
	{
		this.progressAssessment = progressAssessment;
	}

	/**
	 * Gets whatHelpedMostOriginal.
	 *
	 * @return whatHelpedMostOriginal
	 */
	public String getWhatHelpedMostOriginal()
	{
		return whatHelpedMostOriginal;
	}

	/**
	 * Sets whatHelpedMostOriginal.
	 *
	 * @param whatHelpedMostOriginal
	 *            whatHelpedMostOriginal
	 */
	public void setWhatHelpedMostOriginal(String whatHelpedMostOriginal)
	{
		this.whatHelpedMostOriginal = whatHelpedMostOriginal;
	}

	/**
	 * Gets whatHelpedMostTranslated.
	 *
	 * @return whatHelpedMostTranslated
	 */
	public String getWhatHelpedMostTranslated()
	{
		return whatHelpedMostTranslated;
	}

	/**
	 * Sets whatHelpedMostTranslated.
	 *
	 * @param whatHelpedMostTranslated
	 *            whatHelpedMostTranslated
	 */
	public void setWhatHelpedMostTranslated(String whatHelpedMostTranslated)
	{
		this.whatHelpedMostTranslated = whatHelpedMostTranslated;
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
	 * Gets difficultyDirections.
	 *
	 * @return difficultyDirections
	 */
	public Integer getDifficultyDirections()
	{
		return difficultyDirections;
	}

	/**
	 * Sets difficultyDirections.
	 *
	 * @param difficultyDirections
	 *            difficultyDirections
	 */
	public void setDifficultyDirections(Integer difficultyDirections)
	{
		this.difficultyDirections = difficultyDirections;
	}

	/**
	 * Gets difficultyHealthcare.
	 *
	 * @return difficultyHealthcare
	 */
	public Integer getDifficultyHealthcare()
	{
		return difficultyHealthcare;
	}

	/**
	 * Sets difficultyHealthcare.
	 *
	 * @param difficultyHealthcare
	 *            difficultyHealthcare
	 */
	public void setDifficultyHealthcare(Integer difficultyHealthcare)
	{
		this.difficultyHealthcare = difficultyHealthcare;
	}

	/**
	 * Gets difficultyAuthorities.
	 *
	 * @return difficultyAuthorities
	 */
	public Integer getDifficultyAuthorities()
	{
		return difficultyAuthorities;
	}

	/**
	 * Sets difficultyAuthorities.
	 *
	 * @param difficultyAuthorities
	 *            difficultyAuthorities
	 */
	public void setDifficultyAuthorities(Integer difficultyAuthorities)
	{
		this.difficultyAuthorities = difficultyAuthorities;
	}

	/**
	 * Gets difficultyJobInterview.
	 *
	 * @return difficultyJobInterview
	 */
	public Integer getDifficultyJobInterview()
	{
		return difficultyJobInterview;
	}

	/**
	 * Sets difficultyJobInterview.
	 *
	 * @param difficultyJobInterview
	 *            difficultyJobInterview
	 */
	public void setDifficultyJobInterview(Integer difficultyJobInterview)
	{
		this.difficultyJobInterview = difficultyJobInterview;
	}

	/**
	 * Gets difficultyInformal.
	 *
	 * @return difficultyInformal
	 */
	public Integer getDifficultyInformal()
	{
		return difficultyInformal;
	}

	/**
	 * Sets difficultyInformal.
	 *
	 * @param difficultyInformal
	 *            difficultyInformal
	 */
	public void setDifficultyInformal(Integer difficultyInformal)
	{
		this.difficultyInformal = difficultyInformal;
	}

	/**
	 * Gets difficultyChildrenEducation.
	 *
	 * @return difficultyChildrenEducation
	 */
	public Integer getDifficultyChildrenEducation()
	{
		return difficultyChildrenEducation;
	}

	/**
	 * Sets difficultyChildrenEducation.
	 *
	 * @param difficultyChildrenEducation
	 *            difficultyChildrenEducation
	 */
	public void setDifficultyChildrenEducation(Integer difficultyChildrenEducation)
	{
		this.difficultyChildrenEducation = difficultyChildrenEducation;
	}

	/**
	 * Gets difficultyLandlord.
	 *
	 * @return difficultyLandlord
	 */
	public Integer getDifficultyLandlord()
	{
		return difficultyLandlord;
	}

	/**
	 * Sets difficultyLandlord.
	 *
	 * @param difficultyLandlord
	 *            difficultyLandlord
	 */
	public void setDifficultyLandlord(Integer difficultyLandlord)
	{
		this.difficultyLandlord = difficultyLandlord;
	}

	/**
	 * Gets difficultySocialEvents.
	 *
	 * @return difficultySocialEvents
	 */
	public Integer getDifficultySocialEvents()
	{
		return difficultySocialEvents;
	}

	/**
	 * Sets difficultySocialEvents.
	 *
	 * @param difficultySocialEvents
	 *            difficultySocialEvents
	 */
	public void setDifficultySocialEvents(Integer difficultySocialEvents)
	{
		this.difficultySocialEvents = difficultySocialEvents;
	}

	/**
	 * Gets difficultyLocalServices.
	 *
	 * @return difficultyLocalServices
	 */
	public Integer getDifficultyLocalServices()
	{
		return difficultyLocalServices;
	}

	/**
	 * Sets difficultyLocalServices.
	 *
	 * @param difficultyLocalServices
	 *            difficultyLocalServices
	 */
	public void setDifficultyLocalServices(Integer difficultyLocalServices)
	{
		this.difficultyLocalServices = difficultyLocalServices;
	}

	/**
	 * Gets difficultySupportOrgs.
	 *
	 * @return difficultySupportOrgs
	 */
	public Integer getDifficultySupportOrgs()
	{
		return difficultySupportOrgs;
	}

	/**
	 * Sets difficultySupportOrgs.
	 *
	 * @param difficultySupportOrgs
	 *            difficultySupportOrgs
	 */
	public void setDifficultySupportOrgs(Integer difficultySupportOrgs)
	{
		this.difficultySupportOrgs = difficultySupportOrgs;
	}

	/**
	 * Gets difficultyShopping.
	 *
	 * @return difficultyShopping
	 */
	public Integer getDifficultyShopping()
	{
		return difficultyShopping;
	}

	/**
	 * Sets difficultyShopping.
	 *
	 * @param difficultyShopping
	 *            difficultyShopping
	 */
	public void setDifficultyShopping(Integer difficultyShopping)
	{
		this.difficultyShopping = difficultyShopping;
	}

	/**
	 * Gets mostDifficultOverallOriginal.
	 *
	 * @return mostDifficultOverallOriginal
	 */
	public String getMostDifficultOverallOriginal()
	{
		return mostDifficultOverallOriginal;
	}

	/**
	 * Sets mostDifficultOverallOriginal.
	 *
	 * @param mostDifficultOverallOriginal
	 *            mostDifficultOverallOriginal
	 */
	public void setMostDifficultOverallOriginal(String mostDifficultOverallOriginal)
	{
		this.mostDifficultOverallOriginal = mostDifficultOverallOriginal;
	}

	/**
	 * Gets mostDifficultOverallTranslated.
	 *
	 * @return mostDifficultOverallTranslated
	 */
	public String getMostDifficultOverallTranslated()
	{
		return mostDifficultOverallTranslated;
	}

	/**
	 * Sets mostDifficultOverallTranslated.
	 *
	 * @param mostDifficultOverallTranslated
	 *            mostDifficultOverallTranslated
	 */
	public void setMostDifficultOverallTranslated(String mostDifficultOverallTranslated)
	{
		this.mostDifficultOverallTranslated = mostDifficultOverallTranslated;
	}

	/**
	 * Gets mostDifficultForJobOriginal.
	 *
	 * @return mostDifficultForJobOriginal
	 */
	public String getMostDifficultForJobOriginal()
	{
		return mostDifficultForJobOriginal;
	}

	/**
	 * Sets mostDifficultForJobOriginal.
	 *
	 * @param mostDifficultForJobOriginal
	 *            mostDifficultForJobOriginal
	 */
	public void setMostDifficultForJobOriginal(String mostDifficultForJobOriginal)
	{
		this.mostDifficultForJobOriginal = mostDifficultForJobOriginal;
	}

	/**
	 * Gets mostDifficultForJobTranslated.
	 *
	 * @return mostDifficultForJobTranslated
	 */
	public String getMostDifficultForJobTranslated()
	{
		return mostDifficultForJobTranslated;
	}

	/**
	 * Sets mostDifficultForJobTranslated.
	 *
	 * @param mostDifficultForJobTranslated
	 *            mostDifficultForJobTranslated
	 */
	public void setMostDifficultForJobTranslated(String mostDifficultForJobTranslated)
	{
		this.mostDifficultForJobTranslated = mostDifficultForJobTranslated;
	}

	/**
	 * Gets emotionalDifficultiesOriginal.
	 *
	 * @return emotionalDifficultiesOriginal
	 */
	public String getEmotionalDifficultiesOriginal()
	{
		return emotionalDifficultiesOriginal;
	}

	/**
	 * Sets emotionalDifficultiesOriginal.
	 *
	 * @param emotionalDifficultiesOriginal
	 *            emotionalDifficultiesOriginal
	 */
	public void setEmotionalDifficultiesOriginal(String emotionalDifficultiesOriginal)
	{
		this.emotionalDifficultiesOriginal = emotionalDifficultiesOriginal;
	}

	/**
	 * Gets emotionalDifficultiesTranslated.
	 *
	 * @return emotionalDifficultiesTranslated
	 */
	public String getEmotionalDifficultiesTranslated()
	{
		return emotionalDifficultiesTranslated;
	}

	/**
	 * Sets emotionalDifficultiesTranslated.
	 *
	 * @param emotionalDifficultiesTranslated
	 *            emotionalDifficultiesTranslated
	 */
	public void setEmotionalDifficultiesTranslated(String emotionalDifficultiesTranslated)
	{
		this.emotionalDifficultiesTranslated = emotionalDifficultiesTranslated;
	}

	/**
	 * Gets avoidedSituationsOriginal.
	 *
	 * @return avoidedSituationsOriginal
	 */
	public String getAvoidedSituationsOriginal()
	{
		return avoidedSituationsOriginal;
	}

	/**
	 * Sets avoidedSituationsOriginal.
	 *
	 * @param avoidedSituationsOriginal
	 *            avoidedSituationsOriginal
	 */
	public void setAvoidedSituationsOriginal(String avoidedSituationsOriginal)
	{
		this.avoidedSituationsOriginal = avoidedSituationsOriginal;
	}

	/**
	 * Gets avoidedSituationsTranslated.
	 *
	 * @return avoidedSituationsTranslated
	 */
	public String getAvoidedSituationsTranslated()
	{
		return avoidedSituationsTranslated;
	}

	/**
	 * Sets avoidedSituationsTranslated.
	 *
	 * @param avoidedSituationsTranslated
	 *            avoidedSituationsTranslated
	 */
	public void setAvoidedSituationsTranslated(String avoidedSituationsTranslated)
	{
		this.avoidedSituationsTranslated = avoidedSituationsTranslated;
	}

	/**
	 * Gets hasEnoughSupportOriginal.
	 *
	 * @return hasEnoughSupportOriginal
	 */
	public String getHasEnoughSupportOriginal()
	{
		return hasEnoughSupportOriginal;
	}

	/**
	 * Sets hasEnoughSupportOriginal.
	 *
	 * @param hasEnoughSupportOriginal
	 *            hasEnoughSupportOriginal
	 */
	public void setHasEnoughSupportOriginal(String hasEnoughSupportOriginal)
	{
		this.hasEnoughSupportOriginal = hasEnoughSupportOriginal;
	}

	/**
	 * Gets hasEnoughSupportTranslated.
	 *
	 * @return hasEnoughSupportTranslated
	 */
	public String getHasEnoughSupportTranslated()
	{
		return hasEnoughSupportTranslated;
	}

	/**
	 * Sets hasEnoughSupportTranslated.
	 *
	 * @param hasEnoughSupportTranslated
	 *            hasEnoughSupportTranslated
	 */
	public void setHasEnoughSupportTranslated(String hasEnoughSupportTranslated)
	{
		this.hasEnoughSupportTranslated = hasEnoughSupportTranslated;
	}

	/**
	 * Gets desiredResourcesOriginal.
	 *
	 * @return desiredResourcesOriginal
	 */
	public String getDesiredResourcesOriginal()
	{
		return desiredResourcesOriginal;
	}

	/**
	 * Sets desiredResourcesOriginal.
	 *
	 * @param desiredResourcesOriginal
	 *            desiredResourcesOriginal
	 */
	public void setDesiredResourcesOriginal(String desiredResourcesOriginal)
	{
		this.desiredResourcesOriginal = desiredResourcesOriginal;
	}

	/**
	 * Gets desiredResourcesTranslated.
	 *
	 * @return desiredResourcesTranslated
	 */
	public String getDesiredResourcesTranslated()
	{
		return desiredResourcesTranslated;
	}

	/**
	 * Sets desiredResourcesTranslated.
	 *
	 * @param desiredResourcesTranslated
	 *            desiredResourcesTranslated
	 */
	public void setDesiredResourcesTranslated(String desiredResourcesTranslated)
	{
		this.desiredResourcesTranslated = desiredResourcesTranslated;
	}

	/**
	 * Gets willingToInterview.
	 *
	 * @return willingToInterview
	 */
	public YesNo getWillingToInterview()
	{
		return willingToInterview;
	}

	/**
	 * Sets willingToInterview.
	 *
	 * @param willingToInterview
	 *            willingToInterview
	 */
	public void setWillingToInterview(YesNo willingToInterview)
	{
		this.willingToInterview = willingToInterview;
	}

	/**
	 * Gets interviewDeclineReasonOriginal.
	 *
	 * @return interviewDeclineReasonOriginal
	 */
	public String getInterviewDeclineReasonOriginal()
	{
		return interviewDeclineReasonOriginal;
	}

	/**
	 * Sets interviewDeclineReasonOriginal.
	 *
	 * @param interviewDeclineReasonOriginal
	 *            interviewDeclineReasonOriginal
	 */
	public void setInterviewDeclineReasonOriginal(String interviewDeclineReasonOriginal)
	{
		this.interviewDeclineReasonOriginal = interviewDeclineReasonOriginal;
	}

	/**
	 * Gets interviewDeclineReasonTranslated.
	 *
	 * @return interviewDeclineReasonTranslated
	 */
	public String getInterviewDeclineReasonTranslated()
	{
		return interviewDeclineReasonTranslated;
	}

	/**
	 * Sets interviewDeclineReasonTranslated.
	 *
	 * @param interviewDeclineReasonTranslated
	 *            interviewDeclineReasonTranslated
	 */
	public void setInterviewDeclineReasonTranslated(String interviewDeclineReasonTranslated)
	{
		this.interviewDeclineReasonTranslated = interviewDeclineReasonTranslated;
	}

	/**
	 * Gets preferredInterviewType.
	 *
	 * @return preferredInterviewType
	 */
	public InterviewTypePreference getPreferredInterviewType()
	{
		return preferredInterviewType;
	}

	/**
	 * Sets preferredInterviewType.
	 *
	 * @param preferredInterviewType
	 *            preferredInterviewType
	 */
	public void setPreferredInterviewType(InterviewTypePreference preferredInterviewType)
	{
		this.preferredInterviewType = preferredInterviewType;
	}

	/**
	 * Gets contactInfo.
	 *
	 * @return contactInfo
	 */
	public String getContactInfo()
	{
		return contactInfo;
	}

	/**
	 * Sets contactInfo.
	 *
	 * @param contactInfo
	 *            contactInfo
	 */
	public void setContactInfo(String contactInfo)
	{
		this.contactInfo = contactInfo;
	}

	/**
	 * Gets additionalCommentsOriginal.
	 *
	 * @return additionalCommentsOriginal
	 */
	public String getAdditionalCommentsOriginal()
	{
		return additionalCommentsOriginal;
	}

	/**
	 * Sets additionalCommentsOriginal.
	 *
	 * @param additionalCommentsOriginal
	 *            additionalCommentsOriginal
	 */
	public void setAdditionalCommentsOriginal(String additionalCommentsOriginal)
	{
		this.additionalCommentsOriginal = additionalCommentsOriginal;
	}

	/**
	 * Gets additionalCommentsTranslated.
	 *
	 * @return additionalCommentsTranslated
	 */
	public String getAdditionalCommentsTranslated()
	{
		return additionalCommentsTranslated;
	}

	/**
	 * Sets additionalCommentsTranslated.
	 *
	 * @param additionalCommentsTranslated
	 *            additionalCommentsTranslated
	 */
	public void setAdditionalCommentsTranslated(String additionalCommentsTranslated)
	{
		this.additionalCommentsTranslated = additionalCommentsTranslated;
	}

	/**
	 * Gets avgSpeakingAbility.
	 *
	 * @return avgSpeakingAbility
	 */
	public BigDecimal getAvgSpeakingAbility()
	{
		return avgSpeakingAbility;
	}

	/**
	 * Sets avgSpeakingAbility.
	 *
	 * @param avgSpeakingAbility
	 *            avgSpeakingAbility
	 */
	public void setAvgSpeakingAbility(BigDecimal avgSpeakingAbility)
	{
		this.avgSpeakingAbility = avgSpeakingAbility;
	}

	/**
	 * Gets avgDifficultyExpressing.
	 *
	 * @return avgDifficultyExpressing
	 */
	public BigDecimal getAvgDifficultyExpressing()
	{
		return avgDifficultyExpressing;
	}

	/**
	 * Sets avgDifficultyExpressing.
	 *
	 * @param avgDifficultyExpressing
	 *            avgDifficultyExpressing
	 */
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
