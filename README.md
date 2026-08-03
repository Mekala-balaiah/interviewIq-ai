# InterviewIQ AI
### Enterprise AI Recruitment & Interview Intelligence Platform

[![CI Backend](https://github.com/Mekala-balaiah/interviewiq-ai/actions/workflows/ci-backend.yml/badge.svg)](https://github.com/Mekala-balaiah/interviewiq-ai/actions/workflows/ci-backend.yml)
[![Sprint Progress](https://img.shields.io/badge/Sprint-3%20of%2020-blue?style=flat-square)](https://github.com/Mekala-balaiah/interviewiq-ai/blob/main/docs/SPRINT_TRACKER.md)
[![Version](https://img.shields.io/badge/Version-0.3.0-green?style=flat-square)](https://github.com/Mekala-balaiah/interviewiq-ai/releases)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue?style=flat-square&logo=react)](https://react.dev/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

> **Built by:** [@Mekala-balaiah](https://github.com/Mekala-balaiah) | **Status:** Active Development

---

## 🚀 What is InterviewIQ AI?

InterviewIQ AI is a **production-grade, enterprise SaaS platform** that automates and intelligences the entire recruitment lifecycle — from resume parsing and ATS scoring, to AI-driven interview generation, real-time coding assessments, and recruiter analytics.

Built with **Java 21 + Spring Boot 3** on the backend and **React 19 + TypeScript** on the frontend.

---

## 📊 Sprint Progress

```
███░░░░░░░░░░░░░░░░░  Sprint 3 of 20 (15% complete)
```

| Sprint | Module | Status | Version |
|--------|--------|--------|---------|
| ✅ 1 | Project Vision & Architecture | Done | [v0.1.0](https://github.com/Mekala-balaiah/interviewiq-ai/releases/tag/v0.1.0) |
| ✅ 2 | System Architecture & DB Design | Done | [v0.2.0](https://github.com/Mekala-balaiah/interviewiq-ai/releases/tag/v0.2.0) |
| ✅ 3 | Spring Boot Project Setup | Done | [v0.3.0](https://github.com/Mekala-balaiah/interviewiq-ai/releases/tag/v0.3.0) |
| 🔄 4 | Authentication & Authorization | **Active** | v0.4.0 |
| ⬜ 5–20 | Remaining Modules | Planned | — |

> 📋 [Full Sprint Tracker →](docs/SPRINT_TRACKER.md)

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 21, Spring Boot 3.3, Spring Security, JWT, OAuth2 |
| **Database** | PostgreSQL 16, Flyway migrations, Spring Data JPA |
| **Cache** | Redis 7 (8 cache regions) |
| **Messaging** | Apache Kafka (7 topics) |
| **Search** | Elasticsearch 8 |
| **AI** | Spring AI, LangChain4j, OpenAI GPT-4 |
| **Frontend** | React 19, TypeScript, Vite, TailwindCSS, Framer Motion |
| **DevOps** | Docker, Docker Compose, GitHub Actions |

---

## 🚀 Quick Start (Local Dev)

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker Desktop
- Node.js 20+ (for frontend)

### 1. Clone
```bash
git clone https://github.com/Mekala-balaiah/interviewiq-ai.git
cd interviewiq-ai
```

### 2. Start Infrastructure
```bash
docker-compose up -d
```

| Service | URL |
|---------|-----|
| PostgreSQL | `localhost:5432` |
| Redis | `localhost:6379` |
| Kafka | `localhost:9092` |
| Kafka UI | http://localhost:8090 |
| Elasticsearch | http://localhost:9200 |
| Kibana | http://localhost:5601 |
| MailHog | http://localhost:8025 |

### 3. Configure Environment
```bash
copy .env.example .env
# Edit .env — add your OPENAI_API_KEY and JWT_SECRET
```

### 4. Run Backend
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

| Endpoint | URL |
|----------|-----|
| API Base | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs | http://localhost:8080/api-docs |
| Health | http://localhost:8080/actuator/health |

---

## 📁 Project Structure

```
interviewiq-ai/
├── .github/                    # GitHub Actions, issue templates, PR template
│   ├── workflows/
│   │   ├── ci-backend.yml      # Build, test, quality checks
│   │   ├── sprint-release.yml  # Auto-release on version bump
│   │   └── sprint-tracker.yml  # Progress tracking
│   ├── ISSUE_TEMPLATE/         # Sprint & bug templates
│   └── PULL_REQUEST_TEMPLATE.md
├── backend/                    # Spring Boot 3 (Java 21)
│   ├── src/main/java/com/interviewiq/
│   │   ├── config/             # Spring configurations
│   │   ├── common/             # Shared utilities, exceptions, responses
│   │   ├── auth/               # Authentication module (Sprint 4)
│   │   ├── candidate/          # Candidate module (Sprint 5)
│   │   └── ...                 # More modules per sprint
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/       # Flyway V1-V6 scripts
├── frontend/                   # React 19 + TypeScript (Sprint TBD)
├── docs/                       # All documentation
│   ├── SPRINT_TRACKER.md       # 📊 Sprint progress tracker
│   ├── PROJECT_VISION.md
│   ├── ARCHITECTURE_OVERVIEW.md
│   ├── ER_DIAGRAM.md
│   ├── API_DESIGN.md
│   └── SEQUENCE_DIAGRAMS.md
├── docker-compose.yml          # Full dev stack (8 services)
├── .env.example                # Environment variable template
├── CHANGELOG.md                # Version history
├── CONTRIBUTING.md             # Development workflow guide
└── VERSION                     # Current: 0.3.0
```

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [📊 Sprint Tracker](docs/SPRINT_TRACKER.md) | Sprint progress, velocity, board |
| [🏗️ Architecture](docs/ARCHITECTURE_OVERVIEW.md) | System architecture diagrams |
| [🗄️ ER Diagram](docs/ER_DIAGRAM.md) | Database schema (20 entities) |
| [🌐 API Design](docs/API_DESIGN.md) | REST API reference |
| [🔄 Sequence Diagrams](docs/SEQUENCE_DIAGRAMS.md) | Core flow diagrams |
| [📋 Features](docs/FEATURES.md) | Complete feature list |
| [🛠️ Tech Stack](docs/TECH_STACK.md) | Technology justification |
| [📝 CHANGELOG](CHANGELOG.md) | Version history |
| [🤝 Contributing](CONTRIBUTING.md) | Development workflow |

---

## 🔗 GitHub Project Links

- 📋 [Sprint Issues](https://github.com/Mekala-balaiah/interviewiq-ai/issues)
- 🏷️ [Releases](https://github.com/Mekala-balaiah/interviewiq-ai/releases)
- ⚡ [GitHub Actions](https://github.com/Mekala-balaiah/interviewiq-ai/actions)
- 📌 [Project Board](https://github.com/users/Mekala-balaiah/projects)

---

## 📄 License

MIT License — see [LICENSE](LICENSE)

---

*Built with ❤️ by [@Mekala-balaiah](https://github.com/Mekala-balaiah) as an enterprise portfolio project demonstrating full-stack Java + React AI engineering.*
