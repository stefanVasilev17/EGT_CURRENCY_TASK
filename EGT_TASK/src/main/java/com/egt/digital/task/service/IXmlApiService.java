package com.egt.digital.task.service;

import com.egt.digital.task.model.ExchangeRate;
import com.egt.digital.task.model.XmlRequest;

import java.util.List;

/**
 * Created by: svasilev
 * Date: 3/25/2025
 */
public interface IXmlApiService {

    boolean isDuplicate(String requestId);

    ExchangeRate getCurrentRate(XmlRequest request);

    List<ExchangeRate> getHistoryRates(XmlRequest request);
}