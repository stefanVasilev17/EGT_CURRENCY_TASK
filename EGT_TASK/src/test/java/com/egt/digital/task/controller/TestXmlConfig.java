package com.egt.digital.task.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;

import java.util.List;

@TestConfiguration
public class TestXmlConfig {
    @Bean
    public Jaxb2RootElementHttpMessageConverter jaxb2MessageConverter() {
        Jaxb2RootElementHttpMessageConverter converter = new Jaxb2RootElementHttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_XML,
                MediaType.valueOf("application/xml;charset=UTF-8")
        ));
        return converter;
    }
}
