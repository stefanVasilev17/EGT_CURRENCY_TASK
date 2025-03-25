package com.egt.digital.task.messaging;

import com.egt.digital.task.model.RequestStatistics;
import com.egt.digital.task.repository.RequestStatisticsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by: svasilev
 * Date: 3/23/2025
 */
@Component
public class RequestStatisticsConsumer {

    private static final Logger log = LoggerFactory.getLogger(RequestStatisticsConsumer.class);

    private static final String RETRY_HEADER = "x-retry-count";
    private static final int MAX_RETRIES = 3;

    private final RequestStatisticsRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RequestStatisticsConsumer(RequestStatisticsRepository repository,
                                     RabbitTemplate rabbitTemplate,
                                     ObjectMapper objectMapper) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * RabbitMQ message listener with manual ACK, retry mechanism, DLQ fallback and processing timer
     */
    @RabbitListener(queues = "request_stats_queue", ackMode = "MANUAL")
    public void receiveMessage(RequestStatistics stats,
                               Message message,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        long startTime = System.currentTimeMillis();

        try {
            log.info("Received message: {}", stats);

            if (stats.getRequestId() == null || stats.getClientId() == null || stats.getServiceName() == null) {
                log.warn("Invalid message structure: {}", stats);
                channel.basicReject(tag, false);
                return;
            }

            if (repository.existsByRequestId(stats.getRequestId())) {
                log.info("Duplicate requestId: {} – message will be acknowledged", stats.getRequestId());
                channel.basicAck(tag, false);
                return;
            }

            repository.save(stats);
            log.info("Successfully saved statistics to DB: {}", stats);

            channel.basicAck(tag, false);

        } catch (Exception e) {
            log.error("Error while processing message: {}", e.getMessage(), e);

            try {
                int retryCount = getRetryCount(message);

                if (retryCount < MAX_RETRIES) {
                    log.warn("Retrying message – attempt {} of {}: {}", retryCount + 1, MAX_RETRIES, stats);
                    requeueWithRetry(stats, retryCount + 1);
                    channel.basicAck(tag, false);
                } else {
                    log.error("Max retries reached. Sending message to DLQ: {}", stats);
                    channel.basicReject(tag, false);
                }

            } catch (Exception retryEx) {
                log.error("Failed during retry fallback logic: {}", retryEx.getMessage(), retryEx);
                try {
                    channel.basicReject(tag, false);
                } catch (Exception failedToReject) {
                    log.error("Failed to reject message: {}", failedToReject.getMessage());
                }
            }
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("Processing time: {} ms for requestId: {}", (endTime - startTime), stats.getRequestId());
        }
    }

    /**
     * Extracts retry count from the message headers
     */
    private int getRetryCount(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        if (headers.containsKey(RETRY_HEADER)) {
            Object value = headers.get(RETRY_HEADER);
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                log.warn("Invalid retry header format: {}", value);
            }
        }
        return 0;
    }

    /**
     * Re-publishes the message to the queue with an incremented retry count
     */
    private void requeueWithRetry(RequestStatistics stats, int retryCount) throws Exception {
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        properties.setHeader(RETRY_HEADER, retryCount);

        byte[] jsonBody = objectMapper.writeValueAsBytes(stats);
        Message retryMessage = new Message(jsonBody, properties);

        rabbitTemplate.send("statistics-exchange", "statistics-routing-key", retryMessage);
        log.info("Requeued message with retryCount={}: {}", retryCount, stats.getRequestId());
    }
}
