package com.apto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan({"com.apto.model.entity", "com.elo.usuario"})
@ConfigurationPropertiesScan("com.apto.config")
public class AptoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AptoApiApplication.class, args);
    }

}
