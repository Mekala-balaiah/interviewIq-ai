package com.interviewiq.common.exception;

/** Thrown on rate limit exceeded. Maps to HTTP 429. */
public class RateLimitExceededException extends InterviewIqException {
    public RateLimitExceededException() {
        super("RATE_LIMIT_EXCEEDED", "Too many requests. Please try again later.");
    }
}
