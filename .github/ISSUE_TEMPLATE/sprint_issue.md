---
name: "🚀 Sprint Issue — Module {{ sprint_number }}: {{ module_name }}"
about: Track progress for an InterviewIQ AI sprint/module
title: "[SPRINT-{{ sprint_number }}] {{ module_name }}"
labels: ["sprint", "module"]
assignees: ["Mekala-balaiah"]
---

## 📋 Sprint Overview

| Field | Value |
|-------|-------|
| **Sprint** | {{ sprint_number }} of 20 |
| **Module** | {{ module_name }} |
| **Status** | 🔄 In Progress |
| **Version** | {{ version }} |
| **Branch** | `sprint/{{ sprint_number }}-{{ module_slug }}` |

---

## 🎯 Objective

{{ objective }}

---

## ✅ Deliverables Checklist

<!-- Check off each item as it is completed -->

- [ ] Files created and production-ready
- [ ] Code integrates with previous sprints
- [ ] Unit/integration tests written
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] VERSION bumped
- [ ] Code committed with conventional commit message
- [ ] Branch merged to `main`
- [ ] GitHub release tag created

---

## 📁 Files Changed

<!-- List files created or modified in this sprint -->

| Action | File | Description |
|--------|------|-------------|
| NEW | | |
| NEW | | |
| MODIFY | | |

---

## 🧪 Test Instructions

```bash
# Start infrastructure
docker-compose up -d

# Run backend tests
cd backend && mvn test

# Run specific module test
cd backend && mvn test -Dtest={{ module_name }}Test
```

---

## 🔗 Related

- Closes #{{ previous_sprint_issue }}
- Part of Epic #1 (InterviewIQ AI Full Build)
- Docs: `docs/` folder
- CHANGELOG: [CHANGELOG.md](../blob/main/CHANGELOG.md)

---

## 📝 Notes

<!-- Any blockers, design decisions, or open questions for this sprint -->
