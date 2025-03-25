package com.egt.digital.task.service;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */

import com.egt.digital.task.model.RequestLog;
import com.egt.digital.task.repository.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for handling request logging and duplication checks.
 */
@Service
public class RequestLogService {

    private final RequestLogRepository requestLogRepository;

    @Autowired
    public RequestLogService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    /**
     * Checks if a request with the given ID already exists.
     *
     * @param requestId Unique request identifier.
     * @return true if request exists, otherwise false.
     */
    public boolean isDuplicateRequest(String requestId) {
        return requestLogRepository.existsByRequestId(requestId);
    }

    /**
     * Saves a request log in the database.
     *
     * @param requestId Unique request identifier.
     * @param clientId  Client ID.
     * @param currency  Currency requested.
     */
    @Transactional
    public void saveRequestLog(String requestId, String clientId, String currency) {
        if (this.isDuplicateRequest(requestId)) {
            throw new IllegalStateException("Duplicate request: " + requestId);
        }
        RequestLog requestLog = new RequestLog(requestId, clientId, currency, LocalDateTime.now());
        requestLogRepository.save(requestLog);
    }
}