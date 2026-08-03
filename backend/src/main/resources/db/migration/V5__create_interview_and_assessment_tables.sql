-- ============================================================
-- InterviewIQ AI - Flyway Migration V5
-- V5__create_interview_and_assessment_tables.sql
-- Interviews, interview questions/responses, assessments
-- ============================================================

-- ============================================================
-- TABLE: interviews
-- Interview sessions (AI-conducted or human-scheduled)
-- ============================================================
CREATE TABLE interviews (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id      UUID NOT NULL,
    candidate_id        UUID NOT NULL,
    recruiter_id        UUID,
    job_id              UUID NOT NULL,
    type                interview_type NOT NULL DEFAULT 'TECHNICAL',
    status              interview_status NOT NULL DEFAULT 'SCHEDULED',
    round               VARCHAR(50),
    overall_score       INT,
    ai_feedback         TEXT,
    ai_summary          TEXT,
    recruiter_notes     TEXT,
    ai_conducted        BOOLEAN NOT NULL DEFAULT FALSE,
    duration_minutes    INT,
    scheduled_at        TIMESTAMP WITH TIME ZONE,
    started_at          TIMESTAMP WITH TIME ZONE,
    completed_at        TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_interviews_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_interviews_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles(id),
    CONSTRAINT fk_interviews_recruiter FOREIGN KEY (recruiter_id) REFERENCES recruiter_profiles(id) ON DELETE SET NULL,
    CONSTRAINT fk_interviews_job FOREIGN KEY (job_id) REFERENCES jobs(id),
    CONSTRAINT chk_interview_score CHECK (overall_score BETWEEN 0 AND 100 OR overall_score IS NULL)
);

COMMENT ON TABLE interviews IS 'Interview sessions. ai_conducted=TRUE means AI-led interview with Spring AI';

CREATE INDEX idx_interviews_application ON interviews(application_id);
CREATE INDEX idx_interviews_candidate ON interviews(candidate_id);
CREATE INDEX idx_interviews_recruiter ON interviews(recruiter_id);
CREATE INDEX idx_interviews_job ON interviews(job_id);
CREATE INDEX idx_interviews_status ON interviews(status);
CREATE INDEX idx_interviews_scheduled_at ON interviews(scheduled_at);
CREATE INDEX idx_interviews_ai ON interviews(ai_conducted, status);

CREATE TRIGGER trg_interviews_updated_at
    BEFORE UPDATE ON interviews
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- TABLE: interview_questions
-- AI-generated or recruiter-created questions
-- ============================================================
CREATE TABLE interview_questions (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    interview_id        UUID NOT NULL,
    sequence_number     INT NOT NULL,
    question_type       VARCHAR(50) NOT NULL,
    difficulty          VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    question_text       TEXT NOT NULL,
    expected_answer     TEXT,
    topic               VARCHAR(100),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_interview_questions_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    CONSTRAINT uq_interview_question_seq UNIQUE (interview_id, sequence_number)
);

CREATE INDEX idx_interview_questions_interview ON interview_questions(interview_id);
CREATE INDEX idx_interview_questions_topic ON interview_questions(topic);

-- ============================================================
-- TABLE: interview_responses
-- Candidate answers to interview questions
-- ============================================================
CREATE TABLE interview_responses (
    id                          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    interview_id                UUID NOT NULL,
    question_id                 UUID NOT NULL,
    response_text               TEXT,
    ai_score                    INT,
    ai_feedback                 TEXT,
    response_duration_seconds   INT,
    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_interview_responses_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_interview_responses_question FOREIGN KEY (question_id) REFERENCES interview_questions(id) ON DELETE CASCADE,
    CONSTRAINT uq_interview_response UNIQUE (interview_id, question_id),
    CONSTRAINT chk_response_score CHECK (ai_score BETWEEN 0 AND 10 OR ai_score IS NULL)
);

CREATE INDEX idx_interview_responses_interview ON interview_responses(interview_id);
CREATE INDEX idx_interview_responses_question ON interview_responses(question_id);

