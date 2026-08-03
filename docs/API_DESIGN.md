# API Design — InterviewIQ AI

## API Conventions

- **Base URL:** `/api/v1`
- **Authentication:** `Authorization: Bearer <JWT>`
- **Content-Type:** `application/json`
- **Response Format:** Standardized wrapper

```json
{
  "success": true,
  "data": { },
  "message": "Operation successful",
  "timestamp": "2026-08-03T10:00:00Z",
  "path": "/api/v1/candidates/me"
}
```

**Error Response:**
```json
{
  "success": false,
  "error": {
    "code": "CANDIDATE_NOT_FOUND",
    "message": "Candidate with id '...' not found",
    "details": []
  },
  "timestamp": "2026-08-03T10:00:00Z",
  "path": "/api/v1/candidates/me"
}
```

**Pagination Response:**
```json
{
  "success": true,
  "data": {
    "content": [],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "last": false
  }
}
```

---

## Auth API — `/api/v1/auth`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/register` | Public | Register new user |
| POST | `/login` | Public | Email/password login |
| POST | `/refresh` | Public | Refresh access token |
| POST | `/logout` | Bearer | Logout and revoke token |
| POST | `/verify-email` | Public | Verify OTP |
| POST | `/resend-verification` | Public | Resend OTP |
| POST | `/forgot-password` | Public | Send reset email |
| POST | `/reset-password` | Public | Reset with token |
| GET | `/me` | Bearer | Get current user |
| GET | `/oauth2/google` | Public | Google OAuth2 initiate |
| GET | `/oauth2/callback` | Public | Google OAuth2 callback |

**POST /register**
```json
Request:
{
  "firstName": "Arjun",
  "lastName": "Sharma",
  "email": "arjun@example.com",
  "password": "Secure@123",
  "role": "CANDIDATE"
}

Response 201:
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "arjun@example.com",
    "firstName": "Arjun",
    "role": "CANDIDATE",
    "emailVerified": false
  }
}
```

**POST /login**
```json
Request:
{ "email": "arjun@example.com", "password": "Secure@123" }

Response 200:
{
  "success": true,
  "data": {
    "accessToken": "eyJ...",
    "refreshToken": "eyJ...",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "user": { "id": "uuid", "email": "...", "role": "CANDIDATE" }
  }
}
```

---

## Candidate API — `/api/v1/candidates`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/me` | Bearer | CANDIDATE | Get own profile |
| PUT | `/me` | Bearer | CANDIDATE | Update own profile |
| GET | `/me/applications` | Bearer | CANDIDATE | List my applications |
| GET | `/me/interviews` | Bearer | CANDIDATE | List my interviews |
| GET | `/me/assessments` | Bearer | CANDIDATE | List my assessments |
| GET | `/me/roadmap` | Bearer | CANDIDATE | Get learning roadmap |
| POST | `/me/resumes` | Bearer | CANDIDATE | Upload resume |
| GET | `/me/resumes` | Bearer | CANDIDATE | List my resumes |
| DELETE | `/me/resumes/{id}` | Bearer | CANDIDATE | Delete resume |
| POST | `/me/resumes/{id}/set-primary` | Bearer | CANDIDATE | Set primary resume |
| POST | `/me/skills` | Bearer | CANDIDATE | Add skill |
| DELETE | `/me/skills/{skillId}` | Bearer | CANDIDATE | Remove skill |
| GET | `/{id}` | Bearer | RECRUITER, HR, ADMIN | Get candidate by ID |
| GET | `/` | Bearer | RECRUITER, HR, ADMIN | List/search candidates |

**PUT /candidates/me**
```json
Request:
{
  "headline": "Senior Java Developer",
  "bio": "Passionate backend engineer...",
  "location": "Bangalore, India",
  "linkedinUrl": "https://linkedin.com/in/arjun",
  "yearsOfExperience": 5,
  "currentTitle": "Software Engineer II",
  "expectedSalaryMin": 150000,
  "expectedSalaryMax": 200000,
  "salaryCurrency": "INR",
  "openToRemote": true
}
```

---

## Recruiter API — `/api/v1/recruiters`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/me` | Bearer | RECRUITER | Get own profile |
| PUT | `/me` | Bearer | RECRUITER | Update own profile |
| GET | `/me/jobs` | Bearer | RECRUITER | List my jobs |
| GET | `/me/pipeline` | Bearer | RECRUITER | Full application pipeline |
| GET | `/me/analytics` | Bearer | RECRUITER | Recruiter KPIs |
| POST | `/me/ai/chat` | Bearer | RECRUITER | AI chatbot message |
| GET | `/{id}` | Bearer | HR, ADMIN | Get recruiter by ID |

---

## Jobs API — `/api/v1/jobs`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/` | Bearer | RECRUITER | Create job posting |
| GET | `/` | Public | - | List active jobs |
| GET | `/{id}` | Public | - | Get job details |
| PUT | `/{id}` | Bearer | RECRUITER, HR | Update job |
| DELETE | `/{id}` | Bearer | RECRUITER, HR, ADMIN | Archive job |
| POST | `/{id}/publish` | Bearer | RECRUITER | Publish job |
| POST | `/{id}/close` | Bearer | RECRUITER, HR | Close job |
| GET | `/{id}/applications` | Bearer | RECRUITER, HR | List applications |
| GET | `/{id}/candidates/ranked` | Bearer | RECRUITER, HR | AI-ranked candidates |
| POST | `/{id}/apply` | Bearer | CANDIDATE | Apply to job |
| GET | `/{id}/skills` | Public | - | Get required skills |
| POST | `/{id}/skills` | Bearer | RECRUITER | Add skill requirement |

**POST /jobs**
```json
Request:
{
  "title": "Senior Java Developer",
  "description": "We are looking for...",
  "requirements": "5+ years Java experience...",
  "employmentType": "FULL_TIME",
  "workMode": "HYBRID",
  "experienceLevel": "SENIOR",
  "minExperienceYears": 5,
  "maxExperienceYears": 8,
  "location": "Bangalore, India",
  "salaryMin": 1800000,
  "salaryMax": 2500000,
  "salaryCurrency": "INR",
  "skills": [
    { "skillId": "uuid", "isRequired": true, "proficiencyLevel": "ADVANCED" }
  ]
}
```

---

## Applications API — `/api/v1/applications`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/{id}` | Bearer | CANDIDATE, RECRUITER, HR | Get application |
| PUT | `/{id}/status` | Bearer | RECRUITER, HR | Update status |
| POST | `/{id}/notes` | Bearer | RECRUITER, HR | Add recruiter note |
| GET | `/{id}/timeline` | Bearer | CANDIDATE, RECRUITER | Application timeline |

---

## Interviews API — `/api/v1/interviews`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/` | Bearer | RECRUITER | Schedule interview |
| GET | `/{id}` | Bearer | All | Get interview |
| PUT | `/{id}` | Bearer | RECRUITER, HR | Update interview |
| DELETE | `/{id}` | Bearer | RECRUITER, HR | Cancel interview |
| POST | `/{id}/start` | Bearer | CANDIDATE | Start AI interview |
| POST | `/{id}/submit-response` | Bearer | CANDIDATE | Submit answer |
| POST | `/{id}/complete` | Bearer | CANDIDATE | Complete interview |
| GET | `/{id}/feedback` | Bearer | CANDIDATE, RECRUITER | Get AI feedback |
| GET | `/{id}/summary` | Bearer | RECRUITER, HR | Get AI summary |
| POST | `/ai/generate-questions` | Bearer | RECRUITER | Generate AI questions |

**POST /interviews/ai/generate-questions**
```json
Request:
{
  "jobTitle": "Senior Java Developer",
  "experienceLevel": "SENIOR",
  "topics": ["Spring Boot", "Microservices", "System Design"],
  "questionCount": 10,
  "difficulty": "MEDIUM"
}

Response:
{
  "success": true,
  "data": {
    "questions": [
      {
        "questionText": "Explain the difference between...",
        "questionType": "TECHNICAL",
        "difficulty": "MEDIUM",
        "topic": "Spring Boot",
        "expectedAnswer": "..."
      }
    ]
  }
}
```

---

## Resume AI API — `/api/v1/resumes`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/upload` | Bearer | CANDIDATE | Upload resume file |
| GET | `/{id}` | Bearer | CANDIDATE, RECRUITER | Get resume metadata |
| GET | `/{id}/analysis` | Bearer | CANDIDATE, RECRUITER | Get AI analysis |
| POST | `/{id}/analyze` | Bearer | CANDIDATE, RECRUITER | Trigger AI analysis |
| POST | `/{id}/analyze/{jobId}` | Bearer | CANDIDATE, RECRUITER | Analyze against job |
| GET | `/{id}/ats-score/{jobId}` | Bearer | CANDIDATE, RECRUITER | Get ATS score |

---

## Assessments API — `/api/v1/assessments`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/` | Bearer | RECRUITER | Create assessment |
| GET | `/{id}` | Bearer | CANDIDATE, RECRUITER | Get assessment |
| POST | `/{id}/start` | Bearer | CANDIDATE | Start assessment |
| POST | `/{id}/questions/{qId}/submit` | Bearer | CANDIDATE | Submit code |
| POST | `/{id}/submit` | Bearer | CANDIDATE | Final submission |
| GET | `/{id}/results` | Bearer | RECRUITER, HR | Get results |
| POST | `/ai/generate` | Bearer | RECRUITER | AI-generate problems |

---

## Search API — `/api/v1/search`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/candidates` | Bearer | RECRUITER, HR | Search candidates |
| GET | `/jobs` | Public | - | Search jobs |
| GET | `/skills` | Public | - | Search skills |
| GET | `/companies` | Public | - | Search companies |
| GET | `/global` | Bearer | All | Global search |

**GET /search/candidates?q=java&location=bangalore&minExp=3&maxExp=7&page=0&size=20**
```json
Response:
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "uuid",
        "name": "Arjun Sharma",
        "headline": "Senior Java Developer",
        "location": "Bangalore",
        "yearsOfExperience": 5,
        "primarySkills": ["Java", "Spring Boot"],
        "atsScore": 87,
        "avatarUrl": "...",
        "profileCompletePct": 95
      }
    ],
    "page": 0,
    "totalElements": 42
  }
}
```

---

## Notifications API — `/api/v1/notifications`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/` | Bearer | All | List my notifications |
| PUT | `/{id}/read` | Bearer | All | Mark as read |
| PUT | `/read-all` | Bearer | All | Mark all as read |
| DELETE | `/{id}` | Bearer | All | Delete notification |
| GET | `/unread-count` | Bearer | All | Get unread count |
| GET | `/stream` | Bearer | All | SSE stream |

---

## Analytics API — `/api/v1/analytics`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/recruiter/kpis` | Bearer | RECRUITER | My KPIs |
| GET | `/recruiter/pipeline` | Bearer | RECRUITER | Pipeline funnel data |
| GET | `/hr/overview` | Bearer | HR | Team overview |
| GET | `/admin/platform` | Bearer | ADMIN | Platform-wide metrics |
| GET | `/jobs/{id}/metrics` | Bearer | RECRUITER, HR | Per-job analytics |

---

## Admin API — `/api/v1/admin`

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/users` | Bearer | ADMIN | List all users |
| GET | `/users/{id}` | Bearer | ADMIN | Get user detail |
| PUT | `/users/{id}/status` | Bearer | ADMIN | Activate/suspend user |
| PUT | `/users/{id}/role` | Bearer | ADMIN | Change user role |
| DELETE | `/users/{id}` | Bearer | SUPER_ADMIN | Delete user |
| GET | `/audit-logs` | Bearer | ADMIN | View audit logs |
| GET | `/stats` | Bearer | ADMIN | Platform statistics |

---

## HTTP Status Codes Used

| Code | Meaning |
|------|---------|
| 200 | OK — Successful GET/PUT/DELETE |
| 201 | Created — Successful POST |
| 204 | No Content — Successful DELETE with no body |
| 400 | Bad Request — Validation error |
| 401 | Unauthorized — Missing or invalid token |
| 403 | Forbidden — Insufficient role |
| 404 | Not Found — Resource doesn't exist |
| 409 | Conflict — Duplicate resource (e.g., already applied) |
| 422 | Unprocessable Entity — Business logic error |
| 429 | Too Many Requests — Rate limit exceeded |
| 500 | Internal Server Error |
| 503 | Service Unavailable — AI service down |
