package com.egt.digital.task.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by: svasilev
 * Date: 3/24/2025
 */
@Component
public class HttpMessageConvertersLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(HttpMessageConvertersLogger.class);

    @Autowired
    private List<HttpMessageConverter<?>> converters;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Registered HTTP Message Converters:");
        for (HttpMessageConverter<?> converter : converters) {
            log.info("  -> {}", converter.getClass().getName());
            log.info("     Supported MediaTypes: {}", converter.getSupportedMediaTypes());
        }
    }
}