package com.interviewiq.auth.repository;

import com.interviewiq.auth.entity.OauthAccount;
import com.interviewiq.auth.enums.OauthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OauthAccountRepository extends JpaRepository<OauthAccount, UUID> {
    
    Optional<OauthAccount> findByProviderAndProviderUserId(OauthProvider provider, String providerUserId);
}
