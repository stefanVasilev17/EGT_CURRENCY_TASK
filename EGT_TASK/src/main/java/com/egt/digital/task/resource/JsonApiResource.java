package com.egt.digital.task.resource;

import com.egt.digital.task.exception.CurrencyNotFoundException;
import com.egt.digital.task.exception.NoCurrencyHistoryException;
import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.model.JsonHistoryRequest;
import com.egt.digital.task.model.JsonRequest;
import com.egt.digital.task.service.IJsonApiService;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@RestController
public class JsonApiResource {
    private final IJsonApiService iJsonApiService;

    public JsonApiResource(IJsonApiService iJsonApiService) {
        this.iJsonApiService = iJsonApiService;
    }

    @PostMapping("/json_api/current")
    public ResponseEntity<?> retrieveCurrentRate(@RequestBody @Valid JsonRequest request) {
        try {
            ExchangeRate rate = iJsonApiService.processCurrentRate(request);
            return ResponseEntity.ok(rate);
        } catch (DuplicateRequestException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request");
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Currency not found");
        }
    }

    @PostMapping("/json_api/history")
    public ResponseEntity<?> retrieveHistoryRates(@RequestBody @Valid JsonHistoryRequest request) {
        try {
            List<ExchangeRate> rates = iJsonApiService.processHistoryRates(request);
            return ResponseEntity.ok(rates);
        } catch (DuplicateRequestException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate request");
        } catch (NoCurrencyHistoryException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data for currency");
        }
    }

}

