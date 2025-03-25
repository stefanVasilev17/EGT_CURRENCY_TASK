package com.egt.digital.task.model;

import jakarta.validation.constraints.NotBlank;

import javax.xml.bind.annotation.*;
import java.util.Objects;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@XmlRootElement(name = "command")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({XmlGetCommand.class, XmlHistoryCommand.class, ExchangeRate.class})
public class XmlRequest {

    @XmlAttribute(name = "id", required = true)
    @NotBlank(message = "Request ID cannot be empty")
    private String id;

    @XmlElement(name = "get")
    private XmlGetCommand get;

    @XmlElement(name = "history")
    private XmlHistoryCommand history;

    public XmlRequest() {}

    public XmlRequest(String id, XmlGetCommand get, XmlHistoryCommand history) {
        this.id = id;
        this.get = get;
        this.history = history;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public XmlGetCommand get() {
        return get;
    }

    public void setGet(XmlGetCommand get) {
        this.get = get;
    }
    public XmlHistoryCommand getHistory() { return history; }
    public void setHistory(XmlHistoryCommand history) { this.history = history; }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        XmlRequest that = (XmlRequest) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(get, that.get) &&
                Objects.equals(history, that.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, get, history);
    }

    @Override
    public String toString() {
        return "XmlRequest{" +
                "id='" + id + '\'' +
                ", get=" + get +
                ", history=" + history +
                '}';
    }
}