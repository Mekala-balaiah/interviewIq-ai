-- ============================================================
-- InterviewIQ AI - Flyway Migration V2
-- V2__create_auth_tables.sql
-- Authentication and identity management tables
-- ============================================================

-- ============================================================
-- TABLE: users
-- Core user identity table (all roles share this table)
-- ============================================================
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(30),
    avatar_url      VARCHAR(500),
    role            user_role NOT NULL DEFAULT 'CANDIDATE',
    status          user_status NOT NULL DEFAULT 'PENDING_VERIFICATION',
    email_verified  BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_enabled     BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at   TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID,
    deleted_at      TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uq_users_email UNIQUE (email)
);

COMMENT ON TABLE users IS 'Core user identity for all roles in the InterviewIQ platform';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password. NULL for OAuth-only accounts';
COMMENT ON COLUMN users.role IS 'Single role per user account';
COMMENT ON COLUMN users.deleted_at IS 'Soft delete timestamp. NULL means not deleted';

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_role_status ON users(role, status) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- ============================================================
-- TABLE: refresh_tokens
-- JWT refresh tokens with device tracking
-- ============================================================
CREATE TABLE refresh_tokens (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL,
    token           VARCHAR(500) NOT NULL,
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked         BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at      TIMESTAMP WITH TIME ZONE,
    ip_address      VARCHAR(50),
    user_agent      VARCHAR(500),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_refresh_tokens_token UNIQUE (token)
);

COMMENT ON TABLE refresh_tokens IS 'JWT refresh tokens. One user can have multiple active tokens (multi-device)';

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at) WHERE revoked = FALSE;

-- ============================================================
-- TABLE: email_verifications
-- OTP codes for email verification and MFA
-- ============================================================
CREATE TABLE email_verifications (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL,
    otp_code    VARCHAR(10) NOT NULL,
    purpose     otp_purpose NOT NULL DEFAULT 'EMAIL_VERIFICATION',
    expires_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    used        BOOLEAN NOT NULL DEFAULT FALSE,
    used_at     TIMESTAMP WITH TIME ZONE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_email_verifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

COMMENT ON TABLE email_verifications IS 'OTP codes for email verification, password reset, and MFA';

CREATE INDEX idx_email_verifications_user_purpose ON email_verifications(user_id, purpose);
CREATE INDEX idx_email_verifications_expires_at ON email_verifications(expires_at) WHERE used = FALSE;

-- ============================================================
-- TABLE: password_reset_tokens
-- Secure tokens for the forgot password flow
-- ============================================================
CREATE TABLE password_reset_tokens (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL,
    token       VARCHAR(500) NOT NULL,
    expires_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    used        BOOLEAN NOT NULL DEFAULT FALSE,
    used_at     TIMESTAMP WITH TIME ZONE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_password_reset_token UNIQUE (token)
);

CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens(token);
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);

-- ============================================================
-- TABLE: oauth_accounts
-- Social login accounts linked to users
-- ============================================================
CREATE TABLE oauth_accounts (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL,
    provider            oauth_provider NOT NULL,
    provider_user_id    VARCHAR(255) NOT NULL,
    access_token        TEXT,
    refresh_token       TEXT,
    token_expires_at    TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_oauth_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_oauth_provider_user UNIQUE (provider, provider_user_id)
);

COMMENT ON TABLE oauth_accounts IS 'Google/GitHub OAuth2 accounts linked to platform users';

CREATE INDEX idx_oauth_accounts_user_id ON oauth_accounts(user_id);
CREATE INDEX idx_oauth_provider_user ON oauth_accounts(provider, provider_user_id);

-- ============================================================
-- TRIGGER: auto-update updated_at on users table
-- ============================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
