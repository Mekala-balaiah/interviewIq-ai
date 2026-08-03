-- ============================================================
-- InterviewIQ AI - Flyway Migration V4
-- V4__create_jobs_and_application_tables.sql
-- Jobs, job skills, applications, resume analyses
-- ============================================================

-- ============================================================
-- TABLE: jobs
-- Job postings created by recruiters
-- ============================================================
CREATE TABLE jobs (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id              UUID NOT NULL,
    recruiter_id            UUID NOT NULL,
    title                   VARCHAR(255) NOT NULL,
    slug                    VARCHAR(300) NOT NULL,
    description             TEXT NOT NULL,
    requirements            TEXT,
    employment_type         employment_type NOT NULL DEFAULT 'FULL_TIME',
    work_mode               work_mode NOT NULL DEFAULT 'HYBRID',
    experience_level        experience_level NOT NULL DEFAULT 'MID',
    min_experience_years    INT NOT NULL DEFAULT 0,
    max_experience_years    INT,
    location                VARCHAR(255),
    salary_min              DECIMAL(12, 2),
    salary_max              DECIMAL(12, 2),
    salary_currency         VARCHAR(10) DEFAULT 'USD',
    status                  job_status NOT NULL DEFAULT 'DRAFT',
    publish_date            TIMESTAMP WITH TIME ZONE,
    close_date              TIMESTAMP WITH TIME ZONE,
    application_count       INT NOT NULL DEFAULT 0,
    view_count              INT NOT NULL DEFAULT 0,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by              UUID REFERENCES users(id),
    deleted_at              TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_jobs_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    CONSTRAINT fk_jobs_recruiter FOREIGN KEY (recruiter_id) REFERENCES recruiter_profiles(id),
    CONSTRAINT uq_jobs_slug UNIQUE (slug),
    CONSTRAINT chk_jobs_salary CHECK (salary_max >= salary_min OR salary_max IS NULL),
    CONSTRAINT chk_jobs_experience CHECK (max_experience_years >= min_experience_years OR max_experience_years IS NULL)
);

COMMENT ON TABLE jobs IS 'Job postings. Soft-deleted when archived. Status controls visibility';

CREATE INDEX idx_jobs_company_id ON jobs(company_id);
CREATE INDEX idx_jobs_recruiter_id ON jobs(recruiter_id);
CREATE INDEX idx_jobs_status ON jobs(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_jobs_status_publish ON jobs(status, publish_date DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_jobs_experience_level ON jobs(experience_level, status);
CREATE INDEX idx_jobs_work_mode ON jobs(work_mode, status);
CREATE INDEX idx_jobs_title_trgm ON jobs USING GIN (title gin_trgm_ops);
CREATE INDEX idx_jobs_description_fts ON jobs USING GIN (to_tsvector('english', description));

CREATE TRIGGER trg_jobs_updated_at
    BEFORE UPDATE ON jobs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- TABLE: job_skills
-- Many-to-many: jobs ↔ required skills
-- ============================================================
CREATE TABLE job_skills (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id              UUID NOT NULL,
    skill_id            UUID NOT NULL,
    is_required         BOOLEAN NOT NULL DEFAULT TRUE,
    proficiency_level   skill_proficiency DEFAULT 'INTERMEDIATE',

    CONSTRAINT fk_job_skills_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    CONSTRAINT uq_job_skill UNIQUE (job_id, skill_id)
);

CREATE INDEX idx_job_skills_job ON job_skills(job_id);
CREATE INDEX idx_job_skills_skill ON job_skills(skill_id);
CREATE INDEX idx_job_skills_required ON job_skills(job_id, is_required);

-- ============================================================
-- TABLE: applications
-- Candidate applications to job postings
-- ============================================================
CREATE TABLE applications (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id              UUID NOT NULL,
    candidate_id        UUID NOT NULL,
    resume_id           UUID,
    status              application_status NOT NULL DEFAULT 'APPLIED',
    ats_score           INT,
    ai_rank             INT,
    cover_letter        TEXT,
    recruiter_notes     TEXT,
    rejection_reason    TEXT,
    applied_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    status_updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE RESTRICT,
    CONSTRAINT fk_applications_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_resume FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE SET NULL,
    CONSTRAINT uq_application UNIQUE (job_id, candidate_id),
    CONSTRAINT chk_ats_score CHECK (ats_score BETWEEN 0 AND 100 OR ats_score IS NULL)
);

COMMENT ON TABLE applications IS 'One row per candidate per job. Enforces unique application constraint';

CREATE INDEX idx_applications_job_id ON applications(job_id);
CREATE INDEX idx_applications_candidate_id ON applications(candidate_id);
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_job_status ON applications(job_id, status);
CREATE INDEX idx_applications_ats_score ON applications(job_id, ats_score DESC NULLS LAST);
CREATE INDEX idx_applications_applied_at ON applications(applied_at DESC);

CREATE TRIGGER trg_applications_updated_at
    BEFORE UPDATE ON applications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- TABLE: resume_analyses
-- AI-generated ATS scores and resume parsing results
-- ============================================================
CREATE TABLE resume_analyses (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    resume_id               UUID NOT NULL,
    job_id                  UUID,
    ats_score               INT NOT NULL DEFAULT 0,
    keyword_match_score     INT NOT NULL DEFAULT 0,
    semantic_match_score    INT NOT NULL DEFAULT 0,
    format_score            INT NOT NULL DEFAULT 0,
    experience_score        INT NOT NULL DEFAULT 0,
    extracted_skills        JSONB,
    extracted_education     JSONB,
    extracted_experience    JSONB,
    missing_skills          JSONB,
    ai_summary              TEXT,
    improvement_suggestions TEXT,
    model_version           VARCHAR(50),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_resume_analyses_resume FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    CONSTRAINT fk_resume_analyses_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE SET NULL,
    CONSTRAINT chk_ats_score CHECK (ats_score BETWEEN 0 AND 100),
    CONSTRAINT chk_keyword_score CHECK (keyword_match_score BETWEEN 0 AND 100),
    CONSTRAINT chk_semantic_score CHECK (semantic_match_score BETWEEN 0 AND 100)
);

COMMENT ON TABLE resume_analyses IS 'AI parsing and ATS scoring results. job_id NULL means generic analysis';

CREATE INDEX idx_resume_analyses_resume ON resume_analyses(resume_id);
CREATE INDEX idx_resume_analyses_job ON resume_analyses(job_id);
CREATE INDEX idx_resume_analyses_resume_job ON resume_analyses(resume_id, job_id);
CREATE INDEX idx_resume_analyses_ats_score ON resume_analyses(ats_score DESC);
CREATE INDEX idx_resume_analyses_skills ON resume_analyses USING GIN (extracted_skills);
