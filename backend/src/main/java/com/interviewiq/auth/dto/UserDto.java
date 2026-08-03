package com.interviewiq.auth.dto;

import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatarUrl;
    private UserRole role;
    private UserStatus status;
    private boolean emailVerified;
    private boolean mfaEnabled;
    private Instant lastLoginAt;
    private Instant createdAt;
}
