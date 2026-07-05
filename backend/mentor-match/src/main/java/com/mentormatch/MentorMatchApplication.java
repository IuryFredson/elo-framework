package com.mentormatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan({"com.mentormatch.model.entity", "com.elo.usuario"})
public class MentorMatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(MentorMatchApplication.class, args);
    }
}
