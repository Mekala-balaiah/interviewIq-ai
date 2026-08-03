package com.interviewiq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Type-safe binding for all {@code interviewiq.*} properties in application.yml.
 *
 * <p>Bound via {@code @ConfigurationPropertiesScan} in {@link com.interviewiq.InterviewIqApplication}.
 */
@Configuration
@ConfigurationProperties(prefix = "interviewiq")
public class InterviewIqProperties {

    private Jwt jwt = new Jwt();
    private RateLimiting rateLimiting = new RateLimiting();
    private Storage storage = new Storage();
    private Ai ai = new Ai();
    private Email email = new Email();
    private Cors cors = new Cors();
    private Kafka kafka = new Kafka();
    private OAuth2 oauth2 = new OAuth2();

    // ─── JWT ─────────────────────────────────────────────────────────────────
    public static class Jwt {
        private String secret;
        private long accessTokenExpirationMs = 900_000L;
        private long refreshTokenExpirationMs = 604_800_000L;

        // getters + setters
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getAccessTokenExpirationMs() { return accessTokenExpirationMs; }
        public void setAccessTokenExpirationMs(long accessTokenExpirationMs) { this.accessTokenExpirationMs = accessTokenExpirationMs; }
        public long getRefreshTokenExpirationMs() { return refreshTokenExpirationMs; }
        public void setRefreshTokenExpirationMs(long refreshTokenExpirationMs) { this.refreshTokenExpirationMs = refreshTokenExpirationMs; }
    }

    // ─── Rate Limiting ────────────────────────────────────────────────────────
    public static class RateLimiting {
        private boolean enabled = true;
        private int apiRequestsPerMinute = 100;
        private int authRequestsPerMinute = 10;
        private int aiRequestsPerMinute = 20;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getApiRequestsPerMinute() { return apiRequestsPerMinute; }
        public void setApiRequestsPerMinute(int apiRequestsPerMinute) { this.apiRequestsPerMinute = apiRequestsPerMinute; }
        public int getAuthRequestsPerMinute() { return authRequestsPerMinute; }
        public void setAuthRequestsPerMinute(int authRequestsPerMinute) { this.authRequestsPerMinute = authRequestsPerMinute; }
        public int getAiRequestsPerMinute() { return aiRequestsPerMinute; }
        public void setAiRequestsPerMinute(int aiRequestsPerMinute) { this.aiRequestsPerMinute = aiRequestsPerMinute; }
    }

    // ─── Storage ──────────────────────────────────────────────────────────────
    public static class Storage {
        private String type = "local";
        private String localUploadDir = "./uploads";
        private String s3Bucket;
        private String s3Region = "us-east-1";

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getLocalUploadDir() { return localUploadDir; }
        public void setLocalUploadDir(String localUploadDir) { this.localUploadDir = localUploadDir; }
        public String getS3Bucket() { return s3Bucket; }
        public void setS3Bucket(String s3Bucket) { this.s3Bucket = s3Bucket; }
        public String getS3Region() { return s3Region; }
        public void setS3Region(String s3Region) { this.s3Region = s3Region; }
    }

    // ─── AI ───────────────────────────────────────────────────────────────────
    public static class Ai {
        private boolean resumeAnalysisEnabled = true;
        private boolean interviewAiEnabled = true;
        private int maxQuestionsPerInterview = 15;
        private AtsScoreWeights atsScoreWeights = new AtsScoreWeights();

        public static class AtsScoreWeights {
            private int keywordMatch = 40;
            private int semanticMatch = 30;
            private int format = 15;
            private int experience = 15;

            public int getKeywordMatch() { return keywordMatch; }
            public void setKeywordMatch(int keywordMatch) { this.keywordMatch = keywordMatch; }
            public int getSemanticMatch() { return semanticMatch; }
            public void setSemanticMatch(int semanticMatch) { this.semanticMatch = semanticMatch; }
            public int getFormat() { return format; }
            public void setFormat(int format) { this.format = format; }
            public int getExperience() { return experience; }
            public void setExperience(int experience) { this.experience = experience; }
        }

        public boolean isResumeAnalysisEnabled() { return resumeAnalysisEnabled; }
        public void setResumeAnalysisEnabled(boolean resumeAnalysisEnabled) { this.resumeAnalysisEnabled = resumeAnalysisEnabled; }
        public boolean isInterviewAiEnabled() { return interviewAiEnabled; }
        public void setInterviewAiEnabled(boolean interviewAiEnabled) { this.interviewAiEnabled = interviewAiEnabled; }
        public int getMaxQuestionsPerInterview() { return maxQuestionsPerInterview; }
        public void setMaxQuestionsPerInterview(int maxQuestionsPerInterview) { this.maxQuestionsPerInterview = maxQuestionsPerInterview; }
        public AtsScoreWeights getAtsScoreWeights() { return atsScoreWeights; }
        public void setAtsScoreWeights(AtsScoreWeights atsScoreWeights) { this.atsScoreWeights = atsScoreWeights; }
    }

