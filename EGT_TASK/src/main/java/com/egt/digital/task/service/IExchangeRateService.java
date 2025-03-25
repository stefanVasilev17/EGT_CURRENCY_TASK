package com.egt.digital.task.service;

import com.egt.digital.task.model.ExchangeRate;

import java.util.List;

/**
 * Created by: svasilev
 * Date: 3/25/2025
 */
public interface IExchangeRateService {
    ExchangeRate getLatestRate(String currency);
    List<ExchangeRate> getHistoryRates(String currency, int hoursBack);
}
