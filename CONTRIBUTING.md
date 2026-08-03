# Contributing Guide — InterviewIQ AI

> **Project owner:** [@Mekala-balaiah](https://github.com/Mekala-balaiah)
> **Repo:** [github.com/Mekala-balaiah/interviewiq-ai](https://github.com/Mekala-balaiah/interviewiq-ai)

---

## 🏗️ Development Workflow

### 1. Branch Strategy

| Branch | Purpose |
|--------|---------|
| `main` | Protected. Only sprint merges via PR |
| `sprint/X-module-slug` | One branch per sprint |

```bash
# Start a new sprint branch
git checkout main
git pull origin main
git checkout -b sprint/4-auth
```

### 2. Commit Message Convention

We follow **[Conventional Commits](https://www.conventionalcommits.org/)**.

```
<type>(<scope>): <short description>

[optional body]
[optional footer]
```

**Types:**

| Type | When to use |
|------|-------------|
| `feat` | New feature or sprint deliverable |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `refactor` | Code change without feature/fix |
| `test` | Adding/updating tests |
| `chore` | Build, CI, dependencies |
| `perf` | Performance improvement |

**Examples:**

```bash
git commit -m "feat(auth): implement JWT authentication filter"
git commit -m "feat(auth): add Google OAuth2 success handler"
git commit -m "docs: update sprint tracker for sprint 4"
git commit -m "chore: bump version to 0.4.0"
git commit -m "test(auth): add AuthService integration tests"
```

### 3. Sprint Completion Push

```bash
# Sprint completion checklist:

# 1. Update CHANGELOG.md with new sprint entry
# 2. Bump VERSION file
# 3. Update docs/SPRINT_TRACKER.md
# 4. Commit everything
git add .
git commit -m "feat(sprint-4): complete Authentication & Authorization module

- User entity with role-based enum
- JWT access + refresh token authentication
- Google OAuth2 integration  
- Email OTP verification
- Password reset flow
- Spring Security configuration
- All /api/v1/auth endpoints
- Integration tests

Closes #4"

# 5. Push sprint branch
git push origin sprint/4-auth

# 6. Create Pull Request on GitHub
#    PR title: [SPRINT-4] Authentication & Authorization — v0.4.0
#    Use the PR template

# 7. After PR merge → GitHub Actions auto-creates release tag
```

### 4. Pull Request Rules

- PR title: `[SPRINT-X] Module Name — vX.Y.0`
- Must link the sprint issue (`Closes #X`)
- All CI checks must pass
- Use the PR template checklist
- Squash merge to `main`

---

## 📁 Folder Structure Rules

| Rule | Detail |
|------|--------|
| Package prefix | Always `com.interviewiq.<module>` |
| Controller | `com.interviewiq.<module>.controller` |
| Service | `com.interviewiq.<module>.service` |
| Repository | `com.interviewiq.<module>.repository` |
| Entity | `com.interviewiq.<module>.entity` |
| DTO | `com.interviewiq.<module>.dto` |
| Mapper | `com.interviewiq.<module>.mapper` |

---

## 🧪 Testing Rules

- All services must have unit tests (`@ExtendWith(MockitoExtension.class)`)
- All controllers must have MockMvc tests
- Integration tests use Testcontainers (PostgreSQL + Redis)
- Test coverage target: **≥ 80%**

```bash
# Run all tests
cd backend && mvn test

# Run specific module
cd backend && mvn test -Dtest=AuthServiceTest

# Run with coverage
cd backend && mvn verify jacoco:report
```

---

## 📚 Documentation Rules

Every sprint MUST update:
1. `CHANGELOG.md` — new version entry
2. `VERSION` — bump the version number  
3. `docs/SPRINT_TRACKER.md` — mark sprint as done
4. Inline code comments on complex logic

---

## 🔐 Security Rules

- **NEVER** commit `.env`, API keys, or secrets
- `.env.example` must be updated for any new variables
- All secrets via GitHub Secrets / environment variables
