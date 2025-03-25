package com.egt.digital.task.model;
import com.egt.digital.task.constants.ServiceName;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by: svasilev
 * Date: 3/20/2025
 */

@Entity
@Table(name = "request_statistics")
public class RequestStatistics implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String requestId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceName serviceName;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public RequestStatistics() {}

    public RequestStatistics(String requestId, ServiceName serviceName, String clientId, LocalDateTime timestamp) {
        this.requestId = requestId;
        this.serviceName = serviceName;
        this.clientId = clientId;
        this.timestamp = timestamp;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public void setServiceName(ServiceName serviceName) {
        this.serviceName = serviceName;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
