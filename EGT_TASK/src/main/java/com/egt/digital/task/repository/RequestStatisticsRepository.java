package com.egt.digital.task.repository;

import com.egt.digital.task.model.RequestStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@Repository
public interface RequestStatisticsRepository extends JpaRepository<RequestStatistics, Long> {
    boolean existsByRequestId(String requestId);
}
