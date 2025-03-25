package com.egt.digital.task.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.Objects;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlHistoryCommand {

    @XmlAttribute(name = "consumer", required = true)
    @NotBlank(message = "Consumer ID cannot be empty")
    private String consumer;

    @XmlAttribute(name = "currency", required = true)
    @NotBlank(message = "Currency cannot be empty")
    private String currency;

    @XmlAttribute(name = "period", required = true)
    @NotNull(message = "Period is required")
    @Min(value = 1, message = "Period must be at least 1 hour")
    private Integer period;

    public XmlHistoryCommand() {
    }

    public XmlHistoryCommand(String consumer, String currency, Integer period) {
        this.consumer = consumer;
        this.currency = currency;
        this.period = period;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "XmlHistoryCommand{" +
                "consumer='" + consumer + '\'' +
                ", currency='" + currency + '\'' +
                ", period=" + period +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        XmlHistoryCommand that = (XmlHistoryCommand) o;

        return Objects.equals(consumer, that.consumer) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(period, that.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consumer, currency, period);
    }

}