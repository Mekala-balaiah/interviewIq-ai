import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { useAuthStore } from '@/store/authStore';
import apiClient from '@/lib/axios';
import type { ApiResponse, Application, PagedResponse } from '@/types';
import {
  Briefcase, Clock, Star, ChevronRight,
  TrendingUp, Loader2, AlertCircle, CheckCircle2
} from 'lucide-react';

const statusConfig: Record<string, { label: string; className: string }> = {
  APPLIED:              { label: 'Applied',    className: 'badge-info'    },
  SCREENING:            { label: 'Screening',  className: 'badge-warning' },
  INTERVIEW_SCHEDULED:  { label: 'Interview',  className: 'badge-warning' },
  INTERVIEWED:          { label: 'Interviewed',className: 'badge-info'    },
  OFFER_SENT:           { label: 'Offer!',     className: 'badge-success' },
  HIRED:                { label: 'Hired 🎉',   className: 'badge-success' },
  REJECTED:             { label: 'Rejected',   className: 'badge-danger'  },
  WITHDRAWN:            { label: 'Withdrawn',  className: 'badge-muted'   },
};

function KpiCard({ icon: Icon, label, value, color }: {
  icon: React.ElementType; label: string; value: string | number; color: string
}) {
  return (
    <motion.div
      whileHover={{ scale: 1.02 }}
      className="kpi-card group transition-all duration-200 hover:border-brand-500/30"
    >
      <div className={`w-10 h-10 rounded-xl flex items-center justify-center mb-3 ${color}`}>
        <Icon size={18} />
      </div>
      <p className="text-2xl font-bold text-white">{value}</p>
      <p className="text-xs text-slate-400 font-medium">{label}</p>
    </motion.div>
  );
}

export default function CandidateDashboard() {
  const { user } = useAuthStore();

  const { data: applications, isLoading } = useQuery({
    queryKey: ['my-applications'],
    queryFn: async () => {
      const res = await apiClient.get<ApiResponse<PagedResponse<Application>>>(
        '/applications/my?page=0&size=6&sortBy=appliedAt&sortDir=desc'
      );
      return res.data.data;
    },
  });

  const totalApps = applications?.totalElements ?? 0;
  const active = applications?.content.filter(
    (a) => !['REJECTED', 'WITHDRAWN', 'HIRED'].includes(a.status)
  ).length ?? 0;
  const interviews = applications?.content.filter(
    (a) => a.status === 'INTERVIEW_SCHEDULED' || a.status === 'INTERVIEWED'
  ).length ?? 0;

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Greeting */}
      <div>
        <h1 className="text-2xl font-bold text-white">
          Good morning, <span className="text-gradient">{user?.firstName}</span> 👋
        </h1>
        <p className="text-slate-400 text-sm mt-1">Here's your job search summary</p>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard icon={Briefcase}     label="Total Applications" value={totalApps}  color="bg-brand-500/15 text-brand-400" />
        <KpiCard icon={TrendingUp}    label="Active Pipeline"    value={active}     color="bg-amber-500/15 text-amber-400" />
        <KpiCard icon={Clock}         label="Interviews Pending" value={interviews}  color="bg-violet-500/15 text-violet-400" />
        <KpiCard icon={CheckCircle2}  label="Offers Received"
          value={applications?.content.filter(a => a.status === 'OFFER_SENT').length ?? 0}
          color="bg-emerald-500/15 text-emerald-400" />
      </div>

      {/* Applications List */}
      <div className="glass-card overflow-hidden">
        <div className="flex items-center justify-between px-5 py-4 border-b border-surface-700/50">
          <h2 className="font-semibold text-white">Recent Applications</h2>
          <button className="text-xs text-brand-400 hover:text-brand-300 flex items-center gap-1">
            View all <ChevronRight size={13} />
          </button>
        </div>

        {isLoading ? (
          <div className="flex items-center justify-center py-16">
            <Loader2 size={24} className="animate-spin text-brand-400" />
          </div>
        ) : applications?.content.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-16 text-slate-500">
            <Briefcase size={40} className="mb-3 opacity-30" />
            <p className="text-sm">No applications yet. Start applying!</p>
          </div>
        ) : (
          <div className="divide-y divide-surface-700/40">
            {applications?.content.map((app, i) => {
              const badge = statusConfig[app.status] ?? { label: app.status, className: 'badge-muted' };
              return (
                <motion.div
                  key={app.id}
                  initial={{ opacity: 0, x: -8 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: i * 0.05 }}
                  className="flex items-center gap-4 px-5 py-4 hover:bg-surface-800/50 transition-colors group"
                >
                  <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-brand-500/20 to-violet-500/10 border border-brand-500/20 flex items-center justify-center flex-shrink-0">
                    <Briefcase size={16} className="text-brand-400" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-medium text-slate-100 text-sm truncate">{app.jobTitle}</p>
                    <p className="text-xs text-slate-400 truncate">{app.companyName}</p>
                  </div>
                  {app.atsScore !== undefined && (
                    <div className="flex items-center gap-1 text-amber-400 text-xs">
                      <Star size={12} fill="currentColor" />
                      <span>{app.atsScore}%</span>
                    </div>
                  )}
                  <span className={badge.className}>{badge.label}</span>
                  <ChevronRight size={14} className="text-slate-600 group-hover:text-slate-400 transition-colors" />
                </motion.div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
