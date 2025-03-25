package com.egt.digital.task.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class XmlConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(XmlConfig.class);

    @PostConstruct
    public void init() {
        log.info("XmlConfig loaded and active!");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jaxb2RootElementHttpMessageConverter xmlConverter = new Jaxb2RootElementHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_XML,
                new MediaType("application", "xml", StandardCharsets.UTF_8)
        ));
        converters.add(0, xmlConverter);
        log.info("Custom Jaxb2RootElementHttpMessageConverter registered with media types: {}", xmlConverter.getSupportedMediaTypes());
    }
}