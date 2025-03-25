package com.egt.digital.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class CustomMessageConvertersConfig {

    @Bean
    public HttpMessageConverter<Object> jaxb2RootElementHttpMessageConverter() {
        Jaxb2RootElementHttpMessageConverter xmlConverter = new Jaxb2RootElementHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_XML,
                new MediaType("application", "xml", StandardCharsets.UTF_8)
        ));
        return xmlConverter;
    }
}