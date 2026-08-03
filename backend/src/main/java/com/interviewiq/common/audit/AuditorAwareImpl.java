package com.interviewiq.common.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Provides the current authenticated user's ID for JPA auditing.
 *
 * <p>Referenced by {@code @EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")}
 * in {@link com.interviewiq.InterviewIqApplication}.
 *
 * <p>Returns empty optional for unauthenticated operations (e.g., public endpoints).
 */
@Component("auditorAwareImpl")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        // The principal is our custom UserPrincipal which holds the user UUID
        if (authentication.getPrincipal() instanceof com.interviewiq.auth.security.UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal.getId());
        }

        return Optional.empty();
    }
}
