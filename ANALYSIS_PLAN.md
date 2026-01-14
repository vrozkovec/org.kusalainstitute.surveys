# Latudio PRE/POST Survey Data Analysis Plan

This document outlines methods for analyzing the collected quantitative and qualitative data using your existing database structure (`person`, `person_match`, `pre_survey_response`, `post_survey_response`).

---

## Implementation Status

### Completed Features

- ✅ **Person Matching** - Pre/post surveys matched by cohort + email/name
- ✅ **Situation-by-Situation Analysis** - 11 speaking situations compared
- ✅ **SituationAnalysisPanel** - Shows PRE/POST comparison with delta change
- ✅ **OpenEndedQuestionsPanel** - Displays all free-text questions with answers
- ✅ **DeepL Translation** - All French free-text answers translated to English
- ✅ **Per-Student Total Change** - Sticky column showing total speaking improvement
- ✅ **Cohort Label** - Each student shows their cohort (YTI, YTC, etc.)
- ✅ **Column Averages** - Footer row shows average change per situation
- ✅ **Data Import** - Excel parser for both PRE and POST surveys

### Pending Features (Simple Analysis)

- ⬜ Improvement Summary Statistics
- ⬜ Improvement Categories (Much Better, Better, Same, Worse)
- ⬜ Before/After Rating Distribution
- ⬜ Cohort Comparison Table
- ⬜ Top/Bottom Situations Ranking

---

## I. Quantitative Analysis: Measuring Impact

### 1. Improvement Summary Statistics

Shows a quick overview of how many students improved.

**Example Output:**
```
Total Matched Students: 25
Students Improved: 20 (80%)
Students Same: 3 (12%)
Students Worse: 2 (8%)
Average Improvement: +0.65 points
```

**How it works:**
- Compare each student's POST average vs PRE average
- Count how many went up, stayed same, went down
- Calculate percentages

### 2. Improvement Categories

Groups students into easy-to-understand categories.

| Category | Change Range | Description |
|----------|--------------|-------------|
| Much Better | +2.0 or more | Big improvement |
| Better | +0.5 to +2.0 | Clear improvement |
| Slight | +0.1 to +0.5 | Small improvement |
| Same | -0.1 to +0.1 | No change |
| Worse | below -0.1 | Got worse |

**Example Table:**
| Category | Count | Percentage |
|----------|-------|------------|
| Much Better | 5 | 20% |
| Better | 12 | 48% |
| Slight | 3 | 12% |
| Same | 3 | 12% |
| Worse | 2 | 8% |

### 3. Before/After Rating Distribution

Shows how answers shifted from PRE to POST survey.

**Example for "Speaking to Healthcare Providers":**
```
Before (PRE):
  Rating 1 (Can't at all): ████████ 8 students
  Rating 2 (Struggle):     ██████████ 10 students
  Rating 3 (Okay):         ████ 4 students
  Rating 4 (Good):         ██ 2 students
  Rating 5 (Very good):    █ 1 student

After (POST):
  Rating 1 (Can't at all): ██ 2 students
  Rating 2 (Struggle):     ████ 4 students
  Rating 3 (Okay):         ████████ 8 students
  Rating 4 (Good):         ██████ 6 students
  Rating 5 (Very good):    █████ 5 students
```

This visualization clearly shows the shift toward higher ratings.

### 4. Cohort Comparison

Compare different groups (YTI, YTC, etc.).

| Cohort | Students | Avg PRE | Avg POST | Improvement | Best Situation |
|--------|----------|---------|----------|-------------|----------------|
| YTI | 10 | 2.3 | 3.1 | +0.8 | Healthcare |
| YTC | 8 | 2.5 | 3.0 | +0.5 | Directions |
| YTF | 7 | 2.1 | 2.9 | +0.8 | Shopping |

### 5. Top/Bottom Situations

Rank which speaking situations improved the most/least.

**Top 3 Most Improved:**
1. Healthcare (+0.9) - Biggest improvement
2. Directions (+0.7)
3. Shopping (+0.6)

**Bottom 3 Least Improved:**
1. Job Interview (+0.2) - Needs more practice
2. Authorities (+0.3)
3. Landlord (+0.3)

---

## II. Qualitative Analysis: Understanding the 'Why'

The translated free-text fields provide context and personal stories behind the numbers.

### Free-Text Questions Overview

**PRE Survey (5 questions):**
| Q# | Field | Topic |
|----|-------|-------|
| Q7 | mostDifficultThing | Most difficult thing about speaking English |
| Q8 | whyImproveEnglish | Why do you want to improve your English |
| Q9 | otherSituations | Other situations where you want to use English |
| Q10 | difficultPart | Most difficult part about speaking English |
| Q11 | describeSituations | Describe situations where language barrier affected you |

**POST Survey (9 questions):**
| Q# | Field | Topic |
|----|-------|-------|
| Q5 | whatHelpedMost | What helped most in the app |
| Q8 | mostDifficultOverall | Most difficult thing overall |
| Q9 | mostDifficultForJob | Most difficult for job |
| Q10 | emotionalDifficulties | Emotional difficulties when speaking |
| Q11 | avoidedSituations | Situations avoided because of language |
| Q12 | hasEnoughSupport | Do you have enough support |
| Q13 | desiredResources | What resources would help |
| Q15 | interviewDeclineReason | Why not willing to interview |
| Q17 | additionalComments | Additional comments |

### Thematic Coding Approach

**Goal:** Group similar answers into themes and count them.

**Example Themes for "What helped most" (Q5):**
| Theme | Count | Example Quote |
|-------|-------|---------------|
| Pronunciation practice | 12 | "The app helped me pronounce words correctly" |
| Role-play scenarios | 8 | "Practicing real conversations was useful" |
| Immediate feedback | 6 | "Getting corrections right away helped" |
| Confidence building | 5 | "I feel more confident now" |

### PRE vs POST Comparison

Compare how themes change from before to after using the app.

**Example - Emotional Difficulties:**
| Theme | PRE Count | POST Count | Change |
|-------|-----------|------------|--------|
| Fear of judgment | 15 (60%) | 5 (20%) | -40% |
| Anxiety/Stress | 12 (48%) | 4 (16%) | -32% |
| Lack of confidence | 10 (40%) | 3 (12%) | -28% |

---

## III. Java Implementation Checklist

### Completed

- ✅ `AnalysisService` - Core analysis calculations
- ✅ `PersonMatchingService` - Matches PRE/POST surveys
- ✅ `SituationAnalysisModel` - Data model for situation analysis
- ✅ `SituationAnalysisPanel` - Wicket panel displaying results
- ✅ `OpenEndedQuestionsPanel` - Displays qualitative data
- ✅ `TranslationService` - DeepL integration for French → English

### To Implement

- ⬜ Add `ImprovementSummaryPanel` - Shows summary statistics
- ⬜ Add `ImprovementCategoriesPanel` - Groups students by improvement level
- ⬜ Add `RatingDistributionPanel` - Before/after bar charts
- ⬜ Add `CohortComparisonPanel` - Compare different cohorts
- ⬜ Add `SituationRankingPanel` - Top/bottom situations

### Data Model Extensions

```java
// Improvement categories
public enum ImprovementCategory {
    MUCH_BETTER,  // +2.0 or more
    BETTER,       // +0.5 to +2.0
    SLIGHT,       // +0.1 to +0.5
    SAME,         // -0.1 to +0.1
    WORSE         // below -0.1
}

// Summary statistics
public record ImprovementSummary(
    int totalStudents,
    int improved,
    int same,
    int worse,
    BigDecimal averageImprovement
) {}
```

---

## IV. Key Metrics Reference

| Metric | What it Shows | How to Read It |
|--------|---------------|----------------|
| Average Improvement | Overall change | Positive = students got better |
| % Improved | How many got better | Higher = more success |
| Improvement Categories | Distribution of change | More in "Better"/"Much Better" = good |
| Before/After Distribution | Rating shifts | Shift right = improvement |
| Cohort Comparison | Group differences | Identify successful groups |
| Situation Ranking | Where app helps most | Focus on weak areas |

---

## V. Report Generation Ideas

### Summary Report (1 page)
- Total students matched
- Percentage improved
- Average improvement
- Top 3 most improved situations
- Key quote from student

### Detailed Report
- All metrics from sections above
- Full situation-by-situation breakdown
- Cohort comparison
- Selected qualitative quotes

### Data Export
- CSV with all matched pairs and their scores
- JSON for further analysis
- HTML report for sharing
