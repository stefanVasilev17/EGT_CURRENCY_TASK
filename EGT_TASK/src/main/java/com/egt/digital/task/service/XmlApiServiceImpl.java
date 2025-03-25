package com.egt.digital.task.service;

import com.egt.digital.task.constants.ServiceName;
import com.egt.digital.task.messaging.RequestStatisticsProducer;
import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.model.RequestStatistics;
import com.egt.digital.task.model.XmlRequest;
import com.egt.digital.task.repository.RequestStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Created by: svasilev
 * Date: 3/25/2025
 * <p>
 * This class provides methods to process requests for the current exchange rate
 * and historical exchange rates for a specified currency.
 * <p>
 * The main functionalities include:
 * - Checking for duplicate request IDs.
 * - Fetching the latest exchange rate for a specified currency.
 * - Fetching historical exchange rates based on a specified period.
 * - Saving and sending request statistics for tracking and monitoring purposes.
 */
@Service
public class XmlApiServiceImpl implements IXmlApiService {

    private final ExchangeRateServiceImpl exchangeRateServiceImpl;
    private final RequestStatisticsRepository requestStatisticsRepository;
    private final RequestStatisticsProducer requestStatisticsProducer;

    @Autowired
    public XmlApiServiceImpl(ExchangeRateServiceImpl exchangeRateServiceImpl,
                             RequestStatisticsRepository requestStatisticsRepository,
                             RequestStatisticsProducer requestStatisticsProducer) {
        this.exchangeRateServiceImpl = exchangeRateServiceImpl;
        this.requestStatisticsRepository = requestStatisticsRepository;
        this.requestStatisticsProducer = requestStatisticsProducer;
    }

    @Override
    public boolean isDuplicate(String requestId) {
        return requestStatisticsRepository.existsByRequestId(requestId);
    }

    @Override
    public ExchangeRate getCurrentRate(XmlRequest request) {
        String currency = request.get().getCurrency();
        String consumer = request.get().getConsumer();

        ExchangeRate rate = exchangeRateServiceImpl.getLatestRate(currency);

        if (rate != null) {
            saveStats(request.getId(), consumer);
        }

        return rate;
    }

    @Override
    public List<ExchangeRate> getHistoryRates(XmlRequest request) {
        String currency = request.getHistory().getCurrency();
        String consumer = request.getHistory().getConsumer();
        Integer period = request.getHistory().getPeriod();

        List<ExchangeRate> history = exchangeRateServiceImpl.getHistoryRates(currency, period);

        if (!history.isEmpty()) {
            saveStats(request.getId(), consumer);
        }

        return history;
    }

    private void saveStats(String requestId, String consumer) {
        RequestStatistics stats = new RequestStatistics(
                requestId,
                ServiceName.EXTERNAL_SERVICE_2,
                consumer,
                LocalDateTime.now()
        );

        requestStatisticsRepository.save(stats);
        requestStatisticsProducer.sendStatistics(stats);
    }
}