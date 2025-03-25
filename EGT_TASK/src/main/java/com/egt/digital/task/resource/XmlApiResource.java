package com.egt.digital.task.resource;

import com.egt.digital.task.model.XmlErrorResponse;
import com.egt.digital.task.model.XmlRequest;
import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.service.IXmlApiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */

@RestController
@Validated
public class XmlApiResource {

    private final IXmlApiService iXmlApiService;

    @Autowired
    public XmlApiResource(IXmlApiService xmlApiServiceImpl) {
        this.iXmlApiService = xmlApiServiceImpl;
    }

    @RequestMapping(value = "/xml_api/command", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> processXmlRequest(@RequestBody @Valid XmlRequest request) {

        if (iXmlApiService.isDuplicate(request.getId())) {
            return ResponseEntity.status(409)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(new XmlErrorResponse("Duplicate request"));
        }

        if (request.getHistory() != null &&
                request.getHistory().getCurrency() != null &&
                request.getHistory().getPeriod() != null) {

            List<ExchangeRate> history = iXmlApiService.getHistoryRates(request);
            if (history.isEmpty()) {
                return ResponseEntity.status(404)
                        .contentType(MediaType.APPLICATION_XML)
                        .body(new XmlErrorResponse("No historical data found for currency: " +
                                request.getHistory().getCurrency()));
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(history);
        }

        if (request.get() != null) {
            ExchangeRate rate = iXmlApiService.getCurrentRate(request);
            if (rate == null) {
                return ResponseEntity.status(404)
                        .contentType(MediaType.APPLICATION_XML)
                        .body(new XmlErrorResponse("Currency not found"));
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(rate);
        }

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_XML)
                .body(new XmlErrorResponse("Invalid command. Expected <get> or <history>."));
    }
}