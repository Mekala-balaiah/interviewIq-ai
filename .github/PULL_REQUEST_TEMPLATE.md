## 📋 Pull Request — InterviewIQ AI

### 🔗 Closes

Closes #<!-- Issue number -->

---

### 🏷️ Type of Change

- [ ] 🚀 Sprint completion (new module)
- [ ] 🐛 Bug fix
- [ ] ✨ New feature
- [ ] 🔧 Refactoring (no functionality change)
- [ ] 📚 Documentation update
- [ ] ⚙️ Configuration / DevOps change
- [ ] 🧪 Tests only

---

### 📦 Sprint / Module

| Field | Value |
|-------|-------|
| **Sprint** | Sprint X — Module Y |
| **Version** | `0.X.0` |
| **Branch** | `sprint/X-module-name` → `main` |

---

### 📁 Files Changed

| Action | File | Description |
|--------|------|-------------|
| NEW | | |
| MODIFY | | |

---

### ✅ Pre-Merge Checklist

#### Code Quality
- [ ] Code follows Clean Architecture / SOLID principles
- [ ] No Lombok, MapStruct, or annotation processor issues
- [ ] No hardcoded credentials or secrets
- [ ] Error handling is complete (GlobalExceptionHandler covers new exceptions)
- [ ] All new endpoints have OpenAPI annotations

#### Testing
- [ ] Unit tests written and passing (`mvn test`)
- [ ] Integration tests passing (if applicable)
- [ ] Tested against Docker Compose local stack

#### Documentation
- [ ] `CHANGELOG.md` updated with this sprint's changes
- [ ] `VERSION` file bumped
- [ ] `docs/SPRINT_TRACKER.md` updated
- [ ] Inline code comments where necessary
- [ ] New env variables added to `.env.example`

#### GitHub
- [ ] Commit messages follow Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`)
- [ ] Branch named correctly (`sprint/X-module-slug`)
- [ ] Sprint issue linked and checklist completed

---

### 🧪 Test Results

```
# Paste mvn test output here
Tests run: XX, Failures: 0, Errors: 0, Skipped: 0
```

---

### 📝 Notes for Reviewer

<!-- Any context, design decisions, or known limitations -->
