package com.interviewiq.common.exception;

/**
 * Thrown when a requested resource cannot be found.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends InterviewIqException {

    public ResourceNotFoundException(String entityName, String fieldName, Object fieldValue) {
        super(
            entityName.toUpperCase().replace(" ", "_") + "_NOT_FOUND",
            "%s not found with %s: '%s'".formatted(entityName, fieldName, fieldValue)
        );
    }

    public ResourceNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
