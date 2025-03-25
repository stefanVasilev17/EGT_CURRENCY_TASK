package com.egt.digital.task.exception;

/**
 * Created by: svasilev
 * Date: 3/24/2025
 */
public class NoCurrencyHistoryException extends RuntimeException {

    public NoCurrencyHistoryException(String currency, int period) {
        super("No historical exchange rate data found for currency: " + currency + " in last " + period + " hours.");
    }
}