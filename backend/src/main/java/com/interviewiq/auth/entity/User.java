package com.interviewiq.auth.entity;

import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.enums.UserStatus;
import com.interviewiq.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Core user entity for all platform roles.
 *
 * <p>Single-table strategy — role discriminates behaviour.
 * Extended profile data lives in {@code candidate_profiles} / {@code recruiter_profiles}.
 *
 * <p>Password may be null for OAuth-only accounts.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_users_email",
        columnNames = "email"
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "mfa_enabled", nullable = false)
    @Builder.Default
    private boolean mfaEnabled = false;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    // ─── Convenience Methods ──────────────────────────────────────────────────

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(status) && !isDeleted();
    }

    public boolean isOAuthUser() {
        return passwordHash == null;
    }

    public void markEmailVerified() {
        this.emailVerified = true;
        this.status = UserStatus.ACTIVE;
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }
}
