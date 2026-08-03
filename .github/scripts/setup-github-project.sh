#!/usr/bin/env bash
# ============================================================
# InterviewIQ AI — GitHub Project Setup Script
# Run this ONCE after creating the GitHub repository
# ============================================================
# Usage:
#   chmod +x .github/scripts/setup-github-project.sh
#   GH_TOKEN=<your_token> ./setup-github-project.sh
# ============================================================

REPO="Mekala-balaiah/interviewiq-ai"
GH="gh"  # GitHub CLI

echo "🚀 Setting up InterviewIQ AI GitHub Project..."

# ============================================================
# STEP 1: Create Labels
# ============================================================
echo "🏷️  Creating labels..."

declare -A LABELS=(
  ["sprint"]="0075ca:Track sprint issues"
  ["module:auth"]="e4e669:Authentication module"
  ["module:candidate"]="0e8a16:Candidate module"
  ["module:recruiter"]="1d76db:Recruiter module"
  ["module:hr"]="5319e7:HR module"
  ["module:admin"]="006b75:Admin module"
  ["module:ai"]="8b5cf6:AI features"
  ["module:assessment"]="f9d0c4:Coding assessment"
  ["module:analytics"]="fef2c0:Analytics & reports"
  ["module:devops"]="bfd4f2:DevOps & deployment"
  ["done"]="28a745:Completed sprint"
  ["in-progress"]="fbca04:Currently being worked on"
  ["blocked"]="d73a4a:Blocked on dependency"
  ["documentation"]="0075ca:Documentation updates"
  ["backend"]="e6e6e6:Backend Java changes"
  ["frontend"]="e6e6e6:Frontend React changes"
  ["database"]="c5def5:Database / migrations"
)

for name in "${!LABELS[@]}"; do
  IFS=':' read -r color description <<< "${LABELS[$name]}"
  $GH label create "$name" --color "$color" --description "$description" --repo "$REPO" 2>/dev/null || \
  $GH label edit "$name" --color "$color" --description "$description" --repo "$REPO"
  echo "  ✓ Label: $name"
done

# ============================================================
# STEP 2: Create Milestones (one per sprint)
# ============================================================
echo "🎯 Creating milestones..."

milestones=(
  "Sprint 1: Project Vision"
  "Sprint 2: System Architecture"
  "Sprint 3: Spring Boot Setup"
  "Sprint 4: Authentication"
  "Sprint 5: Candidate Module"
  "Sprint 6: Recruiter Module"
  "Sprint 7: HR Module"
  "Sprint 8: Admin Module"
  "Sprint 9: Resume AI"
  "Sprint 10: Interview AI"
  "Sprint 11: Coding Assessment"
  "Sprint 12: Dashboard & Analytics"
  "Sprint 13: Notifications"
  "Sprint 14: Redis Cache"
  "Sprint 15: Elasticsearch"
  "Sprint 16: Kafka Messaging"
  "Sprint 17: Testing Suite"
  "Sprint 18: Deployment"
  "Sprint 19: Performance"
  "Sprint 20: Final Review"
)

for milestone in "${milestones[@]}"; do
  $GH api repos/$REPO/milestones \
    --method POST \
    --field title="$milestone" \
    --field state="open" 2>/dev/null
  echo "  ✓ Milestone: $milestone"
done

# ============================================================
# STEP 3: Create Sprint Issues
# ============================================================
echo "📋 Creating sprint issues..."

create_issue() {
  local number=$1
  local title=$2
  local labels=$3
  local milestone=$4
  local body=$5

  $GH issue create \
    --repo "$REPO" \
    --title "[SPRINT-$number] $title" \
    --body "$body" \
    --label "$labels" \
    --milestone "$milestone" 2>/dev/null
  echo "  ✓ Issue #$number: $title"
}

# Sprint 1 — CLOSED
create_issue 1 "Module 1: Project Vision & Architecture" \
  "sprint,documentation,done" \
  "Sprint 1: Project Vision" \
  "## ✅ COMPLETED in v0.1.0

**Deliverables:**
- Project Vision Document
- Business Requirements (FR + NFR)
- Feature List (100+ features)
- Tech Stack Justification
- Architecture Overview (ASCII diagrams, data flow, Kafka topics, Redis strategy)

**Release:** v0.1.0"

# Sprint 2 — CLOSED
create_issue 2 "Module 2: System Architecture & DB Design" \
  "sprint,database,documentation,done" \
  "Sprint 2: System Architecture" \
  "## ✅ COMPLETED in v0.2.0

