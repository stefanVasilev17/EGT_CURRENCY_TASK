package com.egt.digital.task.exception;

/**
 * Created by: svasilev
 * Date: 3/25/2025
 */
public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException(String requestId) {
        super("Duplicated request with ID: " + requestId);
    }
}