package com.interviewiq.common.exception;

/** Thrown when AI service call fails. Maps to HTTP 503. */
public class AiServiceException extends InterviewIqException {
    public AiServiceException(String message, Throwable cause) {
        super("AI_SERVICE_ERROR", message, cause);
    }
    public AiServiceException(String message) {
        super("AI_SERVICE_ERROR", message);
    }
}
