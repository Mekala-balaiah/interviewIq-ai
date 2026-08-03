# Feature List — InterviewIQ AI

## Module 1: Authentication & Security
- [x] Email/password registration and login
- [x] Google OAuth2 sign-in
- [x] JWT access + refresh token authentication
- [x] Email OTP verification
- [x] Forgot password / reset password flow
- [x] Role-based access control (CANDIDATE, RECRUITER, HR, ADMIN, SUPER_ADMIN)
- [x] Rate limiting per user and IP
- [x] Secure HTTP headers (CSP, HSTS, X-Frame)
- [x] Audit log for authentication events
- [x] Session management with Redis

## Module 2: Candidate Portal
- [x] Candidate profile creation and management
- [x] Resume upload (PDF/DOCX, up to 10MB)
- [x] Resume AI parsing and data extraction
- [x] ATS score display per job
- [x] Skills visualization
- [x] Job application tracking
- [x] Interview history and feedback view
- [x] Coding assessment portal
- [x] AI learning roadmap view
- [x] Notification center

## Module 3: Recruiter Portal
- [x] Job posting creation with rich text JD
- [x] Job requirement and skill tagging
- [x] Candidate pipeline management (Kanban view)
- [x] AI candidate ranking per job
- [x] ATS score comparison table
- [x] Interview question generation by role/level
- [x] Interview scheduling
- [x] Candidate profile deep-view
- [x] AI recruiter chatbot
- [x] Candidate report export (PDF/CSV)
- [x] Bulk candidate operations

## Module 4: HR Dashboard
- [x] Team-level hiring metrics
- [x] Job approval/rejection workflow
- [x] Company profile management
- [x] Evaluation template configuration
- [x] Compliance and audit log viewer
- [x] Role and permission management

## Module 5: Admin Dashboard
- [x] User management (CRUD)
- [x] Role assignment
- [x] Platform analytics (system-wide)
- [x] AI model configuration
- [x] Subscription and usage management
- [x] System health monitoring panel
- [x] Feature flag management

## Module 6: AI Resume Engine
- [x] Resume parser (Spring AI + LangChain4j)
- [x] Skills extraction and normalization
- [x] ATS score algorithm (keyword match + semantic match)
- [x] Job-candidate match scoring
- [x] Missing skills gap analysis
- [x] AI-generated improvement suggestions

## Module 7: AI Interview Engine
- [x] AI interview question generation (by role, level, topic)
- [x] Live AI interview session (Q&A conductor)
- [x] Real-time answer evaluation
- [x] Post-interview AI feedback report
- [x] Interview summary generation
- [x] Candidate sentiment analysis
- [x] Difficulty adaptive questioning

## Module 8: Coding Assessment
- [x] In-browser code editor (Monaco/CodeMirror)
- [x] Multi-language support (Java, Python, JS, C++)
- [x] AI-generated coding problems
- [x] Real-time code execution (sandboxed)
- [x] Automated test case evaluation
- [x] Plagiarism detection flag
- [x] Submission history

## Module 9: Analytics & Reports
- [x] Recruiter dashboard KPIs
- [x] Application funnel chart
- [x] Time-to-hire trend
- [x] Candidate quality score distribution
- [x] Job performance metrics
- [x] Team leaderboard
- [x] Exportable PDF/CSV reports
- [x] Date-range filters

## Module 10: Search & Discovery
- [x] Elasticsearch-powered full-text search
- [x] Search candidates by skills, experience, location
- [x] Search jobs by title, skills, company
- [x] Search companies
- [x] Advanced filter combinations
- [x] Search autocomplete suggestions

## Module 11: Notifications
- [x] Real-time in-app notifications (WebSocket/SSE)
- [x] Email notifications (SMTP/SendGrid)
- [x] Notification center with read/unread state
- [x] Notification preferences settings
- [x] Kafka event-driven notification dispatch

## Module 12: Calendar & Scheduling
- [x] Interview time slot management
- [x] Calendar view (day/week/month)
- [x] Conflict detection
- [x] Automated email reminders
- [x] Timezone support

## Module 13: Settings & Configuration
- [x] User profile settings
- [x] Password change
- [x] Notification preferences
- [x] Company settings (for admins)
- [x] API key management (for integrations)
- [x] Theme toggle (dark/light)

## UI/UX Features
- [x] Premium glassmorphism design system
- [x] Dark mode / Light mode toggle
- [x] Fully responsive (mobile-first)
- [x] Framer Motion animations throughout
- [x] Command palette (⌘K)
- [x] Global search
- [x] Loading skeletons
- [x] Infinite scroll for lists
- [x] Toast notifications
- [x] Accessible (WCAG 2.1 AA)
- [x] Keyboard navigation
