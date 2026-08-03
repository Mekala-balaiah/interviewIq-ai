package com.interviewiq.common.exception;

import lombok.Getter;

/**
 * Base exception for all InterviewIQ domain exceptions.
 * Carries a structured error code for API error responses.
 */
@Getter
public abstract class InterviewIqException extends RuntimeException {

    private final String errorCode;

    protected InterviewIqException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected InterviewIqException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
