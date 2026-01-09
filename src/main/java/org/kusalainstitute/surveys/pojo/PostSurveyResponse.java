package org.kusalainstitute.surveys.pojo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Post-survey response data collected after using the Latudio app.
 * Contains app usage information, English ability levels, and qualitative feedback.
 */
public class PostSurveyResponse {

    private Long id;
    private Long personId;
    private LocalDateTime timestamp;
    private String sourceFile;
    private Integer rowNumber;

    // App usage Q1-Q4
    private String appUsageDuration;
    private String appTimePerSession;
    private String appFrequency;
    private String progressAssessment;

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

    // Q7 Difficulty expressing (11 free-text fields)
    private String difficultyDirectionsOriginal;
    private String difficultyDirectionsTranslated;
    private String difficultyHealthcareOriginal;
    private String difficultyHealthcareTranslated;
    private String difficultyAuthoritiesOriginal;
    private String difficultyAuthoritiesTranslated;
    private String difficultyJobInterviewOriginal;
    private String difficultyJobInterviewTranslated;
    private String difficultyInformalOriginal;
    private String difficultyInformalTranslated;
    private String difficultyChildrenEducationOriginal;
    private String difficultyChildrenEducationTranslated;
    private String difficultyLandlordOriginal;
    private String difficultyLandlordTranslated;
    private String difficultySocialEventsOriginal;
    private String difficultySocialEventsTranslated;
    private String difficultyLocalServicesOriginal;
    private String difficultyLocalServicesTranslated;
    private String difficultySupportOrgsOriginal;
    private String difficultySupportOrgsTranslated;
    private String difficultyShoppingOriginal;
    private String difficultyShoppingTranslated;

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
    private String willingToInterview;
    private String interviewDeclineReasonOriginal;
    private String interviewDeclineReasonTranslated;
    private String preferredInterviewType;
    private String contactInfo;

    // Q17 Free text
    private String additionalCommentsOriginal;
    private String additionalCommentsTranslated;

    // Calculated average
    private BigDecimal avgSpeakingAbility;

    public PostSurveyResponse() {
    }

    /**
     * Calculates and sets the average speaking ability from all 11 situations.
     */
    public void calculateAverages() {
        this.avgSpeakingAbility = calculateSpeakingAverage();
    }

