package com.interviewiq.candidate.dto;

import lombok.Data;

import java.util.List;

@Data
public class CandidateSearchRequest {
    private String keyword;
    private String location;
    private Integer minExperience;
    private Boolean openToRemote;
    private List<String> skills;
}
