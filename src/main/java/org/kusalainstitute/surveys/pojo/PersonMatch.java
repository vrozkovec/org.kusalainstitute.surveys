package org.kusalainstitute.surveys.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.kusalainstitute.surveys.pojo.enums.MatchType;

/**
 * Represents a match between a pre-survey person and a post-survey person.
 * Matching is done within the same cohort only.
 */
public class PersonMatch {

    private Long id;
    private String cohort;
    private Long prePersonId;
    private Long postPersonId;
    private MatchType matchType;
    private BigDecimal confidence;
    private LocalDateTime matchedAt;
    private String matchedBy;
    private String notes;

    public PersonMatch() {
        this.matchedAt = LocalDateTime.now();
    }

    /**
     * Creates a new PersonMatch with the given parameters.
     *
     * @param cohort       the cohort code
     * @param prePersonId  ID of the pre-survey person
     * @param postPersonId ID of the post-survey person
     * @param matchType    type of match (AUTO_EMAIL, AUTO_NAME, MANUAL)
     * @param confidence   confidence score (0.00-1.00) for fuzzy matches
     */
    public PersonMatch(String cohort, Long prePersonId, Long postPersonId,
                       MatchType matchType, BigDecimal confidence) {
        this();
        this.cohort = cohort;
        this.prePersonId = prePersonId;
        this.postPersonId = postPersonId;
        this.matchType = matchType;
        this.confidence = confidence;
        this.matchedBy = matchType == MatchType.MANUAL ? null : "SYSTEM";
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCohort() {
        return cohort;
    }

    public void setCohort(String cohort) {
        this.cohort = cohort;
    }

    public Long getPrePersonId() {
        return prePersonId;
    }

    public void setPrePersonId(Long prePersonId) {
        this.prePersonId = prePersonId;
    }

    public Long getPostPersonId() {
        return postPersonId;
    }

    public void setPostPersonId(Long postPersonId) {
        this.postPersonId = postPersonId;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public LocalDateTime getMatchedAt() {
        return matchedAt;
    }

    public void setMatchedAt(LocalDateTime matchedAt) {
        this.matchedAt = matchedAt;
    }

    public String getMatchedBy() {
        return matchedBy;
    }

    public void setMatchedBy(String matchedBy) {
        this.matchedBy = matchedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonMatch that = (PersonMatch) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PersonMatch{" +
                "id=" + id +
                ", cohort='" + cohort + '\'' +
                ", prePersonId=" + prePersonId +
                ", postPersonId=" + postPersonId +
                ", matchType=" + matchType +
                ", confidence=" + confidence +
                '}';
    }
}
