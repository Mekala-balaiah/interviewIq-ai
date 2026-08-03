# Architecture Overview — InterviewIQ AI

## Architectural Style

InterviewIQ AI follows **Clean Architecture** (Ports & Adapters / Hexagonal Architecture) implemented as a **Modular Monolith** that is **microservice-ready**.

This means:
- Each domain module (Candidate, Recruiter, Interview, etc.) is a self-contained vertical slice
- Modules communicate through well-defined interfaces (ports), not direct dependencies
- The monolith can be decomposed into microservices with minimal code changes
- Each module could run as an independent Docker container in the future

---

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│  React 19 + TypeScript SPA  │  Mobile (Phase 2)                │
│  Vite  │  TailwindCSS  │  Redux Toolkit  │  React Query         │
└────────────────────────┬────────────────────────────────────────┘
                         │ HTTPS / WebSocket
┌────────────────────────▼────────────────────────────────────────┐
│                     API GATEWAY LAYER                           │
│           Spring Boot 3 (Embedded Tomcat / Virtual Threads)     │
│   Global Exception Handler  │  Rate Limiter  │  CORS Config     │
│   Request Logging  │  OpenAPI  │  API Versioning (/api/v1)      │
└────────────────────────┬────────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────────┐
│                    SECURITY LAYER                               │
│   Spring Security Filter Chain                                  │
│   JWT Auth Filter  │  OAuth2 Resource Server                    │
│   Role-Based Access Control (RBAC)                              │
└────────────────────────┬────────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────────┐
│                  APPLICATION LAYER (Use Cases)                  │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │  Auth Module  │  │  Candidate   │  │    Recruiter Module  │  │
│  │  Service      │  │  Service     │  │    Service           │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │  HR Module   │  │  Admin Module│  │    Interview AI       │  │
│  │  Service     │  │  Service     │  │    Service            │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │  Resume AI   │  │  Assessment  │  │    Analytics          │  │
│  │  Service     │  │  Service     │  │    Service            │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
└────────────────────────┬────────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────────┐
│                  DOMAIN LAYER (Core Business Logic)             │
│   Entities  │  Value Objects  │  Domain Events                  │
│   Domain Services  │  Repository Interfaces (Ports)             │
└────────────────────────┬────────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER (Adapters)                 │
│                                                                 │
│  ┌────────────┐  ┌────────────┐  ┌──────────┐  ┌───────────┐  │
│  │ PostgreSQL │  │   Redis    │  │  Kafka   │  │  ES 8     │  │
│  │  (JPA)    │  │  (Cache)   │  │(Messaging)│  │ (Search)  │  │
│  └────────────┘  └────────────┘  └──────────┘  └───────────┘  │
│  ┌────────────┐  ┌────────────┐  ┌──────────────────────────┐  │
│  │ Spring AI  │  │LangChain4j │  │    Email (SMTP/SendGrid) │  │
│  │  (LLM)    │  │  (RAG/Agent)│  │                          │  │
│  └────────────┘  └────────────┘  └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Folder Structure

