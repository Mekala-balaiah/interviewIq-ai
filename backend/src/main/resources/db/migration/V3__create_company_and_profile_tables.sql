-- ============================================================
-- InterviewIQ AI - Flyway Migration V3
-- V3__create_company_and_profile_tables.sql
-- Company, Candidate Profile, Recruiter Profile, Skills
-- ============================================================

-- ============================================================
-- TABLE: companies
-- ============================================================
CREATE TABLE companies (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(255) NOT NULL,
    logo_url        VARCHAR(500),
    website         VARCHAR(500),
    industry        VARCHAR(100),
    size_range      VARCHAR(50),
    description     TEXT,
    headquarters    VARCHAR(255),
    linkedin_url    VARCHAR(500),
    status          VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by      UUID REFERENCES users(id),
    deleted_at      TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uq_companies_slug UNIQUE (slug)
);

COMMENT ON TABLE companies IS 'Company entities that post jobs and employ recruiters';

CREATE INDEX idx_companies_slug ON companies(slug);
CREATE INDEX idx_companies_status ON companies(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_companies_name_trgm ON companies USING GIN (name gin_trgm_ops);

CREATE TRIGGER trg_companies_updated_at
    BEFORE UPDATE ON companies
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- TABLE: candidate_profiles
-- Extended profile for CANDIDATE role users
-- ============================================================
CREATE TABLE candidate_profiles (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id                 UUID NOT NULL,
    headline                VARCHAR(255),
    bio                     TEXT,
    location                VARCHAR(255),
    linkedin_url            VARCHAR(500),
    github_url              VARCHAR(500),
    portfolio_url           VARCHAR(500),
    years_of_experience     INT NOT NULL DEFAULT 0,
    current_title           VARCHAR(255),
    current_company         VARCHAR(255),
    employment_status       VARCHAR(50),
    notice_period           VARCHAR(50),
    expected_salary_min     DECIMAL(12, 2),
    expected_salary_max     DECIMAL(12, 2),
    salary_currency         VARCHAR(10) DEFAULT 'USD',
    availability            VARCHAR(50),
    open_to_remote          BOOLEAN DEFAULT TRUE,
    profile_complete        BOOLEAN DEFAULT FALSE,
    profile_completion_pct  INT DEFAULT 0,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_candidate_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_candidate_profiles_user UNIQUE (user_id),
    CONSTRAINT chk_salary_range CHECK (expected_salary_max >= expected_salary_min OR expected_salary_max IS NULL),
    CONSTRAINT chk_experience CHECK (years_of_experience >= 0),
    CONSTRAINT chk_completion_pct CHECK (profile_completion_pct BETWEEN 0 AND 100)
);

COMMENT ON TABLE candidate_profiles IS 'Extended profile data for candidates — supplements the users table';

CREATE INDEX idx_candidate_profiles_user_id ON candidate_profiles(user_id);
CREATE INDEX idx_candidate_profiles_location ON candidate_profiles(location);
CREATE INDEX idx_candidate_profiles_experience ON candidate_profiles(years_of_experience);
CREATE INDEX idx_candidate_profiles_remote ON candidate_profiles(open_to_remote);

CREATE TRIGGER trg_candidate_profiles_updated_at
    BEFORE UPDATE ON candidate_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- TABLE: recruiter_profiles
-- Extended profile for RECRUITER role users
-- ============================================================
CREATE TABLE recruiter_profiles (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL,
    company_id          UUID,
    title               VARCHAR(255),
    department          VARCHAR(100),
    specialization      VARCHAR(255),
    bio                 TEXT,
    linkedin_url        VARCHAR(500),
    active_jobs_count   INT DEFAULT 0,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_recruiter_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_recruiter_profiles_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL,
    CONSTRAINT uq_recruiter_profiles_user UNIQUE (user_id)
);

CREATE INDEX idx_recruiter_profiles_user_id ON recruiter_profiles(user_id);
CREATE INDEX idx_recruiter_profiles_company_id ON recruiter_profiles(company_id);

CREATE TRIGGER trg_recruiter_profiles_updated_at
    BEFORE UPDATE ON recruiter_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- TABLE: skills
-- Normalized skills dictionary
-- ============================================================
CREATE TABLE skills (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name             VARCHAR(100) NOT NULL,
    category         VARCHAR(100),
    normalized_name  VARCHAR(100) NOT NULL,
    usage_count      INT NOT NULL DEFAULT 0,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_skills_normalized_name UNIQUE (normalized_name)
);

COMMENT ON TABLE skills IS 'Normalized skills dictionary shared across candidates, jobs, and AI matching';

CREATE INDEX idx_skills_name ON skills(name);
CREATE INDEX idx_skills_category ON skills(category);
CREATE INDEX idx_skills_usage ON skills(usage_count DESC);
CREATE INDEX idx_skills_name_trgm ON skills USING GIN (name gin_trgm_ops);

-- ============================================================
-- TABLE: candidate_skills
-- Many-to-many: candidates ↔ skills
-- ============================================================
CREATE TABLE candidate_skills (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id        UUID NOT NULL,
    skill_id            UUID NOT NULL,
    proficiency_level   skill_proficiency NOT NULL DEFAULT 'INTERMEDIATE',
    years_experience    INT DEFAULT 0,
    is_primary          BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_candidate_skills_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_candidate_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    CONSTRAINT uq_candidate_skill UNIQUE (candidate_id, skill_id)
);

CREATE INDEX idx_candidate_skills_candidate ON candidate_skills(candidate_id);
CREATE INDEX idx_candidate_skills_skill ON candidate_skills(skill_id);
CREATE INDEX idx_candidate_skills_primary ON candidate_skills(candidate_id, is_primary);

-- ============================================================
-- TABLE: resumes
-- Uploaded resume file metadata
-- ============================================================
CREATE TABLE resumes (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id    UUID NOT NULL,
    file_name       VARCHAR(500) NOT NULL,
    file_url        VARCHAR(1000) NOT NULL,
    file_type       VARCHAR(20) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    is_primary      BOOLEAN NOT NULL DEFAULT FALSE,
    parse_status    resume_parse_status NOT NULL DEFAULT 'PENDING',
    parsed_at       TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_resumes_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles(id) ON DELETE CASCADE,
    CONSTRAINT chk_file_size CHECK (file_size_bytes > 0 AND file_size_bytes <= 10485760) -- 10MB max
);

COMMENT ON TABLE resumes IS 'Resume file metadata. Actual files stored in object storage (S3/local)';

CREATE INDEX idx_resumes_candidate ON resumes(candidate_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_resumes_parse_status ON resumes(parse_status) WHERE deleted_at IS NULL;
CREATE INDEX idx_resumes_primary ON resumes(candidate_id, is_primary);

CREATE TRIGGER trg_resumes_updated_at
    BEFORE UPDATE ON resumes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
