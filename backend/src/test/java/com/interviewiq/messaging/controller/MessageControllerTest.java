package com.interviewiq.messaging.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.messaging.dto.MessageDto;
import com.interviewiq.messaging.dto.SendMessageRequest;
import com.interviewiq.messaging.service.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = MessageController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessagingService messagingService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal authUser;
    private UUID receiverId;

    @BeforeEach
    void setUp() {
        authUser = new UserPrincipal(
                UUID.randomUUID(),
                "user@example.com",
                "password",
                UserRole.CANDIDATE,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CANDIDATE"))
        );
        receiverId = UUID.randomUUID();
    }

    @Test
    void sendMessage_ReturnsSuccess() throws Exception {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(receiverId);
        request.setSubject("Interview Follow-up");
        request.setContent("Thank you for your time.");

        MessageDto dto = new MessageDto();
        dto.setId(UUID.randomUUID());
        dto.setSubject("Interview Follow-up");

        when(messagingService.sendMessage(any(UUID.class), any(SendMessageRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/messages")
                .with(SecurityMockMvcRequestPostProcessors.user(authUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subject").value("Interview Follow-up"));
    }

    @Test
    void getConversation_ReturnsSuccess() throws Exception {
        MessageDto dto = new MessageDto();
        dto.setId(UUID.randomUUID());
        dto.setContent("Hello there!");

        Page<MessageDto> page = new PageImpl<>(List.of(dto));
        PagedResponse<MessageDto> response = new PagedResponse<>(page);

        when(messagingService.getConversation(any(UUID.class), eq(receiverId), any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/messages/conversations/{otherUserId}", receiverId)
                .with(SecurityMockMvcRequestPostProcessors.user(authUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].content").value("Hello there!"));
    }
}
