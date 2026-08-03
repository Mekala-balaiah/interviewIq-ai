import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useMutation } from '@tanstack/react-query';
import { useAuthStore } from '@/store/authStore';
import apiClient from '@/lib/axios';
import type { ApiResponse, AuthResponse, UserRole } from '@/types';
import { Zap, Mail, Lock, User, AlertCircle, Loader2 } from 'lucide-react';

const schema = z.object({
  firstName: z.string().min(1, 'First name is required'),
  lastName:  z.string().min(1, 'Last name is required'),
  email:     z.string().email('Please enter a valid email'),
  password:  z.string().min(8, 'Password must be at least 8 characters'),
  role:      z.enum(['CANDIDATE', 'RECRUITER'] as const),
});
type FormValues = z.infer<typeof schema>;

const roles: { value: UserRole; label: string; description: string }[] = [
  { value: 'CANDIDATE', label: 'Candidate', description: 'Find your dream job with AI assistance' },
  { value: 'RECRUITER', label: 'Recruiter', description: 'Hire top talent smarter and faster' },
];

export default function RegisterPage() {
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const { register, handleSubmit, watch, setValue, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { role: 'CANDIDATE' },
  });

  const selectedRole = watch('role');

  const mutation = useMutation({
    mutationFn: async (data: FormValues) => {
      const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/register', data);
      return res.data.data;
    },
    onSuccess: (data) => {
      login(data.user, data.accessToken, data.refreshToken);
      navigate(data.user.role === 'CANDIDATE' ? '/candidate/dashboard' : '/recruiter/dashboard');
    },
  });

  return (
    <div className="min-h-screen flex items-center justify-center p-4"
      style={{ backgroundImage: 'radial-gradient(ellipse at 50% 0%, rgba(99,102,241,0.12) 0%, transparent 60%)' }}>
      <motion.div
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="w-full max-w-lg"
      >
        {/* Logo */}
        <div className="flex flex-col items-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-brand-500 to-violet-600 flex items-center justify-center mb-4 shadow-[0_0_30px_rgba(99,102,241,0.4)]">
            <Zap size={28} className="text-white" />
          </div>
          <h1 className="text-2xl font-bold text-white">Create your account</h1>
          <p className="text-slate-400 text-sm mt-1">Join InterviewIQ AI today</p>
        </div>

        <div className="glass-card p-8 shadow-2xl">
          {/* Role Selector */}
          <div className="grid grid-cols-2 gap-3 mb-6">
            {roles.map(({ value, label, description }) => (
              <button
                key={value}
                type="button"
                onClick={() => setValue('role', value)}
                className={`p-4 rounded-xl border text-left transition-all duration-150 ${
                  selectedRole === value
                    ? 'border-brand-500 bg-brand-500/10 text-brand-300'
                    : 'border-surface-700 hover:border-surface-600 text-slate-400'
                }`}
              >
                <p className="font-semibold text-sm">{label}</p>
                <p className="text-xs mt-0.5 opacity-70">{description}</p>
              </button>
            ))}
          </div>

          {mutation.isError && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}
              className="flex items-center gap-2 mb-5 px-4 py-3 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-sm">
              <AlertCircle size={15} />
              Registration failed. This email may already be in use.
            </motion.div>
          )}

          <form onSubmit={handleSubmit((d) => mutation.mutate(d))} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">First Name</label>
                <div className="relative">
                  <User size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
                  <input {...register('firstName')} placeholder="John" className="input pl-9" />
                </div>
                {errors.firstName && <p className="text-red-400 text-xs mt-1">{errors.firstName.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Last Name</label>
                <input {...register('lastName')} placeholder="Doe" className="input" />
                {errors.lastName && <p className="text-red-400 text-xs mt-1">{errors.lastName.message}</p>}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">Email</label>
              <div className="relative">
                <Mail size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
                <input {...register('email')} type="email" placeholder="you@example.com" className="input pl-9" />
              </div>
              {errors.email && <p className="text-red-400 text-xs mt-1">{errors.email.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">Password</label>
              <div className="relative">
                <Lock size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
                <input {...register('password')} type="password" placeholder="Min. 8 characters" className="input pl-9" />
              </div>
              {errors.password && <p className="text-red-400 text-xs mt-1">{errors.password.message}</p>}
            </div>

            <button type="submit" disabled={mutation.isPending} className="btn-primary w-full mt-2">
              {mutation.isPending ? <Loader2 size={16} className="animate-spin" /> : null}
              {mutation.isPending ? 'Creating account…' : 'Create Account'}
            </button>
          </form>

          <p className="text-center text-sm text-slate-400 mt-6">
            Already have an account?{' '}
            <Link to="/login" className="text-brand-400 hover:text-brand-300 font-medium">Sign in</Link>
          </p>
        </div>
      </motion.div>
    </div>
  );
}
