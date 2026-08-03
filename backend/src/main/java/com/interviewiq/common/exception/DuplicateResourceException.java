package com.interviewiq.common.exception;

/** Thrown when a resource already exists. Maps to HTTP 409. */
public class DuplicateResourceException extends InterviewIqException {
    public DuplicateResourceException(String entityName, String fieldName, Object value) {
        super(
            entityName.toUpperCase().replace(" ", "_") + "_ALREADY_EXISTS",
            "%s already exists with %s: '%s'".formatted(entityName, fieldName, value)
        );
    }
    public DuplicateResourceException(String errorCode, String message) {
        super(errorCode, message);
    }
}
