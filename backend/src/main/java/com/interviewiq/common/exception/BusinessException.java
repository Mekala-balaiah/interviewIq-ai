package com.interviewiq.common.exception;

/** Business rule / logic violation. Maps to HTTP 422. */
public class BusinessException extends InterviewIqException {
    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }
}
