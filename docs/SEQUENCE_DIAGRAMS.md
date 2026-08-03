# Sequence Diagrams — InterviewIQ AI

## SD-01: User Registration & Email Verification

```
Client          AuthController      AuthService       UserRepo        EmailService
  │                   │                  │                 │                │
  │ POST /register    │                  │                 │                │
  │──────────────────>│                  │                 │                │
  │                   │ register(req)    │                 │                │
  │                   │─────────────────>│                 │                │
  │                   │                  │ checkEmail()    │                │
  │                   │                  │────────────────>│                │
  │                   │                  │ email unique ✓  │                │
  │                   │                  │<────────────────│                │
  │                   │                  │ bcrypt password │                │
  │                   │                  │ save user       │                │
  │                   │                  │────────────────>│                │
  │                   │                  │ user saved      │                │
  │                   │                  │<────────────────│                │
  │                   │                  │ generate OTP    │                │
  │                   │                  │ save OTP        │                │
  │                   │                  │ sendOtpEmail()  │                │
  │                   │                  │────────────────────────────────>│
  │                   │                  │                 │                │ send email
  │                   │ UserResponse     │                 │                │
  │ 201 Created       │<─────────────────│                 │                │
  │<──────────────────│                  │                 │                │
  │                   │                  │                 │                │
  │ POST /verify-email │                 │                 │                │
  │──────────────────>│                  │                 │                │
  │                   │ verifyOtp(otp)   │                 │                │
  │                   │─────────────────>│                 │                │
  │                   │                  │ findOtp()       │                │
  │                   │                  │ validate expiry │                │
  │                   │                  │ markUsed()      │                │
  │                   │                  │ updateUser(     │                │
  │                   │                  │  emailVerified) │                │
  │                   │                  │────────────────>│                │
  │ 200 OK            │                  │                 │                │
  │<──────────────────│                  │                 │                │
```

---

## SD-02: JWT Login & Token Refresh

```
Client          AuthController      AuthService       UserRepo        RedisService
  │                   │                  │                 │                │
  │ POST /login       │                  │                 │                │
  │──────────────────>│                  │                 │                │
  │                   │ login(req)       │                 │                │
  │                   │─────────────────>│                 │                │
  │                   │                  │ findByEmail()   │                │
  │                   │                  │────────────────>│                │
  │                   │                  │ user            │                │
  │                   │                  │<────────────────│                │
  │                   │                  │ bcrypt.verify() │                │
  │                   │                  │ generateAccess  │                │
  │                   │                  │   Token (15min) │                │
  │                   │                  │ generateRefresh │                │
  │                   │                  │   Token (7days) │                │
  │                   │                  │ saveRefreshToken│                │
  │                   │                  │ cacheSession()  │                │
  │                   │                  │────────────────────────────────>│
  │                   │ TokenResponse    │                 │                │
  │ 200 + tokens      │<─────────────────│                 │                │
  │<──────────────────│                  │                 │                │
  │                   │                  │                 │                │
  │─ ─ ─ 15 min later ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─│
  │                   │                  │                 │                │
  │ POST /refresh     │                  │                 │                │
  │ {refreshToken}    │                  │                 │                │
  │──────────────────>│                  │                 │                │
  │                   │ refresh(token)   │                 │                │
  │                   │─────────────────>│                 │                │
  │                   │                  │ findToken()     │                │
  │                   │                  │ validateExpiry  │                │
  │                   │                  │ validateRevoked │                │
  │                   │                  │ rotateToken()   │                │
  │                   │                  │ generateNew     │                │
  │                   │                  │   AccessToken   │                │
  │                   │ NewTokenResponse │                 │                │
  │ 200 + new tokens  │<─────────────────│                 │                │
  │<──────────────────│                  │                 │                │
```

---

## SD-03: Resume Upload → AI Parsing → ATS Score

