package com.interviewiq.messaging.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.messaging.dto.InboundEmailWebhookRequest;
import com.interviewiq.messaging.service.MessagingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = EmailWebhookController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class EmailWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessagingService messagingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void receiveInboundEmail_ReturnsSuccess() throws Exception {
        InboundEmailWebhookRequest request = new InboundEmailWebhookRequest();
        request.setFrom("john.doe@example.com");
        request.setTo("inbound@interviewiq.ai");
        request.setSubject("Re: Application APP-550e8400-e29b-41d4-a716-446655440000");
        request.setBodyPlain("I am available for an interview on Monday.");

        doNothing().when(messagingService).processInboundEmail(any(InboundEmailWebhookRequest.class));

        mockMvc.perform(post("/api/v1/webhooks/email/inbound")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
