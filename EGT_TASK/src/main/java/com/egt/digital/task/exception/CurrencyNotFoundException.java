package com.egt.digital.task.exception;

/**
 * Created by: svasilev
 * Date: 3/24/2025
 */
public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException(String currency) {
        super("Currency not found: " + currency);
    }
}