package com.egt.digital.task.messaging;

import com.egt.digital.task.model.RequestStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
/**
 * Created by: svasilev
 * Date: 3/20/2025
 */
@Service
public class RequestStatisticsProducer {

    private static final Logger log = LoggerFactory.getLogger(RequestStatisticsProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    public RequestStatisticsProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Retryable(
            value = { AmqpConnectException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000)
    )
    public void sendStatistics(RequestStatistics stats) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, stats);
            log.info("Sent statistics: " + stats.getRequestId());
        } catch (Exception e) {
            log.info("Failed to send statistics: " + e.getMessage());
        }
    }
}