package com.interviewiq.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApplicationTrendDto {
    private List<TimeSeriesDataPoint> data;
}
