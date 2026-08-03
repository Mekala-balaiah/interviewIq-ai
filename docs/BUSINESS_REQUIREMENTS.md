# Business Requirements — InterviewIQ AI

## Functional Requirements

### FR-01: Authentication & Identity
- Users shall register via email/password or Google OAuth2
- System shall issue JWT access tokens (15 min TTL) and refresh tokens (7 day TTL)
- System shall support email verification via OTP
- System shall support forgot password flow
- System shall enforce role-based access control (RBAC)
- Roles: CANDIDATE, RECRUITER, HR_MANAGER, COMPANY_ADMIN, SUPER_ADMIN

### FR-02: Candidate Module
- Candidates shall upload resumes (PDF, DOCX)
- System shall parse resume and extract: name, email, phone, skills, education, experience
- System shall compute ATS Score (0–100) per job posting
- Candidates shall view applied jobs and interview status
- Candidates shall receive AI-generated learning roadmap
- Candidates shall access AI-conducted interview sessions
- Candidates shall complete coding assessments in-browser

### FR-03: Recruiter Module
- Recruiters shall create and publish job postings with JD, requirements, skills
- Recruiters shall view candidate pipeline with AI rankings
- Recruiters shall generate AI interview questions per job role
- Recruiters shall view candidate ATS scores and skill match analysis
- Recruiters shall schedule interviews via calendar integration
- Recruiters shall access a conversational AI assistant (chatbot)
- Recruiters shall export candidate reports (PDF, CSV)

### FR-04: HR Module
- HR managers shall view team-level hiring metrics
- HR managers shall approve or reject job postings
- HR managers shall manage company profile and settings
- HR managers shall view compliance and audit logs
- HR managers shall configure interview evaluation templates

### FR-05: Admin Module
- Admins shall manage all users, roles, and permissions
- Admins shall view platform-wide analytics and usage metrics
- Admins shall configure AI model settings
- Admins shall manage subscription and billing
- Admins shall trigger system maintenance tasks

### FR-06: AI Features
- Resume Parser: Extract structured data from uploaded documents
- ATS Scorer: Score candidate resumes against job descriptions
- Job Matcher: Rank candidates by relevance to job
- Interview Generator: Generate tailored interview questions
- Interview Conductor: AI-led interview sessions with evaluation
- Coding Generator: Generate coding problems by difficulty/topic
- Feedback Generator: Post-interview AI feedback for candidates
- Learning Roadmap: Personalized skill gap analysis and learning plan
- Recruiter Assistant: Chatbot for recruiter queries
- RAG Layer: Context-aware AI using company knowledge base

### FR-07: Search
- Full-text search on: Candidates, Jobs, Skills, Companies, Interviews
- Search suggestions and autocomplete
- Filter by: Date, Score, Status, Skills, Location, Experience

### FR-08: Notifications
- Real-time notifications via WebSocket
- Email notifications for: Interview scheduled, Application status, Feedback ready
- In-app notification center with read/unread tracking
- Push notification support (Phase 2)

### FR-09: Analytics & Reports
- Dashboard KPIs: Active Jobs, Total Candidates, Interviews, Offers
- Time-series charts: Applications over time, Hire rate
- Funnel visualization: Application → Screening → Interview → Offer
- Export reports: PDF, CSV, Excel
- Recruiter performance metrics

### FR-10: Calendar
- Interview scheduling with time slot management
- Calendar sync (Google Calendar — Phase 2)
- Conflict detection
- Automated reminder notifications

---

## Non-Functional Requirements

### NFR-01: Performance
- API response time < 200ms (p95) under normal load
- Concurrent users: 1,000 (Starter), 10,000 (Enterprise)
- AI endpoints: < 5s for generation tasks
- File upload: Support up to 10MB per resume

### NFR-02: Availability
- Target uptime: 99.9% (SLA)
- Zero-downtime deployments via rolling updates
- Database failover with PostgreSQL replication

### NFR-03: Security
- OWASP Top 10 compliance
- Data encryption at rest and in transit (TLS 1.3)
- PII data handling per GDPR/CCPA guidelines
- Rate limiting: 100 req/min per user, 1000 req/min per IP
- Secure headers: CSP, HSTS, X-Frame-Options

### NFR-04: Scalability
- Horizontally scalable via Docker + orchestration
- Stateless services with Redis session management
- Event-driven architecture via Kafka
- Database connection pooling via HikariCP

### NFR-05: Maintainability
- Clean Architecture with SOLID principles
- 80%+ test coverage (unit + integration)
- OpenAPI documentation for all endpoints
- Structured JSON logging
- Distributed tracing ready

### NFR-06: Accessibility
- WCAG 2.1 AA compliance for UI
- Keyboard navigation support
- Screen reader compatibility
- Responsive design: Mobile, Tablet, Desktop

---

## Constraints

| Constraint | Detail |
|------------|--------|
| Language | Java 21 (LTS), TypeScript 5.x |
| Database | PostgreSQL 16+ |
| Cloud | Deployment on Render/Railway/Vercel |
| AI Provider | OpenAI API (configurable via Spring AI) |
| Licensing | MIT (portfolio project) |
