# Kusala Institute Survey Analysis Project

This project analyzes survey data from the Kusala Institute, which provides English language education to refugees and immigrants. The system processes pre-survey and post-survey responses to measure student progress.

## Technology Stack

- **Framework**: Apache Wicket 10 with wicket-bootstrap
- **Build**: Maven, part of the Berries framework
- **Database**: MySQL with JDBI
- **Dependency Injection**: Google Guice

## Project Structure

```
src/main/java/org/kusalainstitute/surveys/
├── config/           # Guice module configuration
├── mapper/           # Excel value to enum mappers
├── parser/           # Excel file parsers (PreSurveyParser, PostSurveyParser)
├── pojo/             # Entity classes (Person, PreSurveyResponse, PostSurveyResponse)
│   └── enums/        # Enum types for survey answers
├── service/          # Business logic (ImportService, AnalysisService, TranslationService)
└── wicket/
    ├── app/          # SurveyApplication
    ├── model/        # Data models for UI (SituationAnalysisModel, StudentRow, etc.)
    ├── pages/        # Wicket pages (HomePage)
    └── panel/        # Wicket panels (SituationAnalysisPanel)
```

## Survey Structure

### Pre-Survey Questions
| Q# | Description | Type |
|----|-------------|------|
| Q1 | How did you find Kusala Institute | Enum |
| Q2 | Study with teacher duration | Enum |
| Q3 | Study on own duration | Enum |
| Q4 | Children ages | Multi-select Enum |
| Q5 | Most difficult thing using English | Free text |
| Q6 | Why improve English | Free text |
| Q7 | Speaking confidence (11 situations) | Scale 1-4 |
| Q8 | Other situations | Free text |
| Q9 | Understanding confidence (11 situations) | Scale 1-4 |
| Q10 | Most difficult part | Free text |
| Q11 | Describe difficult situations | Free text |

### Post-Survey Questions
| Q# | Description | Type |
|----|-------------|------|
| Q1 | App usage duration | Enum |
| Q2 | Time per session | Enum |
| Q3 | App frequency | Enum |
| Q4 | Progress assessment | Enum |
| Q5 | What helped most | Free text |
| Q6 | Speaking ability (11 situations) | Scale 1-4 |
| Q7 | Difficulty expressing (11 situations) | Scale 1-5 |
| Q8 | Most difficult overall | Free text |
| Q9 | Most difficult for job | Free text |
| Q10 | Emotional difficulties | Free text |
| Q11 | Avoided situations | Free text |
| Q12 | Has enough support | Free text |
| Q13 | Desired resources | Free text |
| Q14 | Willing to interview | Yes/No |
| Q15 | Interview decline reason | Free text |
| Q16 | Preferred interview type | Enum |
| Q17 | Additional comments | Free text |

### 11 Situations (in order)
1. **Directions** - Asking for or giving directions in the street
2. **Healthcare** - Talking to healthcare workers (doctor, nurse, pharmacist)
3. **Authorities** - Communicating with government agencies (police, immigration, public services)
4. **Job Interview** - Having a job interview in English
5. **Informal** - Informal conversations with friends, family, or colleagues
6. **Children Education** - Communicating with children's school staff (teachers, administration)
7. **Landlord** - Communicating with landlord or property management
8. **Social Events** - Participating in social events or community gatherings
9. **Local Services** - Communicating with local service providers (utilities, banks, shops)
10. **Support Orgs** - Communicating with support organizations (NGOs, charities, community groups)
11. **Shopping** - Shopping, reading labels, seeking product information

## Analysis Model

The `SituationAnalysisPanel` displays a comprehensive table with:

### Columns
1. **Speaking** (11 columns): Compares Pre Q7 (confidence) → Post Q6 (ability)
   - Shows pre/post bars and delta change
   - Question numbers: Q7→Q6.1 through Q7→Q6.11

2. **Understanding** (11 columns): Pre Q9 only (confidence)
   - Shows single value bar
   - Question numbers: Q9.1 through Q9.11

3. **Ease** (11 columns): Post Q7 inverted (difficulty → ease)
   - Values inverted: higher = easier to express
   - Question numbers: Q7.1 through Q7.11

4. **Text Answers** (14 columns): 5 pre-survey + 9 post-survey free text responses

### Key Classes
- `SituationAnalysisModel` - Main data model with all analysis data
- `StudentRow` - Record containing one student's data across all columns
- `SituationData` - Pre/post comparison data with delta calculation
- `SingleValueData` - Single value data for Understanding/Ease columns
- `TextAnswerData` - Text answer data with pre/post styling
- `HeaderInfo` - Header display info with label, question number, and tooltip
- `OpenEndedQuestionData` - Record for open-ended question with anonymous answers
- `OpenEndedQuestionsPanel` - Panel displaying qualitative summary grouped by question

## Data Files

Data files are located in the `data/` directory:
- `pre/` - Pre-survey Excel files
- `post/` - Post-survey Excel files
- `translations.properties` - Cache of DeepL translations (hash → translated text)

## Translation Service

Free text answers are translated from French to English using the DeepL API. Translations are cached in `translations.properties` using SHA256 hashes as keys.

## Free Text Field Pattern

All free-text survey questions follow a consistent naming pattern:
- `fieldOriginal` - Raw text from Excel (typically French)
- `fieldTranslated` - English translation via DeepL

Example fields in `PostSurveyResponse`:
- `whatHelpedMostOriginal` / `whatHelpedMostTranslated`
- `mostDifficultOverallOriginal` / `mostDifficultOverallTranslated`
- `hasEnoughSupportOriginal` / `hasEnoughSupportTranslated`

The `TranslationService.translateAll()` method populates all `*Translated` fields.

## Import Process

1. **Excel Parsing**: `PostSurveyParser.parseRow()` extracts data from Excel columns
2. **Translation**: `TranslationService.translateAll()` translates French text to English
3. **Database Storage**: `PostSurveyDao.insert()` persists both original and translated text
4. **Caching**: Translations are cached in `data/translations.properties` (SHA256 hash → translation)

## Running the Application

The application is started via `Start.java` (main class) which launches an embedded Jetty server.

## Key Wicket Patterns

- Uses `ListView` for dynamic table rows/columns
- `TooltipBehavior` from wicket-bootstrap for header tooltips
- `AttributeModifier` for dynamic CSS classes
- `WebMarkupContainer` for styled progress bars
