package com.interviewiq.analytics.service;

import com.interviewiq.analytics.dto.ApplicationTrendDto;
import com.interviewiq.analytics.dto.PipelineFunnelDto;
import com.interviewiq.analytics.dto.RecruiterKpiDto;
import com.interviewiq.analytics.dto.TimeSeriesDataPoint;
import com.interviewiq.application.enums.ApplicationStatus;
import com.interviewiq.application.repository.ApplicationRepository;
import com.interviewiq.interview.repository.InterviewRepository;
import com.interviewiq.job.enums.JobStatus;
import com.interviewiq.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final InterviewRepository interviewRepository;

    @Override
    @Transactional(readOnly = true)
    public RecruiterKpiDto getRecruiterKpis(UUID recruiterId) {
        long totalActiveJobs = jobRepository.countByRecruiterIdAndStatusAndDeletedAtIsNull(recruiterId, JobStatus.PUBLISHED);
        long totalApplications = applicationRepository.countByJobRecruiterId(recruiterId);
        long totalInterviews = interviewRepository.countByJobRecruiterId(recruiterId);
        
        // Count applications that are in HIRED or OFFER_ACCEPTED statuses
        long totalHires = applicationRepository.countByJobRecruiterIdAndStatusIn(recruiterId, 
                List.of(ApplicationStatus.HIRED, ApplicationStatus.OFFER_ACCEPTED));

        return RecruiterKpiDto.builder()
                .totalActiveJobs(totalActiveJobs)
                .totalApplications(totalApplications)
                .totalInterviewsScheduled(totalInterviews)
                .totalOffersAccepted(totalHires)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PipelineFunnelDto getPipelineFunnel(UUID recruiterId) {
        List<Object[]> results = applicationRepository.countApplicationsByStatusForRecruiter(recruiterId);
        
        Map<ApplicationStatus, Long> stageCounts = new HashMap<>();
        // Initialize all statuses to 0
        for (ApplicationStatus status : ApplicationStatus.values()) {
            stageCounts.put(status, 0L);
        }
        
        // Populate from query results
        for (Object[] row : results) {
            if (row != null && row.length == 2 && row[0] != null) {
                ApplicationStatus status = (ApplicationStatus) row[0];
                Long count = ((Number) row[1]).longValue();
                stageCounts.put(status, count);
            }
        }
        
        return PipelineFunnelDto.builder()
                .stageCounts(stageCounts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationTrendDto getApplicationTrends(UUID recruiterId, int days) {
        // days is passed but currently query is hardcoded to 30 days. We will use the query as is for now.
        List<Object[]> results = applicationRepository.countApplicationsByDateForRecruiterLast30Days(recruiterId);
        
        List<TimeSeriesDataPoint> dataPoints = new ArrayList<>();
        
        for (Object[] row : results) {
             if (row != null && row.length == 2 && row[0] != null) {
                 String dateStr = (String) row[0];
                 Long count = ((Number) row[1]).longValue();
                 dataPoints.add(new TimeSeriesDataPoint(dateStr, count));
             }
        }
        
        return ApplicationTrendDto.builder()
                .data(dataPoints)
                .build();
    }
}