```
Client      ResumeCtrl     ResumeService    FileStorage     KafkaProducer   AIResumeService     ResumeRepo
  │              │               │               │                │                │                 │
  │ POST /upload │               │               │                │                │                 │
  │ (multipart)  │               │               │                │                │                 │
  │─────────────>│               │               │                │                │                 │
  │              │ uploadResume()│               │                │                │                 │
  │              │──────────────>│               │                │                │                 │
  │              │               │ validateFile()│                │                │                 │
  │              │               │ store(file)   │                │                │                 │
  │              │               │──────────────>│                │                │                 │
  │              │               │ fileUrl       │                │                │                 │
  │              │               │<──────────────│                │                │                 │
  │              │               │ saveResume()  │                │                │                 │
  │              │               │──────────────────────────────────────────────────────────────────>│
  │              │               │ resume{id}    │                │                │                 │
  │              │               │<──────────────────────────────────────────────────────────────────│
  │              │               │ publishEvent()│                │                │                 │
  │              │               │  RESUME_UPLOADED              │                │                 │
  │              │               │──────────────────────────────>│                │                 │
  │ 201 {id}     │               │               │                │                │                 │
  │<─────────────│               │               │                │                │                 │
  │              │               │               │                │                │                 │
  │ ─ ─ ─ Kafka Consumer picks up event ─ ─ ─ ─ ─│                │                │                 │
  │              │               │               │                │                │                 │
  │              │               │               │                │ consumeEvent() │                 │
  │              │               │               │                │ parseResume()  │                 │
  │              │               │               │                │──────────────>│                  │
  │              │               │               │                │                │ readFile(url)   │
  │              │               │               │                │                │────────────────>│
  │              │               │               │                │                │                 │
  │              │               │               │                │                │ LLM.extract()   │
  │              │               │               │                │                │ (Spring AI)     │
  │              │               │               │                │                │                 │
  │              │               │               │                │                │ saveAnalysis()  │
  │              │               │               │                │                │────────────────>│
  │              │               │               │                │                │                 │
  │              │               │               │                │ updateStatus() │                 │
  │              │               │               │                │  (COMPLETED)   │                 │
  │              │               │               │                │                │                 │
  │              │               │               │                │ publishEvent() │                 │
  │              │               │               │                │  NOTIFY_USER   │                 │
```

---

## SD-04: AI Interview Session Flow

```
Client      InterviewCtrl     InterviewService      AIService         InterviewRepo
  │               │                  │                   │                  │
  │ POST /start   │                  │                   │                  │
  │──────────────>│                  │                   │                  │
  │               │ startInterview() │                   │                  │
  │               │─────────────────>│                   │                  │
  │               │                  │ validateCandidate │                  │
  │               │                  │ loadInterview()   │                  │
  │               │                  │──────────────────────────────────────>│
  │               │                  │ generateQuestions │                  │
  │               │                  │──────────────────>│                  │
  │               │                  │                   │ LLM.generate()   │
  │               │                  │                   │ (LangChain4j)    │
  │               │                  │                   │ questions[]      │
  │               │                  │ saveQuestions()   │                  │
  │               │                  │──────────────────────────────────────>│
  │               │                  │ updateStatus(     │                  │
  │               │                  │  IN_PROGRESS)     │                  │
  │               │ {questions[]}    │                   │                  │
  │ 200 questions │<─────────────────│                   │                  │
  │<──────────────│                  │                   │                  │
  │               │                  │                   │                  │
  │ ─ ─ ─ Candidate answers each question ─ ─ ─ ─ ─ ─ ─│                  │
  │               │                  │                   │                  │
  │ POST /submit  │                  │                   │                  │
  │  -response    │                  │                   │                  │
  │──────────────>│                  │                   │                  │
  │               │ submitResponse() │                   │                  │
  │               │─────────────────>│                   │                  │
  │               │                  │ evaluateAnswer()  │                  │
  │               │                  │──────────────────>│                  │
  │               │                  │                   │ LLM.evaluate()   │
  │               │                  │                   │ {score, feedback}│
  │               │                  │ saveResponse()    │                  │
  │               │ {score, feedback}│                   │                  │
  │<──────────────│                  │                   │                  │
  │               │                  │                   │                  │
  │ POST /complete│                  │                   │                  │
  │──────────────>│                  │                   │                  │
  │               │ completeInterview│                   │                  │
  │               │─────────────────>│                   │                  │
  │               │                  │ aggregateScores() │                  │
  │               │                  │ generateFeedback()│                  │
  │               │                  │──────────────────>│                  │
  │               │                  │                   │ LLM.summarize()  │
  │               │                  │                   │ overallFeedback  │
  │               │                  │ generateSummary() │                  │
  │               │                  │ saveInterview(    │                  │
  │               │                  │  COMPLETED)       │                  │
  │               │                  │ notifyRecruiter() │                  │
  │ 200 summary   │                  │                   │                  │
  │<──────────────│                  │                   │                  │
```

