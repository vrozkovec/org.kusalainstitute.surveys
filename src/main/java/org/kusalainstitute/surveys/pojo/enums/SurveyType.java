package org.kusalainstitute.surveys.pojo.enums;

/**
 * Type of survey - pre or post app usage.
 */
public enum SurveyType {

    /** Pre-survey taken before starting to use the Latudio app. */
    PRE("Pre-Survey", "Before app usage"),

    /** Post-survey taken after using the Latudio app. */
    POST("Post-Survey", "After app usage");

    private final String displayName;
    private final String description;

    SurveyType(String displayName, String description) {
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
