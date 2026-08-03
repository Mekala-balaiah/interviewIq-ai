// ─── Auth ────────────────────────────────────────────────────────────────────
export type UserRole = 'CANDIDATE' | 'RECRUITER' | 'HR_MANAGER' | 'ADMIN';

export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  avatarUrl?: string;
}

export interface AuthResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

// ─── API Wrapper ─────────────────────────────────────────────────────────────
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

// ─── Candidate ───────────────────────────────────────────────────────────────
export interface CandidateProfile {
  id: string;
  userId: string;
  headline?: string;
  bio?: string;
  location?: string;
  currentTitle?: string;
  currentCompany?: string;
  yearsOfExperience: number;
  openToRemote: boolean;
  profileComplete: boolean;
  profileCompletionPct: number;
  expectedSalaryMin?: number;
  expectedSalaryMax?: number;
  salaryCurrency: string;
}

export interface CandidateSkill {
  id: string;
  skillName: string;
  proficiencyLevel: string;
  yearsExperience: number;
}

// ─── Jobs ────────────────────────────────────────────────────────────────────
export interface Job {
  id: string;
  title: string;
  description: string;
  location: string;
  workType: string;
  experienceLevel: string;
  salaryMin?: number;
  salaryMax?: number;
  salaryCurrency: string;
  status: 'DRAFT' | 'PENDING_APPROVAL' | 'ACTIVE' | 'PAUSED' | 'CLOSED' | 'ARCHIVED';
  createdAt: string;
  applicationCount?: number;
}

// ─── Applications ────────────────────────────────────────────────────────────
export type ApplicationStatus = 'APPLIED' | 'SCREENING' | 'INTERVIEW_SCHEDULED' | 'INTERVIEWED' | 'OFFER_SENT' | 'HIRED' | 'REJECTED' | 'WITHDRAWN';

export interface Application {
  id: string;
  jobTitle: string;
  companyName: string;
  status: ApplicationStatus;
  appliedAt: string;
  atsScore?: number;
}

// ─── Dashboard Analytics ─────────────────────────────────────────────────────
export interface RecruiterKpis {
  totalActiveJobs: number;
  totalApplications: number;
  interviewsScheduled: number;
  hiredThisMonth: number;
  avgTimeToHireDays: number;
}

export interface PipelineStage {
  stage: string;
  count: number;
}

// ─── Notifications ───────────────────────────────────────────────────────────
export interface Notification {
  id: string;
  type: string;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}

// ─── Elasticsearch Candidate ─────────────────────────────────────────────────
export interface CandidateDocument {
  id: string;
  fullName?: string;
  headline?: string;
  currentTitle?: string;
  location?: string;
  yearsOfExperience?: number;
  openToRemote?: boolean;
  skills?: string[];
  employmentStatus?: string;
}