-- ============================================================
-- TABLE: assessments
-- Coding assessment assignments per application
-- ============================================================
CREATE TABLE assessments (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id      UUID NOT NULL,
    job_id              UUID NOT NULL,
    candidate_id        UUID NOT NULL,
    title               VARCHAR(255) NOT NULL,
    difficulty          VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    duration_minutes    INT NOT NULL DEFAULT 60,
    total_marks         INT NOT NULL DEFAULT 100,
    status              assessment_status NOT NULL DEFAULT 'NOT_STARTED',
    score_obtained      INT,
    percentage          INT,
    plagiarism_flagged  BOOLEAN DEFAULT FALSE,
    plagiarism_score    DECIMAL(5, 2),
    deadline_at         TIMESTAMP WITH TIME ZONE,
    started_at          TIMESTAMP WITH TIME ZONE,
    submitted_at        TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_assessments_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_assessments_job FOREIGN KEY (job_id) REFERENCES jobs(id),
    CONSTRAINT fk_assessments_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles(id),
    CONSTRAINT chk_assessment_score CHECK (score_obtained <= total_marks OR score_obtained IS NULL),
    CONSTRAINT chk_percentage CHECK (percentage BETWEEN 0 AND 100 OR percentage IS NULL)
);

CREATE INDEX idx_assessments_application ON assessments(application_id);
CREATE INDEX idx_assessments_candidate ON assessments(candidate_id);
CREATE INDEX idx_assessments_status ON assessments(status);
CREATE INDEX idx_assessments_deadline ON assessments(deadline_at) WHERE status = 'NOT_STARTED';

CREATE TRIGGER trg_assessments_updated_at
    BEFORE UPDATE ON assessments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- TABLE: assessment_questions
-- Coding problems within assessments
-- ============================================================
CREATE TABLE assessment_questions (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    assessment_id       UUID NOT NULL,
    sequence_number     INT NOT NULL,
    question_type       VARCHAR(50) NOT NULL DEFAULT 'CODING',
    difficulty          VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    problem_statement   TEXT NOT NULL,
    constraints         TEXT,
    test_cases          JSONB NOT NULL DEFAULT '[]',
    starter_code        JSONB,
    marks               INT NOT NULL DEFAULT 10,
    topic               VARCHAR(100),
    time_limit_ms       INT DEFAULT 2000,
    memory_limit_kb     INT DEFAULT 256000,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_assessment_questions_assessment FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE,
    CONSTRAINT uq_assessment_question_seq UNIQUE (assessment_id, sequence_number)
);

CREATE INDEX idx_assessment_questions_assessment ON assessment_questions(assessment_id);
CREATE INDEX idx_assessment_questions_topic ON assessment_questions(topic);

-- ============================================================
-- TABLE: assessment_submissions
-- Code submissions per question per candidate
-- ============================================================
CREATE TABLE assessment_submissions (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    assessment_id       UUID NOT NULL,
    question_id         UUID NOT NULL,
    candidate_id        UUID NOT NULL,
    code                TEXT NOT NULL,
    language            VARCHAR(30) NOT NULL,
    verdict             verdict NOT NULL DEFAULT 'PENDING',
    test_cases_passed   INT DEFAULT 0,
    total_test_cases    INT DEFAULT 0,
    execution_time_ms   INT,
    memory_used_kb      INT,
    test_results        JSONB,
    submitted_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_submissions_assessment FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE,
    CONSTRAINT fk_submissions_question FOREIGN KEY (question_id) REFERENCES assessment_questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_submissions_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles(id)
);

CREATE INDEX idx_submissions_assessment ON assessment_submissions(assessment_id);
CREATE INDEX idx_submissions_question ON assessment_submissions(question_id);
CREATE INDEX idx_submissions_candidate ON assessment_submissions(candidate_id);
CREATE INDEX idx_submissions_verdict ON assessment_submissions(verdict);
