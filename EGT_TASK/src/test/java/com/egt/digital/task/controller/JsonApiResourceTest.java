
package com.egt.digital.task.controller;

import com.egt.digital.task.exception.CurrencyNotFoundException;
import com.egt.digital.task.exception.DuplicateRequestException;
import com.egt.digital.task.exception.NoCurrencyHistoryException;
import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.model.JsonHistoryRequest;
import com.egt.digital.task.model.JsonRequest;
import com.egt.digital.task.resource.JsonApiResource;
import com.egt.digital.task.service.JsonApiServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JsonApiResource.class)
@ActiveProfiles("test")
public class JsonApiResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JsonApiServiceImpl jsonApiServiceImpl;

    private ExchangeRate sampleRate;

    @BeforeEach
    void setUp() {
        sampleRate = new ExchangeRate();
        sampleRate.setCurrency("EUR");
        sampleRate.setRate(BigDecimal.valueOf(1.12));
    }

    @Test
    void testRetrieveCurrentRate_Success() throws Exception {
        JsonRequest request = new JsonRequest("req1", 1686335186721L, "client1", "EUR");
        when(jsonApiServiceImpl.processCurrentRate(any())).thenReturn(sampleRate);

        mockMvc.perform(post("/json_api/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.rate").value(1.12));
    }

    @Test
    void testRetrieveCurrentRate_Duplicate() throws Exception {
        JsonRequest request = new JsonRequest("req2", 1686335186721L, "client1", "EUR");
        when(jsonApiServiceImpl.processCurrentRate(any())).thenThrow(new DuplicateRequestException("req2"));

        mockMvc.perform(post("/json_api/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Duplicated request with ID: req2"));
    }

    @Test
    void testRetrieveCurrentRate_CurrencyNotFound() throws Exception {
        JsonRequest request = new JsonRequest("req3", 1686335186721L, "client1", "XYZ");
        when(jsonApiServiceImpl.processCurrentRate(any())).thenThrow(new CurrencyNotFoundException("XYZ"));

        mockMvc.perform(post("/json_api/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Currency not found"));
    }

    @Test
    void testRetrieveHistoryRates_Success() throws Exception {
        JsonHistoryRequest request = new JsonHistoryRequest("req4", 1686335186721L, "client1", "EUR", 7);
        when(jsonApiServiceImpl.processHistoryRates(any())).thenReturn(List.of(sampleRate));

        mockMvc.perform(post("/json_api/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currency").value("EUR"));
    }

    @Test
    void testRetrieveHistoryRates_Duplicate() throws Exception {
        JsonHistoryRequest request = new JsonHistoryRequest("req5", 1686335186721L, "client1", "EUR", 7);
        when(jsonApiServiceImpl.processHistoryRates(any())).thenThrow(new DuplicateRequestException("req5"));

        mockMvc.perform(post("/json_api/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Duplicated request with ID: req5"));
    }

    @Test
    void testRetrieveHistoryRates_NoData() throws Exception {
        JsonHistoryRequest request = new JsonHistoryRequest("req6", 1686335186721L, "client1", "EUR", 7);
        when(jsonApiServiceImpl.processHistoryRates(any())).thenThrow(new NoCurrencyHistoryException("EUR", 7));

        mockMvc.perform(post("/json_api/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No data for currency"));
    }

    @Test
    void testRetrieveCurrentRate_InvalidInput() throws Exception {
        JsonRequest invalidRequest = new JsonRequest(null, 0L, null, null);

        mockMvc.perform(post("/json_api/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRetrieveHistoryRates_InvalidInput() throws Exception {
        JsonHistoryRequest invalidRequest = new JsonHistoryRequest(null, 0L, null, null, null);

        mockMvc.perform(post("/json_api/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRetrieveCurrentRate_ResponseBodyNotNull() throws Exception {
        JsonRequest request = new JsonRequest("req7", 1686335186721L, "client1", "EUR");
        when(jsonApiServiceImpl.processCurrentRate(any())).thenReturn(sampleRate);

        mockMvc.perform(post("/json_api/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void testRetrieveHistoryRates_EmptyList() throws Exception {
        JsonHistoryRequest request = new JsonHistoryRequest("req8", 1686335186721L, "client1", "EUR", 7);
        when(jsonApiServiceImpl.processHistoryRates(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/json_api/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