**Deliverables:**
- ER Diagram (20 entities, Mermaid)
- API Design (all endpoints with contracts)
- 6 Sequence Diagrams
- Flyway V1-V6 migration scripts
- 50 seed skills

**Release:** v0.2.0"

# Sprint 3 — CLOSED
create_issue 3 "Module 3: Spring Boot Project Setup" \
  "sprint,backend,done" \
  "Sprint 3: Spring Boot Setup" \
  "## ✅ COMPLETED in v0.3.0

**Deliverables:**
- Maven pom.xml (30+ dependencies)
- Main application class
- application.yml (base + dev + prod)
- Config classes (Redis, Kafka, OpenAPI, Jackson, Properties)
- Common layer (ApiResponse, exceptions, BaseEntity, auditing)
- Docker Compose (8 services)
- .env.example, .gitignore

**Release:** v0.3.0"

# Sprint 4 — OPEN (current)
create_issue 4 "Module 4: Authentication & Authorization" \
  "sprint,backend,module:auth,in-progress" \
  "Sprint 4: Authentication" \
  "## 🔄 IN PROGRESS — Target: v0.4.0

### Planned Deliverables

- [ ] User JPA entity
- [ ] JWT utility (generate, validate, refresh)
- [ ] JwtAuthenticationFilter
- [ ] SecurityConfig (filter chain, CORS, RBAC)
- [ ] AuthService (register, login, refresh, logout)
- [ ] AuthController (all /api/v1/auth endpoints)
- [ ] Email OTP verification
- [ ] Forgot password / reset flow
- [ ] Google OAuth2 integration
- [ ] Auth DTOs + MapStruct mappers
- [ ] Integration tests"

# Sprints 5-20 — PLANNED
declare -a SPRINT_TITLES=(
  "Module 5: Candidate Module"
  "Module 6: Recruiter Module"
  "Module 7: HR Module"
  "Module 8: Admin Module"
  "Module 9: Resume AI Engine"
  "Module 10: Interview AI Engine"
  "Module 11: Coding Assessment"
  "Module 12: Dashboard & Analytics"
  "Module 13: Notifications (Kafka + SSE)"
  "Module 14: Redis Cache Layer"
  "Module 15: Elasticsearch Search"
  "Module 16: Kafka Messaging"
  "Module 17: Testing Suite"
  "Module 18: Deployment (Docker + CI/CD)"
  "Module 19: Performance Optimization"
  "Module 20: Final Review & Polish"
)

declare -a SPRINT_LABELS=(
  "sprint,backend,module:candidate"
  "sprint,backend,module:recruiter"
  "sprint,backend,module:hr"
  "sprint,backend,module:admin"
  "sprint,backend,module:ai"
  "sprint,backend,module:ai"
  "sprint,backend,module:assessment"
  "sprint,backend,module:analytics"
  "sprint,backend,module:ai"
  "sprint,backend"
  "sprint,backend"
  "sprint,backend"
  "sprint,backend"
  "sprint,module:devops"
  "sprint,backend"
  "sprint,documentation"
)

declare -a MILESTONE_NAMES=(
  "Sprint 5: Candidate Module"
  "Sprint 6: Recruiter Module"
  "Sprint 7: HR Module"
  "Sprint 8: Admin Module"
  "Sprint 9: Resume AI"
  "Sprint 10: Interview AI"
  "Sprint 11: Coding Assessment"
  "Sprint 12: Dashboard & Analytics"
  "Sprint 13: Notifications"
  "Sprint 14: Redis Cache"
  "Sprint 15: Elasticsearch"
  "Sprint 16: Kafka Messaging"
  "Sprint 17: Testing Suite"
  "Sprint 18: Deployment"
  "Sprint 19: Performance"
  "Sprint 20: Final Review"
)

for i in "${!SPRINT_TITLES[@]}"; do
  sprint_num=$((i + 5))
  create_issue $sprint_num "${SPRINT_TITLES[$i]}" \
    "${SPRINT_LABELS[$i]}" \
    "${MILESTONE_NAMES[$i]}" \
    "## ⬜ PLANNED — Target: v0.$sprint_num.0

This sprint will be started after Sprint $((sprint_num - 1)) is merged and released.

See [Sprint Tracker](../blob/main/docs/SPRINT_TRACKER.md) for full details."
done

echo ""
echo "✅ GitHub project setup complete!"
echo ""
echo "Next steps:"
echo "  1. Go to: https://github.com/Mekala-balaiah/interviewiq-ai/issues"
echo "  2. Create a GitHub Project Board (classic or new Projects)"
echo "  3. Add all sprint issues to the board"
echo "  4. Set up branch protection on 'main'"
