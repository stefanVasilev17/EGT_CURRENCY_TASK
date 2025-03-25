package com.egt.digital.task.exception;

/**
 * Custom exception for handling external service errors.
 * This exception is thrown when a third-party API (e.g., Fixer.io) is unavailable or returns an invalid response.
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
