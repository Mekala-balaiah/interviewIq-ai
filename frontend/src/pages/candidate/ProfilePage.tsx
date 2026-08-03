import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { useAuthStore } from '@/store/authStore';
import apiClient from '@/lib/axios';
import type { ApiResponse, CandidateProfile as IProfile } from '@/types';
import { User, MapPin, Briefcase, Building, Globe, Github, Linkedin, Loader2, CheckCircle2, Edit3 } from 'lucide-react';

const schema = z.object({
  headline:         z.string().optional(),
  bio:              z.string().optional(),
  location:         z.string().optional(),
  currentTitle:     z.string().optional(),
  currentCompany:   z.string().optional(),
  yearsOfExperience: z.coerce.number().min(0).max(50).optional(),
  openToRemote:     z.boolean().optional(),
  linkedinUrl:      z.string().url().optional().or(z.literal('')),
  githubUrl:        z.string().url().optional().or(z.literal('')),
});
type FormValues = z.infer<typeof schema>;

export default function CandidateProfilePage() {
  const { user } = useAuthStore();
  const queryClient = useQueryClient();

  const { data: profile, isLoading } = useQuery({
    queryKey: ['my-profile'],
    queryFn: async () => {
      const res = await apiClient.get<ApiResponse<IProfile>>('/candidates/me');
      return res.data.data;
    },
  });

  const { register, handleSubmit, formState: { errors, isDirty } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    values: profile as FormValues,
  });

  const mutation = useMutation({
    mutationFn: async (data: FormValues) => {
      const res = await apiClient.put<ApiResponse<IProfile>>('/candidates/me', data);
      return res.data.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-profile'] });
    },
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-32">
        <Loader2 size={28} className="animate-spin text-brand-400" />
      </div>
    );
  }

  const completion = profile?.profileCompletionPct ?? 0;
  const completionColor = completion >= 80 ? '#10b981' : completion >= 50 ? '#f59e0b' : '#6366f1';

  return (
    <div className="space-y-6 animate-fade-in max-w-3xl">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">My <span className="text-gradient">Profile</span></h1>
          <p className="text-slate-400 text-sm mt-1">Keep your profile up to date to improve job matches</p>
        </div>
        {/* Completion Ring */}
        <div className="glass-card px-5 py-3 flex items-center gap-3">
          <div className="relative w-12 h-12">
            <svg className="w-12 h-12 -rotate-90" viewBox="0 0 36 36">
              <circle cx="18" cy="18" r="15.9" fill="none" stroke="#1e293b" strokeWidth="2.5" />
              <circle cx="18" cy="18" r="15.9" fill="none"
                stroke={completionColor} strokeWidth="2.5"
                strokeDasharray={`${completion} ${100 - completion}`}
                strokeLinecap="round" />
            </svg>
            <span className="absolute inset-0 flex items-center justify-center text-xs font-bold text-white">
              {completion}%
            </span>
          </div>
          <div>
            <p className="text-xs text-slate-400">Profile</p>
            <p className="text-sm font-semibold text-white">Completion</p>
          </div>
        </div>
      </div>

      {mutation.isSuccess && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}
          className="flex items-center gap-2 px-4 py-3 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-sm">
          <CheckCircle2 size={15} /> Profile updated successfully!
        </motion.div>
      )}

      <form onSubmit={handleSubmit((d) => mutation.mutate(d))} className="glass-card p-6 space-y-5">
        {/* Avatar + Name (read-only) */}
        <div className="flex items-center gap-4 pb-4 border-b border-surface-700/50">
          <div className="w-16 h-16 rounded-full bg-gradient-to-br from-brand-500 to-violet-600 flex items-center justify-center text-xl font-bold text-white">
            {user?.firstName?.[0]}{user?.lastName?.[0]}
          </div>
          <div>
            <p className="text-lg font-bold text-white">{user?.firstName} {user?.lastName}</p>
            <p className="text-sm text-slate-400">{user?.email}</p>
            <span className="badge-info mt-1">{user?.role}</span>
          </div>
        </div>

        {/* Fields Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2">
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wide">Headline</label>
            <div className="relative">
              <Edit3 size={13} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
              <input {...register('headline')} placeholder="e.g. Senior Java Engineer at Google" className="input pl-9" />
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wide">Current Title</label>
            <div className="relative">
              <Briefcase size={13} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
              <input {...register('currentTitle')} placeholder="Software Engineer" className="input pl-9" />
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wide">Current Company</label>
            <div className="relative">
              <Building size={13} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
              <input {...register('currentCompany')} placeholder="Acme Corp" className="input pl-9" />
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wide">Location</label>
            <div className="relative">
              <MapPin size={13} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
              <input {...register('location')} placeholder="San Francisco, CA" className="input pl-9" />
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wide">Years of Experience</label>
            <input {...register('yearsOfExperience')} type="number" min={0} max={50} placeholder="5" className="input" />
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wide">LinkedIn URL</label>
            <div className="relative">
              <Linkedin size={13} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
              <input {...register('linkedinUrl')} placeholder="https://linkedin.com/in/..." className="input pl-9" />
            </div>
            {errors.linkedinUrl && <p className="text-red-400 text-xs mt-1">{errors.linkedinUrl.message}</p>}
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wide">GitHub URL</label>
            <div className="relative">
              <Github size={13} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
              <input {...register('githubUrl')} placeholder="https://github.com/..." className="input pl-9" />
            </div>
            {errors.githubUrl && <p className="text-red-400 text-xs mt-1">{errors.githubUrl.message}</p>}
          </div>

          <div className="md:col-span-2">
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wide">Bio</label>
            <textarea {...register('bio')} rows={4} placeholder="Tell recruiters about yourself..." className="input resize-none" />
          </div>

          <div className="md:col-span-2 flex items-center gap-3">
            <input {...register('openToRemote')} type="checkbox" className="w-4 h-4 rounded accent-brand-500" id="remote" />
            <label htmlFor="remote" className="text-sm text-slate-300">I'm open to remote work</label>
          </div>
        </div>

        <div className="pt-2">
          <button type="submit" disabled={mutation.isPending || !isDirty} className="btn-primary">
            {mutation.isPending && <Loader2 size={15} className="animate-spin" />}
            {mutation.isPending ? 'Saving…' : 'Save Changes'}
          </button>
        </div>
      </form>
    </div>
  );
}
