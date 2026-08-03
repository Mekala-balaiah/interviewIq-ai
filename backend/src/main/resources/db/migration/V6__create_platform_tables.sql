-- ============================================================
-- InterviewIQ AI - Flyway Migration V6
-- V6__create_platform_tables.sql
-- Notifications, Calendar, Audit Logs, Learning Roadmaps
-- ============================================================

-- ============================================================
-- TABLE: notifications
-- All notification records (in-app + email + push)
-- ============================================================
CREATE TABLE notifications (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL,
    type        VARCHAR(100) NOT NULL,
    channel     notification_channel NOT NULL DEFAULT 'IN_APP',
    title       VARCHAR(255) NOT NULL,
    message     TEXT NOT NULL,
    metadata    JSONB,
    is_read     BOOLEAN NOT NULL DEFAULT FALSE,
    read_at     TIMESTAMP WITH TIME ZONE,
    status      notification_status NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

COMMENT ON TABLE notifications IS 'Notification log for all channels. Created by Kafka consumers';

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_user_channel ON notifications(user_id, channel);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX idx_notifications_type ON notifications(type);

-- ============================================================
-- TABLE: calendar_events
-- Interview scheduling and calendar management
-- ============================================================
CREATE TABLE calendar_events (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    interview_id    UUID,
    organizer_id    UUID NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    location        VARCHAR(500),
    start_time      TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time        TIMESTAMP WITH TIME ZONE NOT NULL,
    status          calendar_event_status NOT NULL DEFAULT 'TENTATIVE',
    attendees       JSONB NOT NULL DEFAULT '[]',
    timezone        VARCHAR(100) NOT NULL DEFAULT 'UTC',
    meeting_link    VARCHAR(1000),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_calendar_events_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE SET NULL,
    CONSTRAINT fk_calendar_events_organizer FOREIGN KEY (organizer_id) REFERENCES users(id),
    CONSTRAINT chk_calendar_times CHECK (end_time > start_time)
);

CREATE INDEX idx_calendar_events_interview ON calendar_events(interview_id);
CREATE INDEX idx_calendar_events_organizer ON calendar_events(organizer_id);
CREATE INDEX idx_calendar_events_start_time ON calendar_events(start_time);
CREATE INDEX idx_calendar_events_status ON calendar_events(status, start_time);

CREATE TRIGGER trg_calendar_events_updated_at
    BEFORE UPDATE ON calendar_events
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- TABLE: audit_logs
-- Full immutable audit trail for all entity changes
-- ============================================================
CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID,
    action          VARCHAR(100) NOT NULL,
    entity_type     VARCHAR(100) NOT NULL,
    entity_id       UUID,
    old_value       JSONB,
    new_value       JSONB,
    ip_address      VARCHAR(50),
    user_agent      VARCHAR(500),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

COMMENT ON TABLE audit_logs IS 'Immutable audit trail. No updates or deletes allowed on this table';

CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);
-- JSONB index for searching old/new values
CREATE INDEX idx_audit_logs_new_value ON audit_logs USING GIN (new_value);

-- ============================================================
-- TABLE: learning_roadmaps
-- AI-generated personalized learning plans
-- ============================================================
CREATE TABLE learning_roadmaps (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id    UUID NOT NULL,
    job_id          UUID,
    roadmap_data    JSONB NOT NULL,
    ai_analysis     TEXT,
    model_version   VARCHAR(50),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_roadmaps_candidate FOREIGN KEY (candidate_id) REFERENCES candidate_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_roadmaps_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE SET NULL
);

COMMENT ON TABLE learning_roadmaps IS 'AI-generated skill gap analysis and learning path. job_id NULL means general roadmap';

CREATE INDEX idx_roadmaps_candidate ON learning_roadmaps(candidate_id);
CREATE INDEX idx_roadmaps_candidate_job ON learning_roadmaps(candidate_id, job_id);
CREATE INDEX idx_roadmaps_data ON learning_roadmaps USING GIN (roadmap_data);

CREATE TRIGGER trg_roadmaps_updated_at
    BEFORE UPDATE ON learning_roadmaps
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- SEED: Initial skills (top 50 tech skills)
-- ============================================================
INSERT INTO skills (id, name, category, normalized_name, usage_count) VALUES
    (uuid_generate_v4(), 'Java', 'Programming Language', 'java', 0),
    (uuid_generate_v4(), 'Python', 'Programming Language', 'python', 0),
    (uuid_generate_v4(), 'JavaScript', 'Programming Language', 'javascript', 0),
    (uuid_generate_v4(), 'TypeScript', 'Programming Language', 'typescript', 0),
    (uuid_generate_v4(), 'Go', 'Programming Language', 'go', 0),
    (uuid_generate_v4(), 'Rust', 'Programming Language', 'rust', 0),
    (uuid_generate_v4(), 'C++', 'Programming Language', 'cpp', 0),
    (uuid_generate_v4(), 'C#', 'Programming Language', 'csharp', 0),
    (uuid_generate_v4(), 'Spring Boot', 'Framework', 'spring-boot', 0),
    (uuid_generate_v4(), 'React', 'Framework', 'react', 0),
    (uuid_generate_v4(), 'Angular', 'Framework', 'angular', 0),
    (uuid_generate_v4(), 'Vue.js', 'Framework', 'vuejs', 0),
    (uuid_generate_v4(), 'Node.js', 'Runtime', 'nodejs', 0),
    (uuid_generate_v4(), 'Next.js', 'Framework', 'nextjs', 0),
    (uuid_generate_v4(), 'Django', 'Framework', 'django', 0),
    (uuid_generate_v4(), 'FastAPI', 'Framework', 'fastapi', 0),
    (uuid_generate_v4(), 'PostgreSQL', 'Database', 'postgresql', 0),
    (uuid_generate_v4(), 'MySQL', 'Database', 'mysql', 0),
    (uuid_generate_v4(), 'MongoDB', 'Database', 'mongodb', 0),
    (uuid_generate_v4(), 'Redis', 'Cache/Database', 'redis', 0),
    (uuid_generate_v4(), 'Elasticsearch', 'Search Engine', 'elasticsearch', 0),
    (uuid_generate_v4(), 'Apache Kafka', 'Messaging', 'kafka', 0),
    (uuid_generate_v4(), 'Docker', 'DevOps', 'docker', 0),
    (uuid_generate_v4(), 'Kubernetes', 'DevOps', 'kubernetes', 0),
    (uuid_generate_v4(), 'AWS', 'Cloud', 'aws', 0),
    (uuid_generate_v4(), 'GCP', 'Cloud', 'gcp', 0),
    (uuid_generate_v4(), 'Azure', 'Cloud', 'azure', 0),
    (uuid_generate_v4(), 'Terraform', 'IaC', 'terraform', 0),
    (uuid_generate_v4(), 'GitHub Actions', 'CI/CD', 'github-actions', 0),
    (uuid_generate_v4(), 'Jenkins', 'CI/CD', 'jenkins', 0),
    (uuid_generate_v4(), 'GraphQL', 'API', 'graphql', 0),
    (uuid_generate_v4(), 'REST API', 'API', 'rest-api', 0),
    (uuid_generate_v4(), 'Microservices', 'Architecture', 'microservices', 0),
    (uuid_generate_v4(), 'System Design', 'Architecture', 'system-design', 0),
    (uuid_generate_v4(), 'Data Structures', 'Computer Science', 'data-structures', 0),
    (uuid_generate_v4(), 'Algorithms', 'Computer Science', 'algorithms', 0),
    (uuid_generate_v4(), 'Machine Learning', 'AI/ML', 'machine-learning', 0),
    (uuid_generate_v4(), 'Deep Learning', 'AI/ML', 'deep-learning', 0),
    (uuid_generate_v4(), 'LLM', 'AI/ML', 'llm', 0),
    (uuid_generate_v4(), 'TailwindCSS', 'Frontend', 'tailwindcss', 0),
    (uuid_generate_v4(), 'Spring Security', 'Security', 'spring-security', 0),
    (uuid_generate_v4(), 'JUnit', 'Testing', 'junit', 0),
    (uuid_generate_v4(), 'Selenium', 'Testing', 'selenium', 0),
    (uuid_generate_v4(), 'Git', 'Version Control', 'git', 0),
    (uuid_generate_v4(), 'Linux', 'Operating System', 'linux', 0),
    (uuid_generate_v4(), 'Hibernate', 'ORM', 'hibernate', 0),
    (uuid_generate_v4(), 'Agile', 'Methodology', 'agile', 0),
    (uuid_generate_v4(), 'Scrum', 'Methodology', 'scrum', 0),
    (uuid_generate_v4(), 'Apache Spark', 'Big Data', 'apache-spark', 0),
    (uuid_generate_v4(), 'Kafka Streams', 'Streaming', 'kafka-streams', 0);