    /**
     * Calculates the average speaking ability.
     *
     * @return average of all non-null speaking ability values, or null if all are null
     */
    public BigDecimal calculateSpeakingAverage() {
        int sum = 0;
        int count = 0;

        Integer[] values = {
                speakDirections, speakHealthcare, speakAuthorities, speakJobInterview,
                speakInformal, speakChildrenEducation, speakLandlord, speakSocialEvents,
                speakLocalServices, speakSupportOrgs, speakShopping
        };

        for (Integer value : values) {
            if (value != null) {
                sum += value;
                count++;
            }
        }

        if (count == 0) {
            return null;
        }

        return BigDecimal.valueOf(sum)
                .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getAppUsageDuration() {
        return appUsageDuration;
    }

    public void setAppUsageDuration(String appUsageDuration) {
        this.appUsageDuration = appUsageDuration;
    }

    public String getAppTimePerSession() {
        return appTimePerSession;
    }

    public void setAppTimePerSession(String appTimePerSession) {
        this.appTimePerSession = appTimePerSession;
    }

    public String getAppFrequency() {
        return appFrequency;
    }

    public void setAppFrequency(String appFrequency) {
        this.appFrequency = appFrequency;
    }

    public String getProgressAssessment() {
        return progressAssessment;
    }

    public void setProgressAssessment(String progressAssessment) {
        this.progressAssessment = progressAssessment;
    }

    public String getWhatHelpedMostOriginal() {
        return whatHelpedMostOriginal;
    }

    public void setWhatHelpedMostOriginal(String whatHelpedMostOriginal) {
        this.whatHelpedMostOriginal = whatHelpedMostOriginal;
    }

    public String getWhatHelpedMostTranslated() {
        return whatHelpedMostTranslated;
    }

    public void setWhatHelpedMostTranslated(String whatHelpedMostTranslated) {
        this.whatHelpedMostTranslated = whatHelpedMostTranslated;
    }

    public Integer getSpeakDirections() {
        return speakDirections;
    }

    public void setSpeakDirections(Integer speakDirections) {
        this.speakDirections = speakDirections;
    }

    public Integer getSpeakHealthcare() {
        return speakHealthcare;
    }

    public void setSpeakHealthcare(Integer speakHealthcare) {
        this.speakHealthcare = speakHealthcare;
    }

    public Integer getSpeakAuthorities() {
        return speakAuthorities;
    }

    public void setSpeakAuthorities(Integer speakAuthorities) {
        this.speakAuthorities = speakAuthorities;
    }

    public Integer getSpeakJobInterview() {
        return speakJobInterview;
    }

    public void setSpeakJobInterview(Integer speakJobInterview) {
        this.speakJobInterview = speakJobInterview;
    }

    public Integer getSpeakInformal() {
        return speakInformal;
    }

    public void setSpeakInformal(Integer speakInformal) {
        this.speakInformal = speakInformal;
    }

    public Integer getSpeakChildrenEducation() {
        return speakChildrenEducation;
    }

    public void setSpeakChildrenEducation(Integer speakChildrenEducation) {
        this.speakChildrenEducation = speakChildrenEducation;
    }

    public Integer getSpeakLandlord() {
        return speakLandlord;
    }

    public void setSpeakLandlord(Integer speakLandlord) {
        this.speakLandlord = speakLandlord;
    }

    public Integer getSpeakSocialEvents() {
        return speakSocialEvents;
    }

    public void setSpeakSocialEvents(Integer speakSocialEvents) {
        this.speakSocialEvents = speakSocialEvents;
    }

    public Integer getSpeakLocalServices() {
        return speakLocalServices;
    }

    public void setSpeakLocalServices(Integer speakLocalServices) {
        this.speakLocalServices = speakLocalServices;
    }

    public Integer getSpeakSupportOrgs() {
        return speakSupportOrgs;
    }

    public void setSpeakSupportOrgs(Integer speakSupportOrgs) {
        this.speakSupportOrgs = speakSupportOrgs;
    }

    public Integer getSpeakShopping() {
        return speakShopping;
    }

    public void setSpeakShopping(Integer speakShopping) {
        this.speakShopping = speakShopping;
    }

    public String getDifficultyDirectionsOriginal() {
        return difficultyDirectionsOriginal;
    }

    public void setDifficultyDirectionsOriginal(String difficultyDirectionsOriginal) {
        this.difficultyDirectionsOriginal = difficultyDirectionsOriginal;
    }

    public String getDifficultyDirectionsTranslated() {
        return difficultyDirectionsTranslated;
    }

    public void setDifficultyDirectionsTranslated(String difficultyDirectionsTranslated) {
        this.difficultyDirectionsTranslated = difficultyDirectionsTranslated;
    }

    public String getDifficultyHealthcareOriginal() {
        return difficultyHealthcareOriginal;
    }

    public void setDifficultyHealthcareOriginal(String difficultyHealthcareOriginal) {
        this.difficultyHealthcareOriginal = difficultyHealthcareOriginal;
    }

    public String getDifficultyHealthcareTranslated() {
        return difficultyHealthcareTranslated;
    }

    public void setDifficultyHealthcareTranslated(String difficultyHealthcareTranslated) {
        this.difficultyHealthcareTranslated = difficultyHealthcareTranslated;
    }

    public String getDifficultyAuthoritiesOriginal() {
        return difficultyAuthoritiesOriginal;
    }

    public void setDifficultyAuthoritiesOriginal(String difficultyAuthoritiesOriginal) {
        this.difficultyAuthoritiesOriginal = difficultyAuthoritiesOriginal;
    }

    public String getDifficultyAuthoritiesTranslated() {
        return difficultyAuthoritiesTranslated;
    }

    public void setDifficultyAuthoritiesTranslated(String difficultyAuthoritiesTranslated) {
        this.difficultyAuthoritiesTranslated = difficultyAuthoritiesTranslated;
    }

    public String getDifficultyJobInterviewOriginal() {
        return difficultyJobInterviewOriginal;
    }

    public void setDifficultyJobInterviewOriginal(String difficultyJobInterviewOriginal) {
        this.difficultyJobInterviewOriginal = difficultyJobInterviewOriginal;
    }

    public String getDifficultyJobInterviewTranslated() {
        return difficultyJobInterviewTranslated;
    }

    public void setDifficultyJobInterviewTranslated(String difficultyJobInterviewTranslated) {
        this.difficultyJobInterviewTranslated = difficultyJobInterviewTranslated;
    }

    public String getDifficultyInformalOriginal() {
        return difficultyInformalOriginal;
    }

    public void setDifficultyInformalOriginal(String difficultyInformalOriginal) {
        this.difficultyInformalOriginal = difficultyInformalOriginal;
    }

    public String getDifficultyInformalTranslated() {
        return difficultyInformalTranslated;
    }

    public void setDifficultyInformalTranslated(String difficultyInformalTranslated) {
        this.difficultyInformalTranslated = difficultyInformalTranslated;
    }

    public String getDifficultyChildrenEducationOriginal() {
        return difficultyChildrenEducationOriginal;
    }

    public void setDifficultyChildrenEducationOriginal(String difficultyChildrenEducationOriginal) {
        this.difficultyChildrenEducationOriginal = difficultyChildrenEducationOriginal;
    }

    public String getDifficultyChildrenEducationTranslated() {
        return difficultyChildrenEducationTranslated;
    }

    public void setDifficultyChildrenEducationTranslated(String difficultyChildrenEducationTranslated) {
        this.difficultyChildrenEducationTranslated = difficultyChildrenEducationTranslated;
    }

    public String getDifficultyLandlordOriginal() {
        return difficultyLandlordOriginal;
    }

    public void setDifficultyLandlordOriginal(String difficultyLandlordOriginal) {
        this.difficultyLandlordOriginal = difficultyLandlordOriginal;
    }

    public String getDifficultyLandlordTranslated() {
        return difficultyLandlordTranslated;
    }

    public void setDifficultyLandlordTranslated(String difficultyLandlordTranslated) {
        this.difficultyLandlordTranslated = difficultyLandlordTranslated;
    }

    public String getDifficultySocialEventsOriginal() {
        return difficultySocialEventsOriginal;
    }

    public void setDifficultySocialEventsOriginal(String difficultySocialEventsOriginal) {
        this.difficultySocialEventsOriginal = difficultySocialEventsOriginal;
    }

    public String getDifficultySocialEventsTranslated() {
        return difficultySocialEventsTranslated;
    }

    public void setDifficultySocialEventsTranslated(String difficultySocialEventsTranslated) {
        this.difficultySocialEventsTranslated = difficultySocialEventsTranslated;
    }

    public String getDifficultyLocalServicesOriginal() {
        return difficultyLocalServicesOriginal;
    }

    public void setDifficultyLocalServicesOriginal(String difficultyLocalServicesOriginal) {
        this.difficultyLocalServicesOriginal = difficultyLocalServicesOriginal;
    }

    public String getDifficultyLocalServicesTranslated() {
        return difficultyLocalServicesTranslated;
    }

    public void setDifficultyLocalServicesTranslated(String difficultyLocalServicesTranslated) {
        this.difficultyLocalServicesTranslated = difficultyLocalServicesTranslated;
    }

    public String getDifficultySupportOrgsOriginal() {
        return difficultySupportOrgsOriginal;
    }

    public void setDifficultySupportOrgsOriginal(String difficultySupportOrgsOriginal) {
        this.difficultySupportOrgsOriginal = difficultySupportOrgsOriginal;
    }

    public String getDifficultySupportOrgsTranslated() {
        return difficultySupportOrgsTranslated;
    }

    public void setDifficultySupportOrgsTranslated(String difficultySupportOrgsTranslated) {
        this.difficultySupportOrgsTranslated = difficultySupportOrgsTranslated;
    }

    public String getDifficultyShoppingOriginal() {
        return difficultyShoppingOriginal;
    }

    public void setDifficultyShoppingOriginal(String difficultyShoppingOriginal) {
        this.difficultyShoppingOriginal = difficultyShoppingOriginal;
    }

    public String getDifficultyShoppingTranslated() {
        return difficultyShoppingTranslated;
    }

    public void setDifficultyShoppingTranslated(String difficultyShoppingTranslated) {
        this.difficultyShoppingTranslated = difficultyShoppingTranslated;
    }

    public String getMostDifficultOverallOriginal() {
        return mostDifficultOverallOriginal;
    }

    public void setMostDifficultOverallOriginal(String mostDifficultOverallOriginal) {
        this.mostDifficultOverallOriginal = mostDifficultOverallOriginal;
    }

    public String getMostDifficultOverallTranslated() {
        return mostDifficultOverallTranslated;
    }

    public void setMostDifficultOverallTranslated(String mostDifficultOverallTranslated) {
        this.mostDifficultOverallTranslated = mostDifficultOverallTranslated;
    }

    public String getMostDifficultForJobOriginal() {
        return mostDifficultForJobOriginal;
    }

    public void setMostDifficultForJobOriginal(String mostDifficultForJobOriginal) {
        this.mostDifficultForJobOriginal = mostDifficultForJobOriginal;
    }

    public String getMostDifficultForJobTranslated() {
        return mostDifficultForJobTranslated;
    }

    public void setMostDifficultForJobTranslated(String mostDifficultForJobTranslated) {
        this.mostDifficultForJobTranslated = mostDifficultForJobTranslated;
    }

    public String getEmotionalDifficultiesOriginal() {
        return emotionalDifficultiesOriginal;
    }

    public void setEmotionalDifficultiesOriginal(String emotionalDifficultiesOriginal) {
        this.emotionalDifficultiesOriginal = emotionalDifficultiesOriginal;
    }

    public String getEmotionalDifficultiesTranslated() {
        return emotionalDifficultiesTranslated;
    }

    public void setEmotionalDifficultiesTranslated(String emotionalDifficultiesTranslated) {
        this.emotionalDifficultiesTranslated = emotionalDifficultiesTranslated;
    }

    public String getAvoidedSituationsOriginal() {
        return avoidedSituationsOriginal;
    }

    public void setAvoidedSituationsOriginal(String avoidedSituationsOriginal) {
        this.avoidedSituationsOriginal = avoidedSituationsOriginal;
    }

    public String getAvoidedSituationsTranslated() {
        return avoidedSituationsTranslated;
    }

    public void setAvoidedSituationsTranslated(String avoidedSituationsTranslated) {
        this.avoidedSituationsTranslated = avoidedSituationsTranslated;
    }

    public String getHasEnoughSupport() {
        return hasEnoughSupport;
    }

    public void setHasEnoughSupport(String hasEnoughSupport) {
        this.hasEnoughSupport = hasEnoughSupport;
    }

    public String getDesiredResourcesOriginal() {
        return desiredResourcesOriginal;
    }

    public void setDesiredResourcesOriginal(String desiredResourcesOriginal) {
        this.desiredResourcesOriginal = desiredResourcesOriginal;
    }

    public String getDesiredResourcesTranslated() {
        return desiredResourcesTranslated;
    }

    public void setDesiredResourcesTranslated(String desiredResourcesTranslated) {
        this.desiredResourcesTranslated = desiredResourcesTranslated;
    }

    public String getWillingToInterview() {
        return willingToInterview;
    }

    public void setWillingToInterview(String willingToInterview) {
        this.willingToInterview = willingToInterview;
    }

    public String getInterviewDeclineReasonOriginal() {
        return interviewDeclineReasonOriginal;
    }

    public void setInterviewDeclineReasonOriginal(String interviewDeclineReasonOriginal) {
        this.interviewDeclineReasonOriginal = interviewDeclineReasonOriginal;
    }

    public String getInterviewDeclineReasonTranslated() {
        return interviewDeclineReasonTranslated;
    }

    public void setInterviewDeclineReasonTranslated(String interviewDeclineReasonTranslated) {
        this.interviewDeclineReasonTranslated = interviewDeclineReasonTranslated;
    }

    public String getPreferredInterviewType() {
        return preferredInterviewType;
    }

    public void setPreferredInterviewType(String preferredInterviewType) {
        this.preferredInterviewType = preferredInterviewType;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getAdditionalCommentsOriginal() {
        return additionalCommentsOriginal;
    }

    public void setAdditionalCommentsOriginal(String additionalCommentsOriginal) {
        this.additionalCommentsOriginal = additionalCommentsOriginal;
    }

    public String getAdditionalCommentsTranslated() {
        return additionalCommentsTranslated;
    }

    public void setAdditionalCommentsTranslated(String additionalCommentsTranslated) {
        this.additionalCommentsTranslated = additionalCommentsTranslated;
    }

    public BigDecimal getAvgSpeakingAbility() {
        return avgSpeakingAbility;
    }

    public void setAvgSpeakingAbility(BigDecimal avgSpeakingAbility) {
        this.avgSpeakingAbility = avgSpeakingAbility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostSurveyResponse that = (PostSurveyResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PostSurveyResponse{" +
                "id=" + id +
                ", personId=" + personId +
                ", timestamp=" + timestamp +
                ", avgSpeakingAbility=" + avgSpeakingAbility +
                '}';
    }
}
