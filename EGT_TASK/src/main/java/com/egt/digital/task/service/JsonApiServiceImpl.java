package com.egt.digital.task.service;

import com.egt.digital.task.constants.ServiceName;
import com.egt.digital.task.resource.JsonApiResource;
import com.egt.digital.task.exception.CurrencyNotFoundException;
import com.egt.digital.task.exception.NoCurrencyHistoryException;
import com.egt.digital.task.messaging.RequestStatisticsProducer;
import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.model.JsonHistoryRequest;
import com.egt.digital.task.model.JsonRequest;
import com.egt.digital.task.model.RequestStatistics;
import com.egt.digital.task.repository.RequestStatisticsRepository;
import com.egt.digital.task.exception.DuplicateRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by: svasilev
 * Date: 3/24/2025
 */
@Service
public class JsonApiServiceImpl implements IJsonApiService{

    private final IExchangeRateService iExchangeRateServiceImpl;
    private final RequestStatisticsRepository requestStatisticsRepository;
    private final RequestStatisticsProducer requestStatisticsProducer;
    private static final Logger log = LoggerFactory.getLogger(JsonApiResource.class);

    @Autowired
    public JsonApiServiceImpl(IExchangeRateService iExchangeRateServiceImpl,
                              RequestStatisticsRepository requestStatisticsRepository,
                              RequestStatisticsProducer requestStatisticsProducer) {
        this.iExchangeRateServiceImpl = iExchangeRateServiceImpl;
        this.requestStatisticsRepository = requestStatisticsRepository;
        this.requestStatisticsProducer = requestStatisticsProducer;
    }

    /**
     * Checks if the request is a duplicate by verifying the request ID in the repository.
     * It retrieves the current exchange rate for the specified currency. If the rate is found,
     * the statistics for the request are saved and sent to a producer. If the request is a duplicate
     * or if no rate is found for the specified currency, the corresponding exceptions are thrown.
     *
     * @param request The JsonRequest object containing the details of the current rate request.
     * @return The ExchangeRate object containing the current exchange rate for the specified currency
     * @throws DuplicateRequestException If the requestId has already been processed.
     * @throws CurrencyNotFoundException If the requested currency is not found.
     */
    @Override
    public ExchangeRate processCurrentRate(JsonRequest request) {
        log.info("Received request: {}", request);
        if (requestStatisticsRepository.existsByRequestId(request.getRequestId())) {
            log.warn("Duplicate requestId: {}", request.getRequestId());
            throw new DuplicateRequestException(request.getRequestId());
        }

        ExchangeRate rate = iExchangeRateServiceImpl.getLatestRate(request.getCurrency());
        if (rate == null) {
            log.warn("Currency not found: {}", request.getCurrency());
            throw new CurrencyNotFoundException(request.getCurrency());
        }

        RequestStatistics stats = new RequestStatistics(request.getRequestId(),
                ServiceName.EXTERNAL_SERVICE_1,
                request.getClient(),
                LocalDateTime.now());

        requestStatisticsRepository.save(stats);
        requestStatisticsProducer.sendStatistics(stats);

        log.info("Returning rate: {}", rate);
        return rate;
    }

    /**
     * Processes a request for historical exchange rates by checking for duplicate request IDs and retrieving
     * the corresponding exchange rates for the given currency and period. If the request is a duplicate or if
     * no data is found for the specified currency, it throws the corresponding exceptions.
     * The method also logs the processing steps and saves statistics for the request.
     *
     * @param request The JsonHistoryRequest object containing the details of the history request.
     * @return A list of ExchangeRate objects containing the historical rates for the specified currency and period
     * @throws DuplicateRequestException If the requestId has already been processed.
     * @throws NoCurrencyHistoryException If no historical data is found for the specified currency and period.
     */
    @Override
    public List<ExchangeRate> processHistoryRates(JsonHistoryRequest request) {
        log.info("Received history request: {}", request);
        if (requestStatisticsRepository.existsByRequestId(request.getRequestId())) {
            log.warn("Duplicate requestId: {}", request.getRequestId());
            throw new DuplicateRequestException(request.getRequestId());
        }

        List<ExchangeRate> rates = iExchangeRateServiceImpl.getHistoryRates(request.getCurrency(), request.getPeriod());
        if (rates.isEmpty()) {
            log.warn("No exchange rates found for currency: {}", request.getCurrency());
            throw new NoCurrencyHistoryException(request.getCurrency(), request.getPeriod());
        }

        RequestStatistics stats = new RequestStatistics(request.getRequestId(),
                ServiceName.EXTERNAL_SERVICE_1,
                request.getClient(),
                LocalDateTime.now());

        requestStatisticsRepository.save(stats);
        requestStatisticsProducer.sendStatistics(stats);

        log.info("Returning {} rates for currency {}", rates.size(), request.getCurrency());

        return rates;
    }
}
