package com.egt.digital.task.service;

import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExchangeRateServiceImplTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateServiceImpl;

    @Mock
    private RedisTemplate<String, ExchangeRate> redisTemplate;

    @Mock
    private ValueOperations<String, ExchangeRate> valueOperations;

    private ExchangeRate sampleRate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleRate = new ExchangeRate();
        sampleRate.setCurrency("EUR");
        sampleRate.setRate(BigDecimal.valueOf(1.12));
        sampleRate.setTimestamp(LocalDateTime.now());

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGetLatestRate_Success() {
        when(exchangeRateRepository.findFirstByCurrencyOrderByTimestampDesc("EUR"))
                .thenReturn(Optional.of(sampleRate));

        ExchangeRate result = exchangeRateServiceImpl.getLatestRate("EUR");

        assertNotNull(result);
        assertEquals("EUR", result.getCurrency());
    }

    @Test
    void testGetLatestRate_NotFound() {
        when(exchangeRateRepository.findFirstByCurrencyOrderByTimestampDesc("USD"))
                .thenReturn(Optional.empty());

        ExchangeRate result = exchangeRateServiceImpl.getLatestRate("USD");
        assertNull(result);
    }
}