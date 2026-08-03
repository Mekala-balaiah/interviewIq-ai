import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion, AnimatePresence } from 'framer-motion';
import apiClient from '@/lib/axios';
import type { ApiResponse, PagedResponse, CandidateDocument } from '@/types';
import { Search, Filter, MapPin, Clock, Wifi, Star, Loader2, Users } from 'lucide-react';

const COMMON_SKILLS = ['Java', 'Python', 'React', 'Spring Boot', 'TypeScript', 'Node.js', 'AWS', 'Docker', 'Kubernetes', 'SQL'];

export default function CandidateSearchPage() {
  const [keyword,       setKeyword]       = useState('');
  const [location,      setLocation]      = useState('');
  const [minExp,        setMinExp]        = useState('');
  const [selectedSkills, setSelectedSkills] = useState<string[]>([]);
  const [openToRemote,  setOpenToRemote]  = useState<boolean | undefined>(undefined);
  const [submitted,     setSubmitted]     = useState(false);

  const { data, isLoading, isFetching } = useQuery({
    queryKey: ['candidate-search', keyword, location, minExp, selectedSkills, openToRemote],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (keyword)             params.set('keyword', keyword);
      if (location)            params.set('location', location);
      if (minExp)              params.set('minExperience', minExp);
      if (openToRemote !== undefined) params.set('openToRemote', String(openToRemote));
      selectedSkills.forEach((s) => params.append('skills', s));
      params.set('page', '0');
      params.set('size', '20');
      const res = await apiClient.get<ApiResponse<PagedResponse<CandidateDocument>>>(
        `/candidates/search?${params}`
      );
      return res.data.data;
    },
    enabled: submitted,
  });

  const toggleSkill = (s: string) =>
    setSelectedSkills((prev) => prev.includes(s) ? prev.filter((x) => x !== s) : [...prev, s]);

  const handleSearch = () => setSubmitted(true);

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold text-white">
          Find <span className="text-gradient">Talent</span>
        </h1>
        <p className="text-slate-400 text-sm mt-1">Powered by Elasticsearch — search across 1,000s of candidates</p>
      </div>

      {/* Search Panel */}
      <div className="glass-card p-5 space-y-4">
        {/* Keyword + Location */}
        <div className="flex gap-3">
          <div className="relative flex-1">
            <Search size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
            <input
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              placeholder="e.g. Senior Java Engineer"
              className="input pl-10"
            />
          </div>
          <div className="relative w-52">
            <MapPin size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" />
            <input
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              placeholder="Location"
              className="input pl-10"
            />
          </div>
          <div className="w-36">
            <select
              value={minExp}
              onChange={(e) => setMinExp(e.target.value)}
              className="input"
            >
              <option value="">Any exp.</option>
              {[1, 2, 3, 5, 7, 10].map((y) => (
                <option key={y} value={y}>{y}+ years</option>
              ))}
            </select>
          </div>
        </div>

        {/* Skill Pills */}
        <div>
          <p className="text-xs text-slate-400 mb-2 font-medium flex items-center gap-1.5">
            <Filter size={12} /> Filter by skill
          </p>
          <div className="flex flex-wrap gap-2">
            {COMMON_SKILLS.map((s) => (
              <button
                key={s}
                onClick={() => toggleSkill(s)}
                className={`px-3 py-1 rounded-full text-xs font-medium border transition-all duration-150 ${
                  selectedSkills.includes(s)
                    ? 'bg-brand-600/30 border-brand-500 text-brand-300'
                    : 'bg-surface-800 border-surface-700 text-slate-400 hover:border-brand-500/50 hover:text-slate-200'
                }`}
              >
                {s}
              </button>
            ))}
          </div>
        </div>

        {/* Remote + Search Button */}
        <div className="flex items-center gap-4">
          <label className="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              checked={openToRemote === true}
              onChange={(e) => setOpenToRemote(e.target.checked ? true : undefined)}
              className="w-4 h-4 rounded accent-brand-500"
            />
            <span className="text-sm text-slate-300 flex items-center gap-1.5">
              <Wifi size={13} className="text-brand-400" /> Open to Remote
            </span>
          </label>
          <button onClick={handleSearch} className="btn-primary ml-auto">
            <Search size={15} />
            Search Candidates
          </button>
        </div>
      </div>

      {/* Results */}
      {(isLoading || isFetching) && (
        <div className="flex items-center justify-center py-16">
          <Loader2 size={24} className="animate-spin text-brand-400" />
        </div>
      )}

      {submitted && data && !isLoading && (
        <>
          <p className="text-sm text-slate-400">
            Found <span className="text-white font-semibold">{data.totalElements}</span> candidates
          </p>
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            <AnimatePresence>
              {data.content.map((candidate, i) => (
                <motion.div
                  key={candidate.id}
                  initial={{ opacity: 0, y: 16 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: i * 0.04 }}
                  className="glass-card p-5 hover:border-brand-500/30 transition-all duration-200 group cursor-pointer"
                >
                  <div className="flex items-center gap-3 mb-3">
                    <div className="w-10 h-10 rounded-full bg-gradient-to-br from-brand-500 to-violet-600 flex items-center justify-center text-sm font-bold text-white">
                      {(candidate.fullName ?? '?').charAt(0)}
                    </div>
                    <div>
                      <p className="font-semibold text-sm text-slate-100">
                        {candidate.fullName ?? 'Anonymous'}
                      </p>
                      <p className="text-xs text-slate-400">{candidate.currentTitle ?? '—'}</p>
                    </div>
                  </div>

                  <div className="flex flex-wrap gap-1.5 mb-3">
                    {candidate.location && (
                      <span className="badge-muted">
                        <MapPin size={10} className="mr-0.5" />{candidate.location}
                      </span>
                    )}
                    {candidate.yearsOfExperience !== undefined && (
                      <span className="badge-muted">
                        <Clock size={10} className="mr-0.5" />{candidate.yearsOfExperience}y exp
                      </span>
                    )}
                    {candidate.openToRemote && <span className="badge-success">Remote</span>}
                  </div>

                  {candidate.skills && candidate.skills.length > 0 && (
                    <div className="flex flex-wrap gap-1.5">
                      {candidate.skills.slice(0, 5).map((skill) => (
                        <span key={skill} className="badge-info">{skill}</span>
                      ))}
                      {candidate.skills.length > 5 && (
                        <span className="badge-muted">+{candidate.skills.length - 5}</span>
                      )}
                    </div>
                  )}
                </motion.div>
              ))}
            </AnimatePresence>
          </div>

          {data.content.length === 0 && (
            <div className="flex flex-col items-center justify-center py-20 text-slate-500">
              <Users size={48} className="mb-3 opacity-20" />
              <p className="text-sm">No candidates match your search. Try adjusting your filters.</p>
            </div>
          )}
        </>
      )}
    </div>
  );
}
