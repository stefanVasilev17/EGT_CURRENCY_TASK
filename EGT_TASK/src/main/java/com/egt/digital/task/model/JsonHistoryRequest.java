package com.egt.digital.task.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
public class JsonHistoryRequest extends JsonRequest {

    @NotNull(message = "Period is required")
    @Min(value = 1, message = "Period must be at least 1 hour")
    private Integer period;

    public JsonHistoryRequest() {}

    public JsonHistoryRequest(String requestId, Long timestamp, String client, String currency, Integer period) {
        super(requestId, timestamp, client, currency);
        this.period = period;
    }

    public Integer getPeriod() { return period; }
    public void setPeriod(Integer period) { this.period = period; }
}