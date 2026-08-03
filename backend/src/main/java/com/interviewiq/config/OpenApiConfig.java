package com.interviewiq.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 / Swagger UI Configuration.
 *
 * <p>Accessible at: /swagger-ui.html (dev only)
 * <p>API docs JSON: /api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public OpenAPI interviewIqOpenApi() {
        return new OpenAPI()
                .info(buildApiInfo())
                .servers(buildServers())
                .components(buildComponents())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }

    private Info buildApiInfo() {
        return new Info()
                .title("InterviewIQ AI — REST API")
                .version("v1")
                .description("""
                        ## Enterprise AI Recruitment & Interview Intelligence Platform
                        
                        **Authentication:** All protected endpoints require a `Bearer <JWT>` token header.
                        Use `/api/v1/auth/login` to obtain tokens.
                        
                        ### Roles
                        | Role | Description |
                        |------|-------------|
                        | `CANDIDATE` | Job seeker |
                        | `RECRUITER` | Hiring team member |
                        | `HR_MANAGER` | HR lead |
                        | `COMPANY_ADMIN` | Company administrator |
                        | `SUPER_ADMIN` | Platform super admin |
                        
                        ### Response Format
                        All responses follow the structure:
                        ```json
                        {
                          "success": true,
                          "data": { },
                          "message": "...",
                          "timestamp": "..."
                        }
                        ```
                        """)
                .contact(new Contact()
                        .name("InterviewIQ AI Engineering")
                        .email("engineering@interviewiq.ai")
                        .url("https://interviewiq.ai"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> buildServers() {
        return List.of(
                new Server().url("http://localhost:8080").description("Local Development"),
                new Server().url("https://api-staging.interviewiq.ai").description("Staging"),
                new Server().url("https://api.interviewiq.ai").description("Production")
        );
    }

    private Components buildComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .name("Authorization")
                                .description("Enter your JWT access token. Obtain from `/api/v1/auth/login`")
                );
    }
}
