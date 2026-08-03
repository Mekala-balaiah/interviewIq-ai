# CHANGELOG — InterviewIQ AI

All notable changes to this project will be documented in this file.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [0.17.0] - 2026-08-03
### Added
- `application-test.yml`: Self-contained CI test profile (H2, no Kafka/Redis/ES required).
- `NotificationServiceImplTest`: 6 unit tests covering paginated retrieval, unread count, mark-as-read (success and unauthorized), not-found, and mark-all-as-read.
- `CandidateSearchServiceImplTest`: 4 unit tests covering ES sync, sync with missing profile, keyword search, and empty results.
- `CandidateSearchControllerTest`: 3 `@WebMvcTest` tests covering search endpoint and admin sync trigger.
### Fixed
- `CandidateServiceTest`: Added missing `CandidateSearchService`, `UserRepository`, and `CandidateMapper` mocks (broken after Sprint 16).

## [0.16.0] - 2026-08-03
### Added
- Elasticsearch `CandidateDocument` mapped to the `candidates` index.
- `CandidateSearchRepository` extending `ElasticsearchRepository`.
- `CandidateSearchServiceImpl` with compound NativeQuery builder supporting fuzzy full-text search, location/skill/experience/remote filters.
- `CandidateSearchController`: `GET /api/v1/candidates/search` (Elasticsearch) and `POST /api/v1/candidates/search/sync/{id}` (Admin sync).
- Auto-sync ES index on candidate profile update or skill add/remove (non-blocking with graceful error handling).

## [0.15.0] - 2026-08-03
### Added
- Redis caching for Dashboard KPIs, pipeline funnels, and application trends (`@Cacheable("dashboard-kpis")`).
- Redis caching for AI operations including resume analysis, skill extraction, and interview question generation (`@Cacheable("ats-scores")`, `@Cacheable("ai-responses")`).
- Programmatic cache invalidation (`@CacheEvict`) in Application and Job workflows.

## [0.14.0] - 2026-08-03
### Added
- Real-time Server-Sent Events (SSE) `/stream` endpoint for live notifications.
- Kafka-based background `NotificationConsumer` for processing events from the `notification-events` topic.
- In-App persistent notifications tracking with read/unread statuses.
- Mock `EmailNotificationService` for demonstration.

## [0.13.0] - 2026-08-03
### Added
- Advanced Search capability using Spring Data JPA Specifications.
- `CandidateSearchController` for recruiters to filter candidate profiles.
- `JobController` updated with a `/search` endpoint to filter published jobs.

## [0.12.0] - 2026-08-03
### Added
- `DashboardController` and `AnalyticsService` for recruiter KPIs, funnels, and application time-series trends.
- Complex native `@Query` mapping in `ApplicationRepository` to serve dashboard charts efficiently.

## [0.11.0] - 2026-08-03
### Added
- `Assessment`, `AssessmentQuestion`, `AssessmentSubmission` entities with JSONB mapping for tests/results.
- `CodeExecutionService` stub simulating compiling code, running tests, and checking plagiarism.
- Endpoints to assign, start, submit code to, and complete technical coding assessments in `AssessmentController`.

## [0.10.0] - 2026-08-03
### Added
- `Interview`, `InterviewQuestion`, `InterviewResponse` entities representing the AI Interview Engine.
- `InterviewAiService` stub simulating real-time dynamic question generation and score evaluation (0-10) per answer.
- Endpoints to schedule, start, submit responses to, and complete AI interviews in `InterviewController`.

## [0.9.0] - 2026-08-03
### Added
- `ResumeAnalysis` entity mapping to existing `resume_analyses` table.
- AI engine stubs (`AiService`, `MockAiServiceImpl`) for extracting skills and computing ATS score.
- `ResumeParsingService` that reads a resume and runs AI simulation.
- Endpoints `POST /api/v1/resumes/{resumeId}/analyze` and `GET /api/v1/resumes/{resumeId}/analyses` to trigger and view resume parsing results.

## [0.8.0] - 2026-08-03
### Added
- `messages` and `message_attachments` tables for internal communication.
- `Message` and `MessageAttachment` entities.
- `MessagingService` with support for direct messaging, conversation retrieval, and application-specific messages.
- Webhook endpoint `/api/v1/webhooks/email/inbound` to parse incoming emails and convert them into internal messages.

## [0.7.0] - 2026-08-03
### Added
- `hr_profiles` and `job_approvals` tables to track HR workflows.
- `HrProfile` and `JobApproval` entities.
- HR Service & Controllers to manage profiles, dashboard metrics, and job approvals.
- Ability for recruiters to submit jobs for approval, moving them to `PENDING_APPROVAL`.
- HR dashboard to view active jobs, pending jobs, and team size.

## [0.6.0] — 2026-08-03 | Sprint 6 — Module 6

### Added
- `company/entity/*` — Fleshed out `Company` entity
- `recruiter/entity/*` — Fleshed out `RecruiterProfile` entity
- `job/entity/*` — Fleshed out `Job` entity, created `JobSkill` join entity
- `company/repository/*` — `CompanyRepository`
- `recruiter/repository/*` — `RecruiterProfileRepository`
- `job/repository/*` — `JobRepository`, `JobSkillRepository`
- `company/dto/*` — `CompanyDto`, `UpdateCompanyRequest`
- `recruiter/dto/*` — `RecruiterProfileDto`, `UpdateRecruiterProfileRequest`, `RecruiterDashboardDto`
- `job/dto/*` — `JobDto`, `CreateJobRequest`, `UpdateJobRequest`, `JobSkillDto`, `AddJobSkillRequest`
- `mapper/*` — MapStruct mappers for Company, RecruiterProfile, and Job
- `company/service/*` — `CompanyService` implementation
- `recruiter/service/*` — `RecruiterService` implementation
- `job/service/*` — `JobService`, `JobSkillService` implementations
- `application/service/*` — Updated `ApplicationService` for pipeline management
- `company/controller/*` — `CompanyController`
- `recruiter/controller/*` — `RecruiterProfileController`, `RecruiterChatbotController` (Stub)
- `job/controller/*` — `JobController`
- `application/controller/*` — `PipelineController`
- `test/.../JobControllerTest.java` — Integration tests for Job API
- `test/.../PipelineControllerTest.java` — Integration tests for Pipeline API

---

## [0.5.0] — 2026-08-03 | Sprint 5 — Module 5

### Added
- `candidate/enums/*` — `SkillProficiency`, `ResumeParseStatus` enums
- `job/enums/*` — `JobStatus`, `EmploymentType`, `WorkMode`, `ExperienceLevel` enums
- `application/enums/*` — `ApplicationStatus` enum
- `candidate/entity/*` — `CandidateProfile`, `Skill`, `CandidateSkill`, `Resume` entities
- `application/entity/*` — `Application` entity
- `company/entity/*` — `Company` stub entity
- `job/entity/*` — `Job` stub entity
- `recruiter/entity/*` — `RecruiterProfile` stub entity
- `candidate/repository/*` — 4 repositories for Candidate module entities
- `application/repository/*` — `ApplicationRepository`
- `candidate/dto/*` — 5 DTOs (`CandidateProfileDto`, `UpdateCandidateProfileRequest`, `SkillDto`, `CandidateSkillDto`, `AddCandidateSkillRequest`, `ResumeDto`)
- `application/dto/*` — 2 DTOs (`ApplicationDto`, `ApplyForJobRequest`)
- `candidate/mapper/*` — `CandidateMapper`, `SkillMapper`, `ResumeMapper` (MapStruct)
- `application/mapper/*` — `ApplicationMapper` (MapStruct)
- `common/storage/*` — `FileStorageService` and `LocalFileStorageServiceImpl` for multipart uploads
- `candidate/service/*` — `CandidateService`, `SkillService`, `CandidateSkillService`, `ResumeService` implementations
- `application/service/*` — `ApplicationService` implementation
- `candidate/controller/*` — `CandidateProfileController`, `SkillController`, `ResumeController` with Swagger and Security annotations
- `application/controller/*` — `ApplicationController`
- `test/.../CandidateServiceTest.java` — Unit tests for profile completion calculation
- `test/.../CandidateProfileControllerTest.java` — Integration tests for Candidate Profile API
- `test/.../ResumeControllerTest.java` — Integration tests for Resume API with Multipart mock

---

## [0.4.0] — 2026-08-03 | Sprint 4 — Module 4

### Added
- `entity/User.java` — Core JPA entity for all platform roles
- `entity/RefreshToken.java` — Entity for JWT refresh tokens
- `entity/EmailVerification.java` — Entity for OTP email verification
- `entity/PasswordResetToken.java` — Entity for password reset flow
- `entity/OauthAccount.java` — Entity for OAuth2 social logins
- `enums/*` — `UserRole`, `UserStatus`, `OtpPurpose`, `OauthProvider` enums
- `repository/*` — 5 Spring Data JPA repositories for the entities above
- `security/UserPrincipal.java` — Custom `UserDetails` implementation
- `security/CustomUserDetailsService.java` — `UserDetailsService` implementation
- `security/JwtTokenProvider.java` — Utility for JWT generation and validation
- `security/JwtAuthenticationFilter.java` — Request filter for JWT auth
- `security/JwtAuthenticationEntryPoint.java` — Custom 401 unauthorized handler
- `security/SecurityConfig.java` — Spring Security configuration (CORS, CSRF, endpoint protection)
- `dto/*` — 6 DTOs (`LoginRequest`, `RegisterRequest`, `AuthResponse`, etc.)
- `mapper/UserMapper.java` — MapStruct interface for User mapping
- `service/AuthService(Impl).java` — Login, registration, token refresh, OTP, reset password
- `controller/AuthController.java` — REST endpoints for authentication
- `test/.../JwtTokenProviderTest.java` — Unit tests for JWT utility
- `test/.../AuthControllerTest.java` — Integration tests for Auth endpoints

---

## [0.3.0] — 2026-08-03 | Sprint 3 — Module 3

### Added
- `backend/pom.xml` — Production Maven POM with 30+ dependencies (Spring Boot 3.3, AI, Security, Redis, Kafka, ES, JWT, MapStruct, Lombok, OpenAPI, Testcontainers)
- `InterviewIqApplication.java` — Main entry point with all enable annotations
- `application.yml` — Full base config (DataSource, JPA, Flyway, Redis, Kafka, ES, Spring AI, Mail, Actuator, custom properties)
- `application-dev.yml` — Dev profile overrides
- `application-prod.yml` — Production profile overrides
- `config/OpenApiConfig.java` — OpenAPI 3.0 + Swagger UI with JWT Bearer scheme
- `config/JacksonConfig.java` — ObjectMapper (ISO-8601 dates, null exclusion)
- `config/RedisConfig.java` — RedisTemplate + CacheManager with 8 named cache regions
- `config/KafkaConfig.java` — 7 Kafka topic beans with partition config
- `config/InterviewIqProperties.java` — Type-safe config properties binding
- `common/response/ApiResponse.java` — Standardized API response wrapper
- `common/response/ApiError.java` — Structured error payload with field errors
- `common/response/PagedResponse.java` — Paginated response wrapper
- `common/exception/GlobalExceptionHandler.java` — 15+ exception handlers
- `common/exception/*.java` — Full exception hierarchy (5 domain exception types)
- `common/audit/BaseEntity.java` — JPA base entity with audit fields + soft-delete
- `common/audit/AuditorAwareImpl.java` — SecurityContext-based auditor
- `docker-compose.yml` — Full dev stack (PostgreSQL, Redis, Zookeeper, Kafka, Kafka UI, ES, Kibana, MailHog)
- `.env.example` — All environment variable documentation
- `.gitignore` — Project-wide gitignore

---

## [0.2.0] — 2026-08-03 | Sprint 2 — Module 2

### Added
- `docs/ER_DIAGRAM.md` — Full ER diagram with 20 entities (Mermaid), index strategy, table descriptions
- `docs/API_DESIGN.md` — Complete REST API design (all endpoints, request/response contracts, status codes)
- `docs/SEQUENCE_DIAGRAMS.md` — 6 core sequence diagrams (Auth, Resume AI, Interview AI, Application, Notifications)
- `backend/src/main/resources/db/migration/V1__create_extensions_and_enums.sql` — PostgreSQL extensions + 15 ENUM types
- `backend/src/main/resources/db/migration/V2__create_auth_tables.sql` — users, refresh_tokens, email_verifications, password_reset_tokens, oauth_accounts
- `backend/src/main/resources/db/migration/V3__create_company_and_profile_tables.sql` — companies, candidate_profiles, recruiter_profiles, skills, candidate_skills, resumes
- `backend/src/main/resources/db/migration/V4__create_jobs_and_applications.sql` — jobs, job_skills, applications, resume_analyses
- `backend/src/main/resources/db/migration/V5__create_interview_and_assessment_tables.sql` — interviews, interview_questions, interview_responses, assessments, assessment_questions, assessment_submissions
- `backend/src/main/resources/db/migration/V6__create_platform_tables.sql` — notifications, calendar_events, audit_logs, learning_roadmaps + 50 seed skills

---

## [0.1.0] — 2026-08-03 | Sprint 1 — Module 1

### Added
- Project vision document (`docs/PROJECT_VISION.md`)
- Business requirements document (`docs/BUSINESS_REQUIREMENTS.md`)
- Full feature list (`docs/FEATURES.md`)
- Tech stack justification (`docs/TECH_STACK.md`)
- Architecture overview (`docs/ARCHITECTURE_OVERVIEW.md`)
- CHANGELOG.md
- VERSION file
- Root README.md

---
