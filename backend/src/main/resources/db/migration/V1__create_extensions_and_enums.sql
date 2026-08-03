-- ============================================================
-- InterviewIQ AI - Flyway Migration V1
-- V1__create_extensions_and_schema.sql
-- Creates PostgreSQL extensions and base schema setup
-- ============================================================

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable pg_trgm for full-text search similarity
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Enable btree_gin for composite indexes with JSONB
CREATE EXTENSION IF NOT EXISTS btree_gin;

-- ============================================================
-- ENUM TYPES
-- ============================================================

CREATE TYPE user_role AS ENUM (
    'CANDIDATE',
    'RECRUITER',
    'HR_MANAGER',
    'COMPANY_ADMIN',
    'SUPER_ADMIN'
);

CREATE TYPE user_status AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'SUSPENDED',
    'PENDING_VERIFICATION'
);

CREATE TYPE application_status AS ENUM (
    'APPLIED',
    'SCREENING',
    'PHONE_SCREEN',
    'TECHNICAL_ROUND',
    'HR_ROUND',
    'FINAL_ROUND',
    'OFFER_EXTENDED',
    'OFFER_ACCEPTED',
    'OFFER_REJECTED',
    'REJECTED',
    'WITHDRAWN'
);

CREATE TYPE interview_type AS ENUM (
    'AI_INTERVIEW',
    'TECHNICAL',
    'HR',
    'CULTURAL_FIT',
    'SYSTEM_DESIGN',
    'BEHAVIORAL',
    'CODING'
);

CREATE TYPE interview_status AS ENUM (
    'SCHEDULED',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED',
    'NO_SHOW',
    'RESCHEDULED'
);

CREATE TYPE job_status AS ENUM (
    'DRAFT',
    'PENDING_APPROVAL',
    'ACTIVE',
    'PAUSED',
    'CLOSED',
    'ARCHIVED'
);

CREATE TYPE employment_type AS ENUM (
    'FULL_TIME',
    'PART_TIME',
    'CONTRACT',
    'INTERNSHIP',
    'FREELANCE'
);

CREATE TYPE work_mode AS ENUM (
    'REMOTE',
    'HYBRID',
    'ON_SITE'
);

CREATE TYPE experience_level AS ENUM (
    'ENTRY',
    'JUNIOR',
    'MID',
    'SENIOR',
    'LEAD',
    'PRINCIPAL',
    'DIRECTOR'
);

CREATE TYPE skill_proficiency AS ENUM (
    'BEGINNER',
    'INTERMEDIATE',
    'ADVANCED',
    'EXPERT'
);

CREATE TYPE assessment_status AS ENUM (
    'NOT_STARTED',
    'IN_PROGRESS',
    'SUBMITTED',
    'EVALUATED',
    'EXPIRED'
);

CREATE TYPE notification_channel AS ENUM (
    'IN_APP',
    'EMAIL',
    'SMS',
    'PUSH'
);

CREATE TYPE notification_status AS ENUM (
    'PENDING',
    'SENT',
    'DELIVERED',
    'FAILED'
);

CREATE TYPE oauth_provider AS ENUM (
    'GOOGLE',
    'GITHUB',
    'LINKEDIN'
);

CREATE TYPE otp_purpose AS ENUM (
    'EMAIL_VERIFICATION',
    'PASSWORD_RESET',
    'MFA'
);

CREATE TYPE calendar_event_status AS ENUM (
    'TENTATIVE',
    'CONFIRMED',
    'CANCELLED'
);

CREATE TYPE resume_parse_status AS ENUM (
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'FAILED'
);

CREATE TYPE verdict AS ENUM (
    'ACCEPTED',
    'WRONG_ANSWER',
    'TIME_LIMIT_EXCEEDED',
    'MEMORY_LIMIT_EXCEEDED',
    'RUNTIME_ERROR',
    'COMPILATION_ERROR',
    'PENDING'
);

COMMENT ON TYPE user_role IS 'Roles available in the InterviewIQ platform';
COMMENT ON TYPE application_status IS 'Hiring pipeline stages for job applications';