    // ─── Email ────────────────────────────────────────────────────────────────
    public static class Email {
        private String fromAddress = "noreply@interviewiq.ai";
        private String fromName = "InterviewIQ AI";

        public String getFromAddress() { return fromAddress; }
        public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
        public String getFromName() { return fromName; }
        public void setFromName(String fromName) { this.fromName = fromName; }
    }

    // ─── CORS ─────────────────────────────────────────────────────────────────
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:3000", "http://localhost:5173");
        private String allowedMethods = "GET,POST,PUT,DELETE,PATCH,OPTIONS";
        private boolean allowCredentials = true;
        private long maxAge = 3600;

        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
        public String getAllowedMethods() { return allowedMethods; }
        public void setAllowedMethods(String allowedMethods) { this.allowedMethods = allowedMethods; }
        public boolean isAllowCredentials() { return allowCredentials; }
        public void setAllowCredentials(boolean allowCredentials) { this.allowCredentials = allowCredentials; }
        public long getMaxAge() { return maxAge; }
        public void setMaxAge(long maxAge) { this.maxAge = maxAge; }
    }

    // ─── Kafka ────────────────────────────────────────────────────────────────
    public static class Kafka {
        private Topics topics = new Topics();

        public static class Topics {
            private String interviewCreated = "interview-iq.interview.created";
            private String resumeUploaded = "interview-iq.resume.uploaded";
            private String candidateApplied = "interview-iq.candidate.applied";
            private String assessmentCompleted = "interview-iq.assessment.completed";
            private String authEvents = "interview-iq.auth.events";
            private String emailEvents = "interview-iq.email.events";
            private String notificationEvents = "interview-iq.notification.events";

            public String getInterviewCreated() { return interviewCreated; }
            public void setInterviewCreated(String interviewCreated) { this.interviewCreated = interviewCreated; }
            public String getResumeUploaded() { return resumeUploaded; }
            public void setResumeUploaded(String resumeUploaded) { this.resumeUploaded = resumeUploaded; }
            public String getCandidateApplied() { return candidateApplied; }
            public void setCandidateApplied(String candidateApplied) { this.candidateApplied = candidateApplied; }
            public String getAssessmentCompleted() { return assessmentCompleted; }
            public void setAssessmentCompleted(String assessmentCompleted) { this.assessmentCompleted = assessmentCompleted; }
            public String getAuthEvents() { return authEvents; }
            public void setAuthEvents(String authEvents) { this.authEvents = authEvents; }
            public String getEmailEvents() { return emailEvents; }
            public void setEmailEvents(String emailEvents) { this.emailEvents = emailEvents; }
            public String getNotificationEvents() { return notificationEvents; }
            public void setNotificationEvents(String notificationEvents) { this.notificationEvents = notificationEvents; }
        }

        public Topics getTopics() { return topics; }
        public void setTopics(Topics topics) { this.topics = topics; }
    }

    // ─── OAuth2 ───────────────────────────────────────────────────────────────
    public static class OAuth2 {
        private List<String> authorizedRedirectUris = List.of("http://localhost:3000/oauth2/callback");

        public List<String> getAuthorizedRedirectUris() { return authorizedRedirectUris; }
        public void setAuthorizedRedirectUris(List<String> authorizedRedirectUris) { this.authorizedRedirectUris = authorizedRedirectUris; }
    }

    // ─── Top-Level Getters/Setters ─────────────────────────────────────────────
    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public RateLimiting getRateLimiting() { return rateLimiting; }
    public void setRateLimiting(RateLimiting rateLimiting) { this.rateLimiting = rateLimiting; }
    public Storage getStorage() { return storage; }
    public void setStorage(Storage storage) { this.storage = storage; }
    public Ai getAi() { return ai; }
    public void setAi(Ai ai) { this.ai = ai; }
    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }
    public Cors getCors() { return cors; }
    public void setCors(Cors cors) { this.cors = cors; }
    public Kafka getKafka() { return kafka; }
    public void setKafka(Kafka kafka) { this.kafka = kafka; }
    public OAuth2 getOauth2() { return oauth2; }
    public void setOauth2(OAuth2 oauth2) { this.oauth2 = oauth2; }
}
