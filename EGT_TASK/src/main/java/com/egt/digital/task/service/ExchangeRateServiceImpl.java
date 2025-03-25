package com.egt.digital.task.service;

import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.repository.ExchangeRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@Service
public class ExchangeRateServiceImpl implements IExchangeRateService{
    private final ExchangeRateRepository exchangeRateRepository;
    private final RedisTemplate<String, ExchangeRate> exchangeRateRedisTemplate;
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);

    @Autowired
    public ExchangeRateServiceImpl(
            ExchangeRateRepository exchangeRateRepository,
            RedisTemplate<String, ExchangeRate> exchangeRateRedisTemplate
    ) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.exchangeRateRedisTemplate = exchangeRateRedisTemplate;
    }

    @Override
    public ExchangeRate getLatestRate(String currency) {
        log.info("Looking up exchange rate for currency: {}", currency);
        ExchangeRate rate = exchangeRateRedisTemplate.opsForValue().get(currency);

        if (rate == null) {
            log.info("Rate not found in Redis. Fetching from DB...");

            rate = exchangeRateRepository.findFirstByCurrencyOrderByTimestampDesc(currency)
                    .orElse(null);
            log.info("Database result: {}", rate);


            if (rate != null) {
                log.info("Caching rate to Redis...");
                exchangeRateRedisTemplate.opsForValue().set(currency, rate, Duration.ofHours(1));

            }
        }

        return rate;
    }

    @Override
    public List<ExchangeRate> getHistoryRates(String currency, int hoursBack) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(hoursBack);
        log.info("Fetching exchange rate history for {} from {}", currency, from);
        return exchangeRateRepository.findByCurrencyAndTimestampBetweenOrderByTimestampDesc(currency, from, now);
    }
}
