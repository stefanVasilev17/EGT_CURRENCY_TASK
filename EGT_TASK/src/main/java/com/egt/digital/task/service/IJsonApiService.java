package com.egt.digital.task.service;

import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.model.JsonHistoryRequest;
import com.egt.digital.task.model.JsonRequest;

import java.util.List;

/**
 * Created by: svasilev
 * Date: 3/25/2025
 */
public interface IJsonApiService {

    ExchangeRate processCurrentRate(JsonRequest request);
    List<ExchangeRate> processHistoryRates(JsonHistoryRequest request);
}
