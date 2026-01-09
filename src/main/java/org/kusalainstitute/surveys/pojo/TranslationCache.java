package org.kusalainstitute.surveys.pojo;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Cache for translated text to avoid duplicate API calls.
 */
public class TranslationCache {

    private Long id;
    private String sourceTextHash;
    private String sourceText;
    private String translatedText;
    private String sourceLanguage;
    private String targetLanguage;
    private LocalDateTime createdAt;

    public TranslationCache() {
        this.createdAt = LocalDateTime.now();
        this.sourceLanguage = "FR";
        this.targetLanguage = "EN";
    }

    /**
     * Creates a new TranslationCache entry.
     *
     * @param sourceTextHash SHA-256 hash of source text
     * @param sourceText     original text
     * @param translatedText translated text
     */
    public TranslationCache(String sourceTextHash, String sourceText, String translatedText) {
        this();
        this.sourceTextHash = sourceTextHash;
        this.sourceText = sourceText;
        this.translatedText = translatedText;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceTextHash() {
        return sourceTextHash;
    }

    public void setSourceTextHash(String sourceTextHash) {
        this.sourceTextHash = sourceTextHash;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationCache that = (TranslationCache) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TranslationCache{" +
                "id=" + id +
                ", sourceTextHash='" + sourceTextHash + '\'' +
                ", sourceLanguage='" + sourceLanguage + '\'' +
                ", targetLanguage='" + targetLanguage + '\'' +
                '}';
    }
}
