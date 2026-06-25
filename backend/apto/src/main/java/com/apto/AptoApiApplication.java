package com.apto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.apto.config")
public class AptoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AptoApiApplication.class, args);
	}

}
