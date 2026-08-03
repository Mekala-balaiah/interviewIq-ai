import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import apiClient from '@/lib/axios';
import type { ApiResponse, RecruiterKpis, PipelineStage } from '@/types';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell
} from 'recharts';
import { Briefcase, Users, Calendar, Award, Clock, TrendingUp, Loader2 } from 'lucide-react';
import { useAuthStore } from '@/store/authStore';

function KpiCard({ icon: Icon, label, value, sub, color }: {
  icon: React.ElementType; label: string; value: string | number; sub?: string; color: string
}) {
  return (
    <motion.div whileHover={{ scale: 1.02 }}
      className="kpi-card hover:border-brand-500/30 transition-all duration-200 group cursor-default">
      <div className={`w-11 h-11 rounded-xl flex items-center justify-center mb-3 ${color}`}>
        <Icon size={20} />
      </div>
      <p className="text-3xl font-bold text-white">{value}</p>
      <p className="text-xs text-slate-400 font-medium">{label}</p>
      {sub && <p className="text-xs text-slate-500 mt-0.5">{sub}</p>}
    </motion.div>
  );
}

const STAGE_COLORS = ['#6366f1', '#8b5cf6', '#a78bfa', '#c4b5fd', '#ddd6fe', '#7c3aed'];

const CustomTooltip = ({ active, payload, label }: any) => {
  if (active && payload && payload.length) {
    return (
      <div className="glass-card px-3 py-2 text-xs">
        <p className="text-slate-300 font-medium">{label}</p>
        <p className="text-brand-400">{payload[0].value} candidates</p>
      </div>
    );
  }
  return null;
};

export default function RecruiterDashboard() {
  const { user } = useAuthStore();

  const { data: kpis, isLoading: kpiLoading } = useQuery({
    queryKey: ['recruiter-kpis'],
    queryFn: async () => {
      const res = await apiClient.get<ApiResponse<RecruiterKpis>>('/analytics/recruiter/kpis');
      return res.data.data;
    },
  });

  const { data: pipeline, isLoading: pipelineLoading } = useQuery({
    queryKey: ['pipeline-funnel'],
    queryFn: async () => {
      const res = await apiClient.get<ApiResponse<PipelineStage[]>>('/analytics/recruiter/pipeline');
      return res.data.data;
    },
  });

  const isLoading = kpiLoading || pipelineLoading;

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-white">
          Recruiter <span className="text-gradient">Analytics</span>
        </h1>
        <p className="text-slate-400 text-sm mt-1">
          Welcome back, {user?.firstName}. Here's your hiring overview.
        </p>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center py-32">
          <Loader2 size={28} className="animate-spin text-brand-400" />
        </div>
      ) : (
        <>
          {/* KPI Grid */}
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4">
            <KpiCard icon={Briefcase} label="Active Jobs"     value={kpis?.totalActiveJobs ?? 0}       color="bg-brand-500/15 text-brand-400" />
            <KpiCard icon={Users}     label="Applications"    value={kpis?.totalApplications ?? 0}      color="bg-violet-500/15 text-violet-400" />
            <KpiCard icon={Calendar}  label="Interviews"      value={kpis?.interviewsScheduled ?? 0}    color="bg-amber-500/15 text-amber-400" />
            <KpiCard icon={Award}     label="Hired (Month)"   value={kpis?.hiredThisMonth ?? 0}         color="bg-emerald-500/15 text-emerald-400" />
            <KpiCard icon={Clock}     label="Avg Time to Hire" value={`${kpis?.avgTimeToHireDays ?? 0}d`} color="bg-sky-500/15 text-sky-400" />
          </div>

          {/* Pipeline Chart */}
          <div className="glass-card p-6">
            <div className="flex items-center gap-2 mb-5">
              <TrendingUp size={17} className="text-brand-400" />
              <h2 className="font-semibold text-white">Application Pipeline</h2>
            </div>
            {pipeline && pipeline.length > 0 ? (
              <ResponsiveContainer width="100%" height={220}>
                <BarChart data={pipeline} margin={{ top: 5, right: 5, bottom: 5, left: -20 }}>
                  <XAxis
                    dataKey="stage"
                    tick={{ fill: '#94a3b8', fontSize: 11 }}
                    tickFormatter={(v) => v.replace('_', ' ')}
                    axisLine={false} tickLine={false}
                  />
                  <YAxis tick={{ fill: '#94a3b8', fontSize: 11 }} axisLine={false} tickLine={false} />
                  <Tooltip content={<CustomTooltip />} cursor={{ fill: 'rgba(99,102,241,0.05)' }} />
                  <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                    {pipeline.map((_, index) => (
                      <Cell key={index} fill={STAGE_COLORS[index % STAGE_COLORS.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="flex items-center justify-center h-48 text-slate-500 text-sm">
                No pipeline data available yet.
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
