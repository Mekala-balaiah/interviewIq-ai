package com.interviewiq.notification.controller;

import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.notification.dto.NotificationDto;
import com.interviewiq.notification.service.NotificationService;
import com.interviewiq.notification.service.SseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = NotificationController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private SseService sseService;

    private UserPrincipal testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new UserPrincipal(
                userId,
                "user@example.com",
                "password",
                UserRole.CANDIDATE,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CANDIDATE"))
        );
    }

    @Test
    void getNotifications_ReturnsSuccess() throws Exception {
        NotificationDto notification = new NotificationDto();
        notification.setId(UUID.randomUUID());
        notification.setTitle("Welcome");
        notification.setMessage("Welcome to InterviewIQ");

        PagedResponse<NotificationDto> response = new PagedResponse<>(
                List.of(notification), 0, 10, 1, 1, true
        );

        when(notificationService.getNotifications(eq(userId), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/notifications")
                .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Welcome"));
    }

    @Test
    void getUnreadCount_ReturnsSuccess() throws Exception {
        when(notificationService.getUnreadCount(userId)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(5));
    }

    @Test
    void markAsRead_ReturnsSuccess() throws Exception {
        UUID notifId = UUID.randomUUID();
        NotificationDto notification = new NotificationDto();
        notification.setId(notifId);
        notification.setIsRead(true);

        when(notificationService.markAsRead(notifId, userId)).thenReturn(notification);

        mockMvc.perform(put("/api/v1/notifications/{id}/read", notifId)
                .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isRead").value(true));
    }
}
