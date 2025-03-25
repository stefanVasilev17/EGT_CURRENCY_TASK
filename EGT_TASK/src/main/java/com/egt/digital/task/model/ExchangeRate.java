package com.egt.digital.task.model;

import com.egt.digital.task.config.LocalDateTimeAdapter;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@Entity
@Table(name = "exchange_rates")
@XmlRootElement(name = "rate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeRate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlAttribute(name = "id", required = true)
    private Long id;

    @Column(nullable = false)
    private String baseCurrency;
    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal rate;

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ExchangeRate() {
    }

    public ExchangeRate(Long id, String baseCurrency, String currency, BigDecimal rate, LocalDateTime timestamp) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.currency = currency;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public ExchangeRate( String currency, BigDecimal rate, LocalDateTime timestamp) {
        this.currency = currency;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }
}
