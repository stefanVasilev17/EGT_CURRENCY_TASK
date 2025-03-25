package com.egt.digital.task.repository;

import com.egt.digital.task.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findFirstByCurrencyOrderByTimestampDesc(String currency);
    List<ExchangeRate> findByCurrencyAndTimestampBetweenOrderByTimestampDesc(String currency, LocalDateTime from, LocalDateTime to);

}
