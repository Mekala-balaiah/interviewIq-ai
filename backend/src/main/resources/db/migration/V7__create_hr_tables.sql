-- ============================================================
-- InterviewIQ AI - Flyway Migration V7
-- V7__create_hr_tables.sql
-- HR Manager profiles and job approvals
-- ============================================================

-- ============================================================
-- TABLE: hr_profiles
-- Extended profile for HR_MANAGER role users
-- ============================================================
CREATE TABLE hr_profiles (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL,
    company_id          UUID,
    title               VARCHAR(255),
    department          VARCHAR(100),
    linkedin_url        VARCHAR(500),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_hr_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_hr_profiles_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL,
    CONSTRAINT uq_hr_profiles_user UNIQUE (user_id)
);

CREATE INDEX idx_hr_profiles_user_id ON hr_profiles(user_id);
CREATE INDEX idx_hr_profiles_company_id ON hr_profiles(company_id);

CREATE TRIGGER trg_hr_profiles_updated_at
    BEFORE UPDATE ON hr_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


-- ============================================================
-- TYPE: job_approval_status
-- ============================================================
CREATE TYPE job_approval_status AS ENUM (
    'APPROVED',
    'REJECTED'
);


-- ============================================================
-- TABLE: job_approvals
-- Audit trail of job approvals and rejections
-- ============================================================
CREATE TABLE job_approvals (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id          UUID NOT NULL,
    hr_id           UUID NOT NULL,
    status          job_approval_status NOT NULL,
    comments        TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_job_approvals_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_approvals_hr FOREIGN KEY (hr_id) REFERENCES hr_profiles(id)
);

CREATE INDEX idx_job_approvals_job_id ON job_approvals(job_id);
CREATE INDEX idx_job_approvals_hr_id ON job_approvals(hr_id);
