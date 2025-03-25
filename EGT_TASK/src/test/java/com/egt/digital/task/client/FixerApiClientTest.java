package com.egt.digital.task.client;

import com.egt.digital.task.exception.ExternalServiceException;
import com.egt.digital.task.messaging.RequestStatisticsConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FixerApiClientTest {
    private static final Logger log = LoggerFactory.getLogger(FixerApiClientTest.class);

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FixerApiClient fixerApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchExchangeRates_Success() {
        ResponseEntity<Map<String, Object>> mockResponse =
                new ResponseEntity<>(Map.of("rates", Map.of("EUR", 1.12)), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);

        Map<String, Object> result = fixerApiClient.fetchExchangeRates();

        assertNotNull(result);
        assertTrue(result.containsKey("rates"));
    }

    @Test
    void testFetchExchangeRates_InvalidResponse() {
        ResponseEntity<Map<String, Object>> emptyResponse =
                new ResponseEntity<>(Collections.emptyMap(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(emptyResponse);

        Exception exception = assertThrows(ExternalServiceException.class, fixerApiClient::fetchExchangeRates);

        log.info("Actual exception message: " + exception.getMessage());

        assertEquals("Invalid response from Fixer.io", exception.getMessage());
    }

    @Test
    void testFetchExchangeRates_HttpError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Exception exception = assertThrows(ExternalServiceException.class, fixerApiClient::fetchExchangeRates);

        assertTrue(exception.getMessage().contains("Fixer.io API error: 404"));
    }

    @Test
    void testFetchExchangeRates_ApiUnavailable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("Service Unavailable"));

        Exception exception = assertThrows(ExternalServiceException.class, fixerApiClient::fetchExchangeRates);
        assertEquals("Fixer.io API unavailable", exception.getMessage());
    }
}