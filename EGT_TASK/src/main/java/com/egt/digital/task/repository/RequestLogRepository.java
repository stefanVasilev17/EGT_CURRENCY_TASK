package com.egt.digital.task.repository;

import com.egt.digital.task.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    boolean existsByRequestId(String requestId);
}

