import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { motion } from 'framer-motion';
import {
  LayoutDashboard, User, Briefcase, Search,
  Bell, LogOut, ChevronRight, Zap
} from 'lucide-react';

const candidateNav = [
  { to: '/candidate/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/candidate/profile',   icon: User,            label: 'My Profile'  },
];

const recruiterNav = [
  { to: '/recruiter/dashboard', icon: LayoutDashboard, label: 'Dashboard'  },
  { to: '/recruiter/jobs',      icon: Briefcase,       label: 'Jobs'        },
  { to: '/recruiter/search',    icon: Search,          label: 'Find Talent' },
];

export default function AppShell() {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const nav = user?.role === 'CANDIDATE' ? candidateNav : recruiterNav;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="flex h-screen overflow-hidden">
      {/* ── Sidebar ─────────────────────────────────────────────── */}
      <aside className="w-64 flex-shrink-0 flex flex-col border-r border-surface-700/50 bg-surface-900/80 backdrop-blur-xl">
        {/* Logo */}
        <div className="flex items-center gap-2.5 px-5 py-5 border-b border-surface-700/50">
          <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-brand-500 to-violet-600 flex items-center justify-center shadow-[0_0_15px_rgba(99,102,241,0.4)]">
            <Zap size={16} className="text-white" />
          </div>
          <span className="font-bold text-lg text-gradient">InterviewIQ</span>
        </div>

        {/* Nav Items */}
        <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          {nav.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `group flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150 ${
                  isActive
                    ? 'bg-brand-600/20 text-brand-300 border border-brand-500/30'
                    : 'text-slate-400 hover:text-slate-100 hover:bg-surface-800'
                }`
              }
            >
              <Icon size={17} />
              <span className="flex-1">{label}</span>
              <ChevronRight size={13} className="opacity-0 group-hover:opacity-50 -translate-x-1 group-hover:translate-x-0 transition-all" />
            </NavLink>
          ))}
        </nav>

        {/* User Footer */}
        <div className="px-3 py-4 border-t border-surface-700/50">
          <div className="flex items-center gap-3 px-3 py-2.5 rounded-xl hover:bg-surface-800 transition-colors group cursor-pointer">
            <div className="w-8 h-8 rounded-full bg-gradient-to-br from-brand-500 to-violet-600 flex items-center justify-center text-xs font-bold text-white">
              {user?.firstName?.[0]}{user?.lastName?.[0]}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-slate-200 truncate">{user?.firstName} {user?.lastName}</p>
              <p className="text-xs text-slate-500 truncate">{user?.role}</p>
            </div>
            <button onClick={handleLogout} title="Logout"
              className="p-1 rounded-lg text-slate-500 hover:text-red-400 hover:bg-red-500/10 transition-colors">
              <LogOut size={15} />
            </button>
          </div>
        </div>
      </aside>

      {/* ── Main Content ─────────────────────────────────────────── */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top Bar */}
        <header className="flex items-center justify-end gap-3 px-6 py-4 border-b border-surface-700/50 bg-surface-900/50 backdrop-blur-sm">
          <button className="relative p-2 rounded-xl text-slate-400 hover:text-slate-100 hover:bg-surface-800 transition-colors">
            <Bell size={18} />
            <span className="absolute top-1.5 right-1.5 w-1.5 h-1.5 bg-brand-500 rounded-full ring-2 ring-surface-900" />
          </button>
          <div className="w-8 h-8 rounded-full bg-gradient-to-br from-brand-500 to-violet-600 flex items-center justify-center text-xs font-bold text-white">
            {user?.firstName?.[0]}{user?.lastName?.[0]}
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-y-auto p-6">
          <motion.div
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
          >
            <Outlet />
          </motion.div>
        </main>
      </div>
    </div>
  );
}
