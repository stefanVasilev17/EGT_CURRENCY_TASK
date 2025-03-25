package com.egt.digital.task.service;

import com.egt.digital.task.client.FixerApiClient;
import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.repository.ExchangeRateRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by: svasilev
 * Date: 3/23/2025
 */

@Component
public class ExchangeRateScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateScheduler.class);

    private final FixerApiClient fixerApiClient;
    private final ExchangeRateRepository exchangeRateRepository;
    private final RedisTemplate<String, ExchangeRate> redisTemplate;
    private final String baseCurrency;

    @Autowired
    public ExchangeRateScheduler(FixerApiClient fixerApiClient,
                                 ExchangeRateRepository exchangeRateRepository,
                                 RedisTemplate<String, ExchangeRate> redisTemplate,
                                 @Value("${fixer.api.base-currency}") String baseCurrency) {
        this.fixerApiClient = fixerApiClient;
        this.exchangeRateRepository = exchangeRateRepository;
        this.redisTemplate = redisTemplate;
        this.baseCurrency = baseCurrency;
    }

    @PostConstruct
    public void init() {
        log.info("ExchangeRateScheduler is active and running");
    }

    /**
     * Fetches exchange rates from Fixer.io and stores them in the database and Redis cache.
     * This method is scheduled to run at a fixed interval to keep the exchange rates up to date.
     */
    @Scheduled(fixedDelayString = "${schedule.update-interval}")
    public void fetchAndStoreRates() {
        log.info("Fetching exchange rates from Fixer.io...");

        fetchExchangeRates().ifPresentOrElse(response -> {
            Map<String, BigDecimal> rates = parseRates(response);
            if (rates.isEmpty()) {
                log.error("No valid rates found in the response.");
                return;
            }

            LocalDateTime timestamp = parseTimestamp(response);
            saveRatesToDatabaseAndCache(rates, timestamp);
        }, () -> log.error("Failed to fetch rates from Fixer.io."));
    }

    /**
     * Fetches exchange rates from the external Fixer.io service.
     *
     * @return the response from the Fixer API wrapped in an Optional.
     */
    private Optional<Map<String, Object>> fetchExchangeRates() {
        try {
            return Optional.ofNullable(fixerApiClient.fetchExchangeRates());
        } catch (Exception e) {
            log.error("Failed to fetch rates from Fixer.io: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Parses the exchange rates from the Fixer.io response into a Map of currency codes and their corresponding rates.
     *
     * @param response the Fixer.io response
     * @return a map of currency codes and rates
     */
    private Map<String, BigDecimal> parseRates(Map<String, Object> response) {
        Map<String, Object> rawRates = (Map<String, Object>) response.get("rates");
        if (rawRates == null) {
            log.error("Rates data is missing in the response.");
            return Collections.emptyMap();
        }

        Map<String, BigDecimal> rates = new HashMap<>();
        rawRates.forEach((currency, value) -> {
            BigDecimal rate = convertToBigDecimal(value);
            rates.put(currency, rate);
        });
        return rates;
    }

    /**
     * Converts a rate value into a BigDecimal.
     *
     * @param value the value to be converted
     * @return the BigDecimal representation of the value or null if the type is unsupported
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        throw new UnsupportedOperationException("Unsupported rate type: " + value);
    }

    /**
     * Parses the timestamp from the Fixer.io response into a LocalDateTime.
     *
     * @param response the Fixer.io response
     * @return the timestamp as LocalDateTime
     */
    private LocalDateTime parseTimestamp(Map<String, Object> response) {
        Object timestampObject = response.get("timestamp");
        if (timestampObject instanceof Number) {
            long timestamp = ((Number) timestampObject).longValue();
            return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        log.error("Invalid or missing timestamp in the response.");
        return LocalDateTime.now();
    }

    /**
     * Saves the exchange rates to both the database and the Redis cache.
     *
     * @param rates     the rates to be saved
     * @param timestamp the timestamp for the rates
     */
    private void saveRatesToDatabaseAndCache(Map<String, BigDecimal> rates, LocalDateTime timestamp) {
        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : rates.entrySet()) {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setCurrency(entry.getKey());
            exchangeRate.setRate(entry.getValue());
            exchangeRate.setTimestamp(timestamp);
            exchangeRate.setBaseCurrency(baseCurrency);
            exchangeRateList.add(exchangeRate);
        }
        try {
            exchangeRateRepository.saveAll(exchangeRateList);
            log.info("Bulk saved {} rates to DB", exchangeRateList.size());
        } catch (Exception e) {
            log.error("Failed to bulk save rates: {}", e.getMessage(), e);
        }
        for (ExchangeRate exchangeRate : exchangeRateList) {
            saveRateToCache(exchangeRate.getCurrency(), exchangeRate);
        }

        log.info("Cached {} rates to Redis", exchangeRateList.size());
    }

    /**
     * Saves an exchange rate to the database.
     *
     * @param exchangeRate the exchange rate to be saved
     */
    private void saveRateToDatabase(ExchangeRate exchangeRate) {
        try {
            exchangeRateRepository.save(exchangeRate);
            log.info("✔ Saved to DB: {} = {}", exchangeRate.getCurrency(), exchangeRate.getRate());
        } catch (Exception e) {
            log.error("Failed to save exchange rate to DB for {}: {}", exchangeRate.getCurrency(), e.getMessage());
        }
    }

    /**
     * Saves an exchange rate to the Redis cache.
     *
     * @param currency     the currency code
     * @param exchangeRate the exchange rate to be cached
     */
    private void saveRateToCache(String currency, ExchangeRate exchangeRate) {
        try {
            redisTemplate.opsForValue().set(currency, exchangeRate, Duration.ofMinutes(10));
        } catch (Exception e) {
            log.warn("Could not cache {} to Redis: {}", currency, e.getMessage());
        }
    }
}