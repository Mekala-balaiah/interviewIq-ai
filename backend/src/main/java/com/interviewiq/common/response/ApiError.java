package com.interviewiq.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Structured API error payload included in failed responses.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final String code;
    private final String message;
    private final List<FieldError> details;

    public static ApiError of(String code, String message) {
        return ApiError.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ApiError of(String code, String message, List<FieldError> details) {
        return ApiError.builder()
                .code(code)
                .message(message)
                .details(details)
                .build();
    }

    /**
     * Field-level validation error detail.
     */
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {
        private final String field;
        private final Object rejectedValue;
        private final String message;

        public static FieldError of(String field, Object rejectedValue, String message) {
            return FieldError.builder()
                    .field(field)
                    .rejectedValue(rejectedValue)
                    .message(message)
                    .build();
        }
    }
}
