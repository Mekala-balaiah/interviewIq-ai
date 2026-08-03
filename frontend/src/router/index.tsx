import { createBrowserRouter, Navigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';

// Pages
import LoginPage from '@/pages/auth/LoginPage';
import RegisterPage from '@/pages/auth/RegisterPage';
import CandidateDashboard from '@/pages/candidate/DashboardPage';
import CandidateProfile from '@/pages/candidate/ProfilePage';
import RecruiterDashboard from '@/pages/recruiter/DashboardPage';
import JobListPage from '@/pages/recruiter/JobListPage';
import CandidateSearchPage from '@/pages/recruiter/CandidateSearchPage';
import AppShell from '@/components/layout/AppShell';
import type { ReactNode } from 'react';

// ─── Route Guards ─────────────────────────────────────────────────────────────
function RequireAuth({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuthStore();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

function RequireRole({ children, roles }: { children: ReactNode; roles: string[] }) {
  const { isAuthenticated, user } = useAuthStore();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (!user || !roles.includes(user.role)) return <Navigate to="/unauthorized" replace />;
  return <>{children}</>;
}

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    path: '/',
    element: <RequireAuth><AppShell /></RequireAuth>,
    children: [
      // Candidate
      {
        path: 'candidate/dashboard',
        element: (
          <RequireRole roles={['CANDIDATE']}>
            <CandidateDashboard />
          </RequireRole>
        ),
      },
      {
        path: 'candidate/profile',
        element: (
          <RequireRole roles={['CANDIDATE']}>
            <CandidateProfile />
          </RequireRole>
        ),
      },
      // Recruiter
      {
        path: 'recruiter/dashboard',
        element: (
          <RequireRole roles={['RECRUITER', 'HR_MANAGER', 'ADMIN']}>
            <RecruiterDashboard />
          </RequireRole>
        ),
      },
      {
        path: 'recruiter/jobs',
        element: (
          <RequireRole roles={['RECRUITER', 'HR_MANAGER', 'ADMIN']}>
            <JobListPage />
          </RequireRole>
        ),
      },
      {
        path: 'recruiter/search',
        element: (
          <RequireRole roles={['RECRUITER', 'HR_MANAGER', 'ADMIN']}>
            <CandidateSearchPage />
          </RequireRole>
        ),
      },
      // Default redirect
      { index: true, element: <Navigate to="/candidate/dashboard" replace /> },
    ],
  },
  { path: '*', element: <Navigate to="/" replace /> },
]);
