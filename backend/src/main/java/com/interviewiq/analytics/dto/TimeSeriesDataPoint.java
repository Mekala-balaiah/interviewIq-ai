package com.interviewiq.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeSeriesDataPoint {
    private String date; // "YYYY-MM-DD"
    private long count;
}
