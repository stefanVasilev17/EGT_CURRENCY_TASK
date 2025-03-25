package com.egt.digital.task.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlGetCommand {

    @XmlAttribute(name = "consumer", required = true)
    @NotNull(message = "Consumer ID cannot be empty")
    private String consumer;

    @XmlElement(name = "currency", required = true)
    @NotNull(message = "Currency cannot be empty")
    private String currency;

    public XmlGetCommand() {
    }

    public XmlGetCommand(String consumer, String currency) {
        this.consumer = consumer;
        this.currency = currency;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        XmlGetCommand that = (XmlGetCommand) o;

        return Objects.equals(consumer, that.consumer) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consumer, currency);
    }

    @Override
    public String toString() {
        return "XmlGetCommand{" +
                "consumer='" + consumer + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}