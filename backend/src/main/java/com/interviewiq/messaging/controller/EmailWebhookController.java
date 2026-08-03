package com.interviewiq.messaging.controller;

import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.messaging.dto.InboundEmailWebhookRequest;
import com.interviewiq.messaging.service.MessagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks/email")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Endpoints for external system integrations")
public class EmailWebhookController {

    private final MessagingService messagingService;

    @PostMapping("/inbound")
    @Operation(summary = "Receive inbound email via webhook")
    public ResponseEntity<ApiResponse<Void>> receiveInboundEmail(
            @RequestBody InboundEmailWebhookRequest request) {
        
        // Note: In production, we'd verify a webhook signature (e.g. from Mailgun/SendGrid)
        // using an API key or HMAC signature in the headers.

        messagingService.processInboundEmail(request);

        return ResponseEntity.ok(ApiResponse.success(null, "Webhook processed"));
    }
}
