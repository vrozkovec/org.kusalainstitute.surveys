package org.kusalainstitute.surveys.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.kusalainstitute.surveys.dao.MatchDao;
import org.kusalainstitute.surveys.dao.PersonDao;
import org.kusalainstitute.surveys.dao.PostSurveyDao;
import org.kusalainstitute.surveys.dao.PreSurveyDao;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.PersonMatch;
import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;
import org.kusalainstitute.surveys.pojo.enums.SurveyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for analyzing survey data and calculating statistics.
 */
public class AnalysisService {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisService.class);

    private final PersonDao personDao;
    private final PreSurveyDao preSurveyDao;
    private final PostSurveyDao postSurveyDao;
    private final MatchDao matchDao;

    public AnalysisService() {
        this.personDao = new PersonDao();
        this.preSurveyDao = new PreSurveyDao();
        this.postSurveyDao = new PostSurveyDao();
        this.matchDao = new MatchDao();
    }

    /**
     * Generates a comprehensive analysis of the survey data.
     *
     * @return analysis results
     * @throws SQLException if database operation fails
     */
    public AnalysisResult analyze() throws SQLException {
        LOG.info("Running survey analysis...");

        // Get counts
        List<Person> prePeople = personDao.findAllByType(SurveyType.PRE);
        List<Person> postPeople = personDao.findAllByType(SurveyType.POST);
        List<PersonMatch> matches = matchDao.findAll();

        // Calculate pre-survey averages
        List<PreSurveyResponse> preResponses = preSurveyDao.findAll();
        BigDecimal avgPreSpeaking = calculateAveragePreSpeaking(preResponses);
        BigDecimal avgPreUnderstanding = calculateAveragePreUnderstanding(preResponses);

        // Calculate post-survey averages
        List<PostSurveyResponse> postResponses = postSurveyDao.findAll();
        BigDecimal avgPostSpeaking = calculateAveragePostSpeaking(postResponses);

        // Calculate matched pair changes
        List<MatchedPairAnalysis> matchedAnalyses = analyzeMatchedPairs(matches);
        BigDecimal avgConfidenceChange = calculateAverageConfidenceChange(matchedAnalyses);

        // Get cohort breakdown
        List<String> cohorts = personDao.findAllCohorts();

        AnalysisResult result = new AnalysisResult(
                prePeople.size(),
                postPeople.size(),
                matches.size(),
                avgPreSpeaking,
                avgPreUnderstanding,
                avgPostSpeaking,
                avgConfidenceChange,
                matchedAnalyses,
                cohorts
        );

        LOG.info("Analysis complete: {} pre, {} post, {} matches, avg change: {}",
                result.preCount(), result.postCount(), result.matchedCount(), result.avgConfidenceChange());

        return result;
    }

    /**
     * Analyzes all matched pairs to calculate confidence changes.
     */
    private List<MatchedPairAnalysis> analyzeMatchedPairs(List<PersonMatch> matches) throws SQLException {
        List<MatchedPairAnalysis> results = new ArrayList<>();

        for (PersonMatch match : matches) {
            Optional<PreSurveyResponse> preOpt = preSurveyDao.findByPersonId(match.getPrePersonId());
            Optional<PostSurveyResponse> postOpt = postSurveyDao.findByPersonId(match.getPostPersonId());

            if (preOpt.isPresent() && postOpt.isPresent()) {
                PreSurveyResponse pre = preOpt.get();
                PostSurveyResponse post = postOpt.get();

                BigDecimal preSpeaking = pre.getAvgSpeakingConfidence();
                BigDecimal postSpeaking = post.getAvgSpeakingAbility();

                BigDecimal change = null;
                if (preSpeaking != null && postSpeaking != null) {
                    change = postSpeaking.subtract(preSpeaking);
                }

                Person prePerson = personDao.findById(match.getPrePersonId()).orElse(null);

                results.add(new MatchedPairAnalysis(
                        match.getCohort(),
                        prePerson != null ? prePerson.getName() : "Unknown",
                        preSpeaking,
                        pre.getAvgUnderstandingConfidence(),
                        postSpeaking,
                        change,
                        match.getMatchType().name()
                ));
            }
        }

        return results;
    }

    private BigDecimal calculateAveragePreSpeaking(List<PreSurveyResponse> responses) {
        return responses.stream()
                .map(PreSurveyResponse::getAvgSpeakingConfidence)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(1, responses.stream()
                        .filter(r -> r.getAvgSpeakingConfidence() != null).count())), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAveragePreUnderstanding(List<PreSurveyResponse> responses) {
        return responses.stream()
                .map(PreSurveyResponse::getAvgUnderstandingConfidence)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(1, responses.stream()
                        .filter(r -> r.getAvgUnderstandingConfidence() != null).count())), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAveragePostSpeaking(List<PostSurveyResponse> responses) {
        return responses.stream()
                .map(PostSurveyResponse::getAvgSpeakingAbility)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(1, responses.stream()
                        .filter(r -> r.getAvgSpeakingAbility() != null).count())), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAverageConfidenceChange(List<MatchedPairAnalysis> analyses) {
        List<BigDecimal> changes = analyses.stream()
                .map(MatchedPairAnalysis::confidenceChange)
                .filter(v -> v != null)
                .toList();

        if (changes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return changes.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(changes.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Analysis result container.
     */
    public record AnalysisResult(
            int preCount,
            int postCount,
            int matchedCount,
            BigDecimal avgPreSpeaking,
            BigDecimal avgPreUnderstanding,
            BigDecimal avgPostSpeaking,
            BigDecimal avgConfidenceChange,
            List<MatchedPairAnalysis> matchedPairAnalyses,
            List<String> cohorts
    ) {
    }

    /**
     * Analysis of a single matched pair.
     */
    public record MatchedPairAnalysis(
            String cohort,
            String name,
            BigDecimal preSpeakingConfidence,
            BigDecimal preUnderstandingConfidence,
            BigDecimal postSpeakingAbility,
            BigDecimal confidenceChange,
            String matchType
    ) {
    }
}
