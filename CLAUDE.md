  Project Created

  Location: /speedy/dev/name.berries/_projects/org.kusalainstitute.surveys/

  Files Created (27 files)

  Configuration:
  - pom.xml - Maven project with dependencies (POI, MySQL, Flyway, picocli, etc.)
  - application.properties - Database and API configuration
  - flyway.properties - Migration settings
  - logback.xml - Logging configuration

  Database:
  - V01.00.00__initial_schema.sql - Tables: person, pre_survey_response, post_survey_response, person_match, translation_cache

  Enums:
  - ConfidenceLevel.java - 1-4 scale with French/English labels
  - MatchType.java - AUTO_EMAIL, AUTO_NAME, MANUAL
  - SurveyType.java - PRE, POST

  Entities:
  - Person.java, PreSurveyResponse.java, PostSurveyResponse.java, PersonMatch.java, TranslationCache.java

  DAOs:
  - BaseDao.java, PersonDao.java, PreSurveyDao.java, PostSurveyDao.java, MatchDao.java, TranslationCacheDao.java

  Parsers:
  - ExcelParser.java, PreSurveyParser.java, PostSurveyParser.java

  Services:
  - ImportService.java, MatchingService.java, AnalysisService.java

  CLI:
  - App.java - picocli-based CLI with init, import, match, analyze commands

  To Get Started

  1. Create the database:
  CREATE DATABASE `org.kusalainstitute.surveys` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  2. Configure application.properties with your MySQL credentials
  3. Build:
  cd /speedy/dev/name.berries/_projects/org.kusalainstitute.surveys
  mvn clean package -DskipTests
  4. Run:
  java -jar target/org.kusalainstitute.surveys-*.jar init
  java -jar target/org.kusalainstitute.surveys-*.jar import --pre data/12-26\ pre-survey\ data.xlsx --post data/12-26\ post-survey\ data.xlsx
  java -jar target/org.kusalainstitute.surveys-*.jar match --auto
  java -jar target/org.kusalainstitute.surveys-*.jar analyze

  Still Needed (for future iterations)

  - TranslationService (DeepL API integration)
  - ReportService (Claude API for narrative reports)
  - HTML report generation
  - Manual matching CLI interface