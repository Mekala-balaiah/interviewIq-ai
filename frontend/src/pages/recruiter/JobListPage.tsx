import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import apiClient from '@/lib/axios';
import type { ApiResponse, PagedResponse, Job } from '@/types';
import { Briefcase, Plus, MapPin, DollarSign, Clock, Loader2 } from 'lucide-react';

const statusConfig: Record<string, { label: string; cls: string }> = {
  DRAFT:            { label: 'Draft',    cls: 'badge-muted'    },
  PENDING_APPROVAL: { label: 'Pending',  cls: 'badge-warning'  },
  ACTIVE:           { label: 'Active',   cls: 'badge-success'  },
  PAUSED:           { label: 'Paused',   cls: 'badge-warning'  },
  CLOSED:           { label: 'Closed',   cls: 'badge-danger'   },
  ARCHIVED:         { label: 'Archived', cls: 'badge-muted'    },
};

export default function JobListPage() {
  const { data, isLoading } = useQuery({
    queryKey: ['recruiter-jobs'],
    queryFn: async () => {
      const res = await apiClient.get<ApiResponse<PagedResponse<Job>>>(
        '/jobs/my?page=0&size=20&sortBy=createdAt&sortDir=desc'
      );
      return res.data.data;
    },
  });

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">
            My <span className="text-gradient">Jobs</span>
          </h1>
          <p className="text-slate-400 text-sm mt-1">Manage your job postings</p>
        </div>
        <button className="btn-primary">
          <Plus size={16} />
          Post a Job
        </button>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center py-24">
          <Loader2 size={24} className="animate-spin text-brand-400" />
        </div>
      ) : data?.content.length === 0 ? (
        <div className="glass-card flex flex-col items-center justify-center py-24 text-slate-500">
          <Briefcase size={48} className="mb-3 opacity-20" />
          <p className="text-sm">No jobs posted yet. Click "Post a Job" to get started.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {data?.content.map((job, i) => {
            const badge = statusConfig[job.status] ?? { label: job.status, cls: 'badge-muted' };
            return (
              <motion.div
                key={job.id}
                initial={{ opacity: 0, y: 12 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.04 }}
                className="glass-card p-5 flex items-center gap-5 hover:border-brand-500/30 transition-all duration-200 group cursor-pointer"
              >
                {/* Icon */}
                <div className="w-12 h-12 rounded-xl bg-brand-500/15 border border-brand-500/20 flex items-center justify-center flex-shrink-0">
                  <Briefcase size={20} className="text-brand-400" />
                </div>

                {/* Info */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <p className="font-semibold text-slate-100 text-sm truncate">{job.title}</p>
                    <span className={badge.cls}>{badge.label}</span>
                  </div>
                  <div className="flex flex-wrap items-center gap-3 text-xs text-slate-400">
                    {job.location && (
                      <span className="flex items-center gap-1"><MapPin size={11} />{job.location}</span>
                    )}
                    {job.workType && (
                      <span className="flex items-center gap-1"><Clock size={11} />{job.workType}</span>
                    )}
                    {job.salaryMin && (
                      <span className="flex items-center gap-1">
                        <DollarSign size={11} />
                        {job.salaryMin.toLocaleString()} – {job.salaryMax?.toLocaleString()} {job.salaryCurrency}
                      </span>
                    )}
                  </div>
                </div>

                {/* Meta */}
                <div className="text-right flex-shrink-0">
                  <p className="text-xl font-bold text-white">{job.applicationCount ?? 0}</p>
                  <p className="text-xs text-slate-500">applicants</p>
                </div>
              </motion.div>
            );
          })}
        </div>
      )}
    </div>
  );
}
