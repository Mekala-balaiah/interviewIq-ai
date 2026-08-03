package com.interviewiq.recruiter.controller;

import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat/recruiter")
@Tag(name = "Recruiter AI Assistant", description = "AI chatbot endpoints for recruiters")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterChatbotController {

    @PostMapping
    @Operation(summary = "Send a message to the AI assistant (Stub)")
    public ApiResponse<Map<String, String>> sendMessage(@RequestBody Map<String, String> request) {
        String query = request.getOrDefault("message", "");
        
        // Mock response until Sprint 10
        String aiResponse = "I am the InterviewIQ AI Assistant. I understood your query: '" + query + 
                           "'. Full AI integration will be available in Sprint 10. How else can I help you today?";
                           
        return ApiResponse.success(Map.of("response", aiResponse), "AI Response generated");
    }
}
