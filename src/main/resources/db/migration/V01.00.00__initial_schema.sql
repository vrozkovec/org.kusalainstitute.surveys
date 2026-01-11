-- Kusala Institute Survey Analysis - Initial Schema
-- Database: org.kusalainstitute.surveys

-- Person (unique respondent identified by cohort + survey type)
create table person (
    id bigint not null auto_increment,
    cohort varchar(50) not null comment 'YTI, YTC, etc. from Column B',
    email varchar(255),
    name varchar(512),
    normalized_email varchar(255),
    requires_manual_match boolean default false comment 'true if cohort was ?',
    survey_type enum('PRE', 'POST') not null,
    created_at timestamp default current_timestamp,
    primary key (id),
    index idx_person_cohort (cohort),
    index idx_person_email (normalized_email),
    index idx_person_survey_type (survey_type)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- Pre-survey response
create table pre_survey_response (
    id bigint not null auto_increment,
    person_id bigint not null,
    timestamp datetime,
    source_file varchar(255),
    row_num int,

    -- Demographics (Q1-Q6)
    how_found_kusala varchar(512),
    study_with_teacher_duration varchar(255),
    study_on_own_duration varchar(255),
    children_ages text,
    most_difficult_thing_original text,
    most_difficult_thing_translated text,
    why_improve_english_original text,
    why_improve_english_translated text,

    -- Speaking confidence Q7 (11 situations) - stored as 1-4 scale
    speak_directions int comment '1=Not at all, 2=Somewhat, 3=Confident, 4=Very confident, 5=Extremely confident',
    speak_healthcare int,
    speak_authorities int,
    speak_job_interview int,
    speak_informal int,
    speak_children_education int,
    speak_landlord int,
    speak_social_events int,
    speak_local_services int,
    speak_support_orgs int,
    speak_shopping int,

    -- Q8 Free text
    other_situations_original text,
    other_situations_translated text,

    -- Understanding confidence Q9 (11 situations)
    understand_directions int,
    understand_healthcare int,
    understand_authorities int,
    understand_job_interview int,
    understand_informal int,
    understand_children_education int,
    understand_landlord int,
    understand_social_events int,
    understand_local_services int,
    understand_support_orgs int,
    understand_shopping int,

    -- Q10 Free text
    difficult_part_original text,
    difficult_part_translated text,

    -- Q11 Free text
    describe_situations_original text,
    describe_situations_translated text,

    -- Calculated averages
    avg_speaking_confidence decimal(5,2),
    avg_understanding_confidence decimal(5,2),

    primary key (id),
    constraint fk_pre_survey_person foreign key (person_id) references person(id),
    index idx_pre_person (person_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- Post-survey response
create table post_survey_response (
    id bigint not null auto_increment,
    person_id bigint not null,
    timestamp datetime,
    source_file varchar(255),
    row_num int,

    -- App usage Q1-Q4
    app_usage_duration varchar(255),
    app_time_per_session varchar(255),
    app_frequency varchar(255),
    progress_assessment varchar(255),

    -- Q5 Free text
    what_helped_most_original text,
    what_helped_most_translated text,

    -- Speaking ability Q6 (11 situations) - post-app
    speak_directions int,
    speak_healthcare int,
    speak_authorities int,
    speak_job_interview int,
    speak_informal int,
    speak_children_education int,
    speak_landlord int,
    speak_social_events int,
    speak_local_services int,
    speak_support_orgs int,
    speak_shopping int,

    -- Q7 Difficulty expressing (11 situations) - 1-5 scale
    difficulty_directions int comment '1-5 difficulty scale',
    difficulty_healthcare int,
    difficulty_authorities int,
    difficulty_job_interview int,
    difficulty_informal int,
    difficulty_children_education int,
    difficulty_landlord int,
    difficulty_social_events int,
    difficulty_local_services int,
    difficulty_support_orgs int,
    difficulty_shopping int,

    -- Q8 Free text
    most_difficult_overall_original text,
    most_difficult_overall_translated text,

    -- Q9 Free text
    most_difficult_for_job_original text,
    most_difficult_for_job_translated text,

    -- Q10 Free text
    emotional_difficulties_original text,
    emotional_difficulties_translated text,

    -- Q11 Free text
    avoided_situations_original text,
    avoided_situations_translated text,

    -- Q12
    has_enough_support varchar(255),

    -- Q13 Free text
    desired_resources_original text,
    desired_resources_translated text,

    -- Q14-16 Interview questions
    willing_to_interview varchar(50),
    interview_decline_reason_original text,
    interview_decline_reason_translated text,
    preferred_interview_type varchar(255),
    contact_info text,

    -- Q17 Free text
    additional_comments_original text,
    additional_comments_translated text,

    -- Calculated averages
    avg_speaking_ability decimal(5,2),
    avg_difficulty_expressing decimal(5,2),

    primary key (id),
    constraint fk_post_survey_person foreign key (person_id) references person(id),
    index idx_post_person (person_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- Person matching (pre to post within same cohort)
create table person_match (
    id bigint not null auto_increment,
    cohort varchar(50) not null comment 'Matching only within same cohort',
    pre_person_id bigint not null,
    post_person_id bigint not null,
    match_type enum('AUTO_EMAIL', 'AUTO_NAME', 'MANUAL') not null,
    confidence decimal(3,2) comment '0.00-1.00 for fuzzy matches',
    matched_at timestamp default current_timestamp,
    matched_by varchar(255) comment 'SYSTEM or username for manual',
    notes text,

    primary key (id),
    constraint fk_match_pre_person foreign key (pre_person_id) references person(id),
    constraint fk_match_post_person foreign key (post_person_id) references person(id),
    unique key uk_match (pre_person_id, post_person_id),
    index idx_match_cohort (cohort)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;

-- Translation cache to avoid re-translating
create table translation_cache (
    id bigint not null auto_increment,
    source_text_hash varchar(64) not null comment 'SHA-256 hash of source text',
    source_text text not null,
    translated_text text not null,
    source_language varchar(10) default 'FR',
    target_language varchar(10) default 'EN',
    created_at timestamp default current_timestamp,

    primary key (id),
    unique key uk_translation_hash (source_text_hash),
    index idx_translation_languages (source_language, target_language)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci;