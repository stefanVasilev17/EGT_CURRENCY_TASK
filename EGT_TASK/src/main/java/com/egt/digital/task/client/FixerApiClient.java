package com.egt.digital.task.client;

import com.egt.digital.task.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@Service
public class FixerApiClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String fixerUrl;

    @Autowired
    public FixerApiClient(RestTemplate restTemplate,
                          @Value("${fixer.api.key}") String apiKey,
                          @Value("${fixer.api.url}") String fixerUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.fixerUrl = fixerUrl;
    }

    public Map<String, Object> fetchExchangeRates() {
        try {
            String url = fixerUrl + "?access_key=" + apiKey;

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (null == response || null == response.getBody() || response.getBody().isEmpty()) {
                throw new ExternalServiceException("Invalid response from Fixer.io");
            }

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("Fixer.io API error: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("Fixer.io API unavailable", e);
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("Unexpected error while calling Fixer.io", e);
        }
    }
}