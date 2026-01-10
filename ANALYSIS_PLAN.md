
# 🚀 Latudio PRE/POST Survey Data Analysis Plan

This document outlines the best methods for analyzing the collected quantitative and qualitative data using your existing database structure (`person`, `person_match`, `pre_survey_response`, `post_survey_response`).

## 📊 I. Quantitative Analysis: Measuring Impact

The core goal is to move beyond simple averages and prove the statistical significance of any change in users' self-rated speaking ability.

### 1. The Power of Paired T-Tests

Since your data uses a `person_match` table, a **Paired Samples T-Test** is the most appropriate statistical method. It compares the means of two related groups (PRE score vs. POST score for the *same individual*).

**Goal:** Determine if the mean difference  in speaking confidence is statistically different from zero.

#### A. Overall Speaking Improvement

* **Focus:** The overall change in speaking confidence.
* **Metric:** Compare `pre_survey_response.avg_speaking_confidence` vs. `post_survey_response.avg_speaking_ability` for all matched pairs.
* **Deliverable:** Report the **Mean Change, T-statistic, Degrees of Freedom, and P-value**. A p-value of  indicates a statistically significant improvement.

#### B. Item-Level Improvement (Drill Down)

* **Focus:** Identify which of the 11 specific speaking situations (Question 6) saw the largest and most significant improvement.
* **Method:** Run **11 separate Paired T-Tests**, one for each situational speaking score (e.g., `speak_directions` PRE vs. `speak_directions` POST).
* **Deliverable:** A table ranking the 11 situations by the magnitude of the improvement and the significance of the change. This provides concrete evidence of where the app is most effective.

### 2. Usage and Segmentation Analysis

Segmenting your data helps correlate usage patterns with outcomes.

| Segmentation Criteria | Database Fields | Analysis Goal |
| --- | --- | --- |
| **Usage Intensity** | `post_survey_response.app_frequency` (Q3) and `app_time_per_session` (Q2) | Group matched pairs into 'High Users' vs. 'Low Users'. Compare the `avgConfidenceChange` between these groups to see if higher usage leads to better outcomes (dosage effect). |
| **Cohort Effectiveness** | `person.cohort` | Calculate the average `avgConfidenceChange` for each distinct cohort (`YTI`, `YTC`, etc.) to understand which demographic groups benefit most. |
| **Self-Assessment Check** | `post_survey_response.progress_assessment` (Q4) | Compare the quantitative `avgConfidenceChange` with the users' subjective feeling of progress. Do the numbers back up their feelings? |

---

## 🔍 II. Qualitative Analysis: Understanding the 'Why'

The rich, translated text fields provide the context and personal stories behind the numbers. This requires a systematic coding process.

### 1. Thematic Coding

**Goal:** Categorize and quantify the free-text responses (e.g., Q5, Q7, Q10, Q11) into a manageable set of themes.

| Question Pair | Focus | Key Themes to Code For |
| --- | --- | --- |
| **Q5 (What helped most)** | **Product Validation** | *Pronunciation Feedback, Simulated Role-Play, Variety/Relevance of Scenarios, Confidence Boost, Immediate Corrections.* |
| **Q7/Q8 (Difficult Part)** | **Specific Difficulties** | *Vocabulary Recall, Grammar/Tenses, Pronunciation, Listening Comprehension, Cultural Nerves.* |
| **Q10 (Emotional Difficulties)** | **Emotional Barriers** | *Fear of Judgment, Anxiety/Stress, Shame/Embarrassment, Lack of Confidence.* |
| **Q11 (Avoided Situations)** | **Behavioral Change** | *Medical/Healthcare, Job Interviews, Education/School, Bureaucratic Tasks (Landlord/Authorities).* |

**Deliverable:** Calculate the **frequency** of each theme in the PRE vs. the POST survey. For example, show that "Anxiety" (Q10) was mentioned by  of users in the PRE survey, but only  in the POST survey.

### 2. Paired Qualitative Contrast

**Goal:** Create compelling, individual case studies by comparing the PRE and POST answers side-by-side for the same user.

**Method:** Generate a detailed report (e.g., a CSV or JSON output from your Java service) for all matched pairs that includes:

1. **Quantitative Change:**  Speaking Confidence.
2. **PRE Q10 Answer:** `emotional_difficulties_translated`
3. **POST Q10 Answer:** `emotional_difficulties_translated`
4. **PRE Q11 Answer:** `avoided_situations_translated`
5. **POST Q11 Answer:** `avoided_situations_translated`

This allows you to select powerful quotes for your final report, such as a user who wrote about severe anxiety in their PRE survey but wrote about feeling "free to speak" in their POST survey.

---

## 🛠 III. Java Project Implementation Checklist

Your existing `AnalysisService` can be enhanced to provide all the above metrics.

### 1. Data Retrieval and Calculation

* ✅ **Completed:** Overall Average Confidence Change.
* ➡️ **Action:** Add methods to calculate the **Average Change for Each of the 11 Speaking Scenarios**.
* ➡️ **Action:** Create a helper function to calculate the **Standard Deviation of the Differences** for T-Test input.

### 2. Statistical Testing (T-Test)

* ➡️ **Action:** Integrate a statistical library (e.g., **Apache Commons Math**).
* ➡️ **Action:** Create a method `calculatePairedTTestResult(List<BigDecimal> differences)` that returns the T-statistic and P-value.

### 3. Reporting

* ➡️ **Action:** Enhance the `AnalysisResult` record to include a `Map<String, TTestResult>` for the 11 item-level speaking tests.
* ➡️ **Action:** Implement a method to generate a **Matched Pair Detailed Report** (CSV/JSON) containing the side-by-side translated qualitative answers for Q5, Q10, and Q11 for all matched participants.

---

Would you like to start with the SQL queries needed to fetch the individual speaking scores for the T-Test analysis?