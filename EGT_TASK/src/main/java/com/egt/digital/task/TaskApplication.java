package com.egt.digital.task;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication(scanBasePackages = "com.egt.digital.task")
//@Import(XmlConfig.class)
@EnableScheduling
public class TaskApplication {
	private static final Logger log = LoggerFactory.getLogger(TaskApplication.class);

	@Autowired
	Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(TaskApplication.class, args);
	}

	@PostConstruct
	public void checkProfile() {
		log.info("Active profile: " + Arrays.toString(environment.getActiveProfiles()));
	}

	@Bean
	public CommandLineRunner logHttpMessageConverters(List<HttpMessageConverter<?>> converters) {
		return args -> {
			log.info("Registered HTTP Message Converters:");
			for (HttpMessageConverter<?> converter : converters) {
				log.info(" -> {}", converter.getClass().getName());
				if (converter instanceof AbstractHttpMessageConverter<?>) {
					List<MediaType> supported = converter.getSupportedMediaTypes();
					log.info("Supported MediaTypes: {}", supported);
				}
			}
		};
	}

}