---

## SD-05: Candidate Job Application + Ranking

```
Client     ApplicationCtrl    ApplicationService    ATSService     NotificationSvc
  │               │                  │                   │                │
  │ POST /apply   │                  │                   │                │
  │──────────────>│                  │                   │                │
  │               │ applyForJob()    │                   │                │
  │               │─────────────────>│                   │                │
  │               │                  │ checkDuplicate()  │                │
  │               │                  │ validateJobStatus │                │
  │               │                  │ createApplication │                │
  │               │                  │ computeAtsScore() │                │
  │               │                  │──────────────────>│                │
  │               │                  │                   │ scoreResume()  │
  │               │                  │                   │ vs jobSkills   │
  │               │                  │                   │ atsScore: 84   │
  │               │                  │ updateAtsScore()  │                │
  │               │                  │ updateJobCount()  │                │
  │               │                  │ notifyRecruiter() │                │
  │               │                  │──────────────────────────────────>│
  │               │                  │                   │                │ publishKafka
  │               │                  │                   │                │ EMAIL_EVENT
  │ 201 Applied   │                  │                   │                │
  │<──────────────│                  │                   │                │
  │               │                  │                   │                │
  │  Recruiter views ranked candidates                   │                │
  │               │                  │                   │                │
  │ GET /jobs/{id}│                  │                   │                │
  │ /candidates   │                  │                   │                │
  │ /ranked       │                  │                   │                │
  │──────────────>│                  │                   │                │
  │               │ getRankedList()  │                   │                │
  │               │─────────────────>│                   │                │
  │               │                  │ checkRedisCache() │                │
  │               │                  │ (cache miss)      │                │
  │               │                  │ fetchApplications │                │
  │               │                  │ rankByAtsScore()  │                │
  │               │                  │ cacheResult(15min)│                │
  │               │ rankedList[]     │                   │                │
  │<──────────────│                  │                   │                │
```

---

## SD-06: Real-Time Notifications (SSE)

```
Client          NotificationCtrl    NotificationService    KafkaConsumer    Redis
  │                    │                    │                     │            │
  │ GET /stream        │                    │                     │            │
  │ Accept: text/event │                    │                     │            │
  │───────────────────>│                    │                     │            │
  │                    │ subscribe(userId)  │                     │            │
  │                    │───────────────────>│                     │            │
  │                    │                    │ createSseEmitter()  │            │
  │                    │ SseEmitter         │                     │            │
  │<───────────────────│                    │                     │            │
  │ (connection kept)  │                    │                     │            │
  │                    │                    │                     │            │
  │ ─ ─ ─ Event happens (e.g., interview scheduled) ─ ─ ─ ─ ─ ─ │            │
  │                    │                    │                     │            │
  │                    │                    │              consumeEvent()      │
  │                    │                    │              createNotification()│
  │                    │                    │<────────────────────│            │
  │                    │                    │ saveNotification()  │            │
  │                    │                    │ getEmitter(userId)  │            │
  │                    │                    │ emitter.send()      │            │
  │ event: notification│                    │                     │            │
  │ data: {...}        │                    │                     │            │
  │<───────────────────│                    │                     │            │
```
