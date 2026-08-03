package com.interviewiq.analytics.dto;

import com.interviewiq.application.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PipelineFunnelDto {
    private Map<ApplicationStatus, Long> stageCounts;
}
