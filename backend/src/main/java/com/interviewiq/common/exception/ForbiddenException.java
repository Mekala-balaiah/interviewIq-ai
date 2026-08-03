package com.interviewiq.common.exception;

/** Thrown when user lacks permissions for the action. Maps to HTTP 403. */
public class ForbiddenException extends InterviewIqException {
    public ForbiddenException(String message) {
        super("FORBIDDEN", message);
    }
}
