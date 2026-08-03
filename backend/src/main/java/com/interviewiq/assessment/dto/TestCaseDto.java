package com.interviewiq.assessment.dto;

import lombok.Data;

@Data
public class TestCaseDto {
    private String input;
    private String expectedOutput;
    private boolean isHidden;
}
