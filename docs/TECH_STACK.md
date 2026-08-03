# Tech Stack Justification — InterviewIQ AI

## Backend

### Java 21 (LTS)
**Why:** Java 21 is the latest Long-Term Support release and brings Virtual Threads (Project Loom) for high-concurrency I/O without reactive boilerplate. Record classes, sealed interfaces, and pattern matching make business logic cleaner. The JVM ecosystem is the gold standard for enterprise software.

### Spring Boot 3.x
**Why:** The de-facto standard for enterprise Java applications. Spring Boot 3 with Spring Framework 6 provides native GraalVM compilation support, WebFlux for reactive endpoints, and a mature ecosystem of integrations (Security, Data, AI). The opinionated defaults reduce configuration overhead while maintaining full flexibility.

### Spring Security + JWT + OAuth2
**Why:** Spring Security is the most battle-tested Java security framework. JWT enables stateless, horizontally scalable authentication. OAuth2 integration with Google is required for enterprise SSO. Refresh token rotation prevents token theft attacks.

### Spring Data JPA + Hibernate
**Why:** JPA provides a clean object-relational mapping layer. Hibernate is the most mature ORM with excellent PostgreSQL dialect support, second-level caching, and query optimization capabilities. Spring Data repositories eliminate boilerplate CRUD code.

### Flyway
**Why:** Database migrations must be version-controlled and reproducible. Flyway provides ordered, checksummed SQL migration scripts that integrate with Spring Boot startup, ensuring schema consistency across all environments.

### Spring AI + LangChain4j
**Why:** Spring AI provides a vendor-neutral abstraction over LLM providers (OpenAI, Anthropic, Google). LangChain4j adds agentic capabilities, RAG pipelines, memory management, and tool use. Together they form a production-ready AI layer for Java.

### PostgreSQL 16
**Why:** PostgreSQL is the enterprise open-source database standard. Its JSONB support for semi-structured data, full-text search capabilities, excellent indexing strategies, and ACID compliance make it ideal for complex recruitment data models.

### Redis 7
**Why:** Redis provides sub-millisecond caching for hot data paths (dashboard KPIs, user sessions, AI responses). Spring Session with Redis enables distributed session management required for horizontal scaling.

### Apache Kafka
**Why:** Kafka decouples high-throughput event streams (notifications, audit events, email dispatch) from synchronous request/response paths. This ensures that email delivery failures don't affect API response times and enables event sourcing patterns.

### Elasticsearch 8
**Why:** PostgreSQL full-text search degrades at scale. Elasticsearch provides relevance-scored, distributed full-text search across millions of candidate/job documents with sub-100ms query times. It also enables aggregation-based analytics.

### MapStruct
**Why:** Manual DTO-to-entity mapping is error-prone and verbose. MapStruct generates compile-time type-safe mappers with zero reflection overhead. It integrates with Lombok and supports custom mapping rules.

### Lombok
**Why:** Reduces 60-70% of boilerplate code (@Data, @Builder, @Slf4j, @RequiredArgsConstructor). All annotations are compile-time with no runtime dependency, making it a zero-cost abstraction.

### OpenAPI / Swagger
**Why:** API documentation is a first-class concern. SpringDoc OpenAPI 3 auto-generates interactive API docs from annotations, enabling frontend developers and QA to explore and test APIs without reading source code.

---

## Frontend

### React 19 + TypeScript
**Why:** React 19 brings the new Compiler (automatic memoization), Server Components, and improved Suspense. TypeScript provides compile-time type safety eliminating entire classes of runtime bugs. This combination is the industry standard for enterprise SPAs.

### Vite 5
**Why:** Vite provides near-instant HMR (Hot Module Replacement) and ESM-native bundling. Build times are 10-100x faster than Webpack for large TypeScript projects.

### TailwindCSS 3
**Why:** Utility-first CSS enables rapid UI development without context-switching to CSS files. The JIT compiler produces minimal CSS bundles. Design constraints enforce visual consistency.

### Redux Toolkit
**Why:** RTK simplifies Redux with `createSlice`, `createAsyncThunk`, and `RTK Query`. For a complex SaaS app with global state (auth, notifications, candidate pipeline), Redux remains the most predictable state solution.

### React Query (TanStack Query)
**Why:** Server state management separate from client state. React Query handles caching, background refetching, optimistic updates, and pagination — eliminating custom data-fetching boilerplate.

### Framer Motion
**Why:** Production-quality animations for page transitions, modal open/close, drag-and-drop pipeline cards, and micro-interactions. Framer Motion has the best React animation DX and performance.

### Zod + React Hook Form
**Why:** Zod provides runtime type validation with TypeScript inference. RHF provides performant uncontrolled form management. Together they form the gold standard for form validation in React.

### Recharts + Chart.js
**Why:** Recharts for React-native SVG charts (dashboards, funnels). Chart.js for complex visualizations. Using both provides the right tool for each chart type.

---

## Infrastructure

### Docker + Docker Compose
**Why:** Containerization ensures environment parity (dev = staging = production). Docker Compose orchestrates the multi-service local development environment (PostgreSQL, Redis, Kafka, Elasticsearch, Backend, Frontend).

### GitHub Actions
**Why:** Native CI/CD with GitHub repository. Free for public repos. Supports matrix builds, container registry, environment secrets, and deployment workflows.

### Render / Railway
**Why:** Modern PaaS platforms with PostgreSQL and Redis managed services. Simpler than AWS for portfolio deployment while demonstrating real cloud deployment skills.

### Vercel
**Why:** Industry-leading React/Next.js deployment with global CDN, instant preview deployments, and zero-configuration setup.

---

## Summary Decision Matrix

| Requirement | Chosen Technology | Alternatives Considered |
|-------------|-------------------|-------------------------|
| Backend API | Spring Boot 3 | Quarkus, Micronaut |
| Database | PostgreSQL | MySQL, MongoDB |
| Cache | Redis | Memcached, Hazelcast |
| Messaging | Kafka | RabbitMQ, ActiveMQ |
| Search | Elasticsearch | OpenSearch, Solr |
| AI | Spring AI + LangChain4j | Semantic Kernel |
| Frontend | React 19 + TypeScript | Next.js, Vue, Angular |
| Styling | TailwindCSS | CSS Modules, Styled-Components |
| Auth | JWT + OAuth2 | Session-based, SAML |
| CI/CD | GitHub Actions | Jenkins, CircleCI |
