package com.egt.digital.task.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Created by: svasilev
 * Date: 3/23/2025
 */
@Component
public class RedisHealthChecker {
    private static final Logger log = LoggerFactory.getLogger(RedisHealthChecker.class);

    private final RedisConnectionFactory connectionFactory;

    @Autowired
    public RedisHealthChecker(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @PostConstruct
    public void testConnection() {
        log.info("Testing Redis connection...");
        try {
            connectionFactory.getConnection().ping();
            log.info("Redis is UP!");
        } catch (Exception e) {
            log.info("Redis test failed: " + e.getMessage());
        }
    }
}
