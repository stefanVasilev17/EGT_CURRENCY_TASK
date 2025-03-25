package com.egt.digital.task.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
public class JsonRequest {

    @NotBlank(message = "Request ID cannot be empty")
    private String requestId;

    @NotNull(message = "Timestamp is required")
    @Positive(message = "Timestamp must be a positive number")
    private Long timestamp;

    @NotBlank(message = "Client ID cannot be empty")
    private String client;

    @NotBlank(message = "Currency cannot be empty")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code")
    private String currency;

    public JsonRequest() {}

    public JsonRequest(String requestId, Long timestamp, String client, String currency) {
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.client = client;
        this.currency = currency;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
