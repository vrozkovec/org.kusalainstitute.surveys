package org.kusalainstitute.surveys.pojo.enums;

/**
 * Type of matching between pre and post survey respondents.
 */
public enum MatchType {

    /** Automatic match by exact email address within same cohort. */
    AUTO_EMAIL("Automatic (Email)", "Matched by exact email within cohort"),

    /** Automatic match by similar name using fuzzy matching within same cohort. */
    AUTO_NAME("Automatic (Name)", "Matched by similar name within cohort"),

    /** Manual match by administrator review. */
    MANUAL("Manual", "Manually matched by administrator");

    private final String displayName;
    private final String description;

    MatchType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
