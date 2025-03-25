package com.egt.digital.task.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by: svasilev
 * Date: 3/23/2025
 */
@XmlRootElement(name = "XmlErrorResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlErrorResponse {
    private String message;

    public XmlErrorResponse() {}
    public XmlErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}