# Plan: Create Mapper Interface and Implementations

## Overview
Create a dedicated mapper layer to handle string-to-enum conversions for survey responses. This provides a cleaner separation of concerns and makes it easier to add new language mappings.

## Current State
- 9 enum types created with embedded `fromText()` methods
- Parsers call enum's static `fromText()` methods directly
- JDBI configurator handles enum storage/retrieval

## What Needs to Be Done

### 1. Create Mapper Interface
**Path**: `src/main/java/org/kusalainstitute/surveys/mapper/ChoiceMapper.java`

```java
public interface ChoiceMapper<E extends Enum<E>> {
    /**
     * Maps a single text value to an enum.
     * @param value the text value from survey response
     * @return the corresponding enum, or null if not found
     */
    E map(String value);

    /**
     * Maps a multi-select text value to a set of enums.
     * @param value comma/semicolon separated values
     * @return set of corresponding enums (empty if none found)
     */
    Set<E> mapMultiple(String value);
}
```

### 2. Create Mapper Implementations
**Path**: `src/main/java/org/kusalainstitute/surveys/mapper/`

Each mapper should:
- Use a static `Map<String, EnumType>` for mappings
- Support both French and English text
- Handle case-insensitive matching
- Handle whitespace trimming
- Support letter/number prefix patterns (e.g., "A= Moins de 3 mois")

#### Files to Create:
1. `HowFoundKusalaMapper.java` - PRE Q1
2. `StudyDurationMapper.java` - PRE Q2/Q3
3. `ChildrenAgeGroupMapper.java` - PRE Q4 (multi-select)
4. `AppTimePerSessionMapper.java` - POST Q1/Q2
5. `AppFrequencyMapper.java` - POST Q3
6. `ProgressAssessmentMapper.java` - POST Q4
7. `YesNoMapper.java` - POST Q14
8. `InterviewTypePreferenceMapper.java` - POST Q16

#### Mapper Implementation Template:
```java
public class StudyDurationMapper implements ChoiceMapper<StudyDuration> {
    private static final Map<String, StudyDuration> MAPPINGS = new HashMap<>();

    static {
        // French mappings
        MAPPINGS.put("moins de 3 mois", StudyDuration.A);
        MAPPINGS.put("entre 4 mois et 1 an", StudyDuration.B);
        MAPPINGS.put("1-2 ans", StudyDuration.C);
        MAPPINGS.put("2-5 ans", StudyDuration.D);
        MAPPINGS.put("5-10 ans", StudyDuration.E);
        MAPPINGS.put("10 ans ou plus", StudyDuration.F);

        // English mappings
        MAPPINGS.put("less than 3 months", StudyDuration.A);
        MAPPINGS.put("4 months - 1 year", StudyDuration.B);
        MAPPINGS.put("1-2 years", StudyDuration.C);
        MAPPINGS.put("2-5 years", StudyDuration.D);
        MAPPINGS.put("5-10 years", StudyDuration.E);
        MAPPINGS.put("10+ years", StudyDuration.F);
    }

    @Override
    public StudyDuration map(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.toLowerCase().trim();

        // Try direct mapping first
        StudyDuration result = MAPPINGS.get(normalized);
        if (result != null) {
            return result;
        }

        // Try letter prefix (e.g., "A= Moins de 3 mois")
        if (normalized.length() >= 1 && Character.isLetter(normalized.charAt(0))) {
            char prefix = Character.toUpperCase(normalized.charAt(0));
            if (prefix >= 'A' && prefix <= 'F') {
                return StudyDuration.fromCode(String.valueOf(prefix));
            }
        }

        // Try partial matching
        for (Map.Entry<String, StudyDuration> entry : MAPPINGS.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public Set<StudyDuration> mapMultiple(String value) {
        // Not applicable for StudyDuration (single-select)
        StudyDuration single = map(value);
        return single != null ? EnumSet.of(single) : EnumSet.noneOf(StudyDuration.class);
    }
}
```

### 3. Update Parsers to Use Mappers
**Files to modify:**
- `PreSurveyParser.java`
- `PostSurveyParser.java`

Change from:
```java
response.setStudyWithTeacherDuration(StudyDuration.fromText(getStringValue(row, COL_STUDY_TEACHER)));
```

To:
```java
private final StudyDurationMapper studyDurationMapper = new StudyDurationMapper();
// ...
response.setStudyWithTeacherDuration(studyDurationMapper.map(getStringValue(row, COL_STUDY_TEACHER)));
```

### 4. Optional: Remove fromText() from Enums
After mappers are in place, the `fromText()` methods in enums become redundant. They can either:
- Be removed (cleaner)
- Be kept as convenience methods that delegate to mappers
- Be kept for backward compatibility

## Language Mappings Reference

### HowFoundKusala (PRE Q1)
| Code | French | English |
|------|--------|---------|
| A | LinkedIn | LinkedIn |
| B | Facebook | Facebook |
| C | Un ami local | A local friend |
| D | Un ami qui vit dans une autre ville ou un autre pays | A friend in another city/country |

### StudyDuration (PRE Q2/Q3)
| Code | French | English |
|------|--------|---------|
| A | Moins de 3 mois | Less than 3 months |
| B | Entre 4 mois et 1 an | 4 months - 1 year |
| C | 1-2 ans | 1-2 years |
| D | 2-5 ans | 2-5 years |
| E | 5-10 ans | 5-10 years |
| F | 10 ans ou plus | 10+ years |

### ChildrenAgeGroup (PRE Q4) - Multi-select
| Code | French | English |
|------|--------|---------|
| A | 5 ans et moins | 5 and under |
| B | 6-12 ans | 6-12 |
| C | 13-18 ans | 13-18 |
| D | 19 ans et plus | 19+ |
| E | Pas d'enfants | No children |

### AppTimePerSession (POST Q1/Q2)
| Code | French | English |
|------|--------|---------|
| A | Moins de 15 minutes | Less than 15 minutes |
| B | 15-30 minutes | 15-30 minutes |
| C | Entre 30 minutes et une heure | 30 min - 1 hour |
| D | 1-2 heures | 1-2 hours |
| E | Plus de deux heures | More than 2 hours |

### AppFrequency (POST Q3)
| Code | French | English |
|------|--------|---------|
| 1 | Très fréquemment, plusieurs fois par jour | Very frequently, multiple times per day |
| 2 | Fréquemment, au moins une fois par jour | Frequently, at least once per day |
| 3 | Régulièrement, quelques fois par semaine | Regularly, few times per week |
| 4 | Occasionnellement, quelques fois par mois | Occasionally, few times per month |
| 5 | Rarement, presque jamais | Rarely, almost never |

### ProgressAssessment (POST Q4)
| Code | French | English |
|------|--------|---------|
| 1 | Aucun progrès | No progress |
| 2 | Peu de progrès | Little progress |
| 3 | Progrès modérés | Moderate progress |
| 4 | Progrès significatifs | Significant progress |
| 5 | Progrès énormes | Huge progress |

### YesNo (POST Q14)
| Code | French | English |
|------|--------|---------|
| YES | Oui | Yes |
| NO | Non | No |

### InterviewTypePreference (POST Q16)
| Code | French | English |
|------|--------|---------|
| A | Je n'ai pas de préférence | No preference |
| B | Appel vidéo utilisant WhatsApp, Zoom ou Google Meet | Video call (WhatsApp/Zoom/Meet) |
| C | Appel vocal utilisant WhatsApp avec mon numéro ci-dessous | Voice call (WhatsApp) |

## Implementation Order

1. Create `ChoiceMapper.java` interface
2. Create mapper implementations (8 files)
3. Update `PreSurveyParser.java` to use mappers
4. Update `PostSurveyParser.java` to use mappers
5. Test import with French survey data
6. (Optional) Remove redundant `fromText()` methods from enums

## Benefits of Mapper Layer

1. **Separation of Concerns**: Parsing logic separate from enum definitions
2. **Easier Language Support**: Add new mappings without modifying enums
3. **Testability**: Mappers can be unit tested independently
4. **Flexibility**: Different mapping strategies per enum if needed
5. **Maintainability**: All language strings in one place per enum
