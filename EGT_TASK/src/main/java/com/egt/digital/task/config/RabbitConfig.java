package com.egt.digital.task.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by: svasilev
 * Date: 3/23/2025
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Queue statisticsQueue() {
        return QueueBuilder.durable("statistics-queue")
                .withArgument("x-dead-letter-exchange", "dlx-exchange")
                .withArgument("x-dead-letter-routing-key", "dlx-routing-key")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("statistics-dlq").build();
    }

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange("statistics-exchange");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx-exchange");
    }

    @Bean
    public Binding statisticsBinding() {
        return BindingBuilder
                .bind(statisticsQueue())
                .to(mainExchange())
                .with("statistics-routing-key");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("dlx-routing-key");
    }
}
