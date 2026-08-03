package com.interviewiq.candidate.controller;

import com.interviewiq.candidate.dto.SkillDto;
import com.interviewiq.candidate.service.SkillService;
import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skills Dictionary", description = "Endpoints for searching and managing the global skills dictionary")
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/search")
    @Operation(summary = "Search skills by name")
    public ApiResponse<List<SkillDto>> searchSkills(@RequestParam String query) {
        return ApiResponse.success(skillService.searchSkills(query), "Skills retrieved");
    }
}