```
secure-hub/
├── backend/                         # Spring Boot 3 Application
│   ├── src/main/java/com/interviewiq/
│   │   ├── InterviewIqApplication.java
│   │   ├── config/                  # All Spring configurations
│   │   │   ├── SecurityConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── KafkaConfig.java
│   │   │   ├── ElasticsearchConfig.java
│   │   │   ├── OpenApiConfig.java
│   │   │   └── ApplicationConfig.java
│   │   ├── common/                  # Shared utilities
│   │   │   ├── exception/           # Global exception handling
│   │   │   ├── response/            # API response wrappers
│   │   │   ├── validation/          # Custom validators
│   │   │   ├── audit/               # Audit entity base
│   │   │   └── constants/           # Application constants
│   │   ├── auth/                    # Authentication module
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── mapper/
│   │   │   └── security/
│   │   ├── candidate/               # Candidate module
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   └── mapper/
│   │   ├── recruiter/               # Recruiter module
│   │   ├── hr/                      # HR module
│   │   ├── admin/                   # Admin module
│   │   ├── interview/               # Interview AI module
│   │   ├── resume/                  # Resume AI module
│   │   ├── assessment/              # Coding assessment module
│   │   ├── notification/            # Notification module
│   │   ├── analytics/               # Analytics module
│   │   ├── search/                  # Elasticsearch module
│   │   └── ai/                      # AI engine (Spring AI + LangChain4j)
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-prod.yml
│   │   └── db/migration/            # Flyway SQL scripts
│   └── src/test/
│       └── java/com/interviewiq/    # All test classes
│
├── frontend/                        # React 19 + TypeScript
│   ├── src/
│   │   ├── app/                     # Redux store + RTK
│   │   ├── assets/                  # Images, fonts
│   │   ├── components/              # Shared reusable components
│   │   │   ├── ui/                  # Design system primitives
│   │   │   ├── forms/               # Form components
│   │   │   ├── charts/              # Chart wrappers
│   │   │   └── layout/              # Layout components
│   │   ├── features/                # Feature-sliced modules
│   │   │   ├── auth/
│   │   │   ├── candidate/
│   │   │   ├── recruiter/
│   │   │   ├── hr/
│   │   │   ├── admin/
│   │   │   ├── interview/
│   │   │   ├── assessment/
│   │   │   ├── analytics/
│   │   │   └── notifications/
│   │   ├── hooks/                   # Custom React hooks
│   │   ├── lib/                     # Utilities, API client, validators
│   │   ├── pages/                   # Route-level page components
│   │   ├── router/                  # React Router config
│   │   ├── styles/                  # Global CSS + Tailwind config
│   │   └── types/                   # Global TypeScript types
│   ├── public/
│   ├── index.html
│   ├── vite.config.ts
│   ├── tailwind.config.ts
│   └── tsconfig.json
│
├── docs/                            # All documentation
│   ├── PROJECT_VISION.md
│   ├── BUSINESS_REQUIREMENTS.md
│   ├── FEATURES.md
│   ├── TECH_STACK.md
│   ├── ARCHITECTURE_OVERVIEW.md
│   ├── ER_DIAGRAM.md                # (Sprint 2)
│   ├── API_DESIGN.md                # (Sprint 2)
│   └── SEQUENCE_DIAGRAMS.md        # (Sprint 2)
│
├── infra/                           # Infrastructure
│   ├── docker/
│   │   ├── Dockerfile.backend
│   │   ├── Dockerfile.frontend
│   │   └── nginx.conf
│   ├── docker-compose.yml
│   ├── docker-compose.prod.yml
│   └── .github/
│       └── workflows/
│           ├── ci.yml
│           └── deploy.yml
│
├── CHANGELOG.md
├── VERSION
└── README.md
```

---

## Data Flow: Resume Upload → AI Score

```
Candidate                   Backend                      AI Layer
    │                          │                              │
    │  POST /api/v1/resumes    │                              │
    │─────────────────────────>│                              │
    │                          │  Store file (S3/local)       │
    │                          │  Publish Kafka event         │
    │                          │─────────────────────────────>│
    │                          │                              │ Parse PDF/DOCX
    │                          │                              │ Extract skills/exp
    │                          │                              │ Compute ATS score
    │                          │<─────────────────────────────│
    │                          │  Store result in PostgreSQL  │
    │                          │  Cache in Redis              │
    │                          │  Send notification           │
    │  200 OK + resume_id      │                              │
    │<─────────────────────────│                              │
```

---

## Security Architecture

```
Request → Rate Limiter → CORS Filter → JWT Auth Filter
                                           │
                              ┌────────────▼───────────┐
                              │  Token Valid?           │
                              │  Role Authorized?       │
                              └────────────┬───────────┘
                                           │
                                    Controller
                                           │
                                     Service Layer
                                           │
                              Audit Log written to DB
```

---

## Event Architecture (Kafka Topics)

| Topic | Producer | Consumer |
|-------|----------|----------|
| `interview-iq.interview.created` | InterviewService | NotificationService, EmailService |
| `interview-iq.resume.uploaded` | ResumeController | AIResumeService |
| `interview-iq.candidate.applied` | ApplicationService | RecruiterNotification, EmailService |
| `interview-iq.assessment.completed` | AssessmentService | ScoringService, NotificationService |
| `interview-iq.auth.events` | AuthService | AuditLogService |

---

## Caching Strategy (Redis)

| Cache Key Pattern | TTL | Purpose |
|-------------------|-----|---------|
| `user:{userId}:profile` | 30 min | User profile data |
| `candidate:{id}:ats-score:{jobId}` | 1 hour | ATS scores |
| `dashboard:{recruiterId}:kpis` | 5 min | Dashboard KPIs |
| `ai:resume:{hash}:analysis` | 24 hours | AI resume analysis |
| `job:{id}:candidates:ranked` | 15 min | Ranked candidate list |
| `session:{token}` | 7 days | Refresh token session |
