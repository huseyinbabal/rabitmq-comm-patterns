package com.example.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RabbitmqCommPatternsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqCommPatternsApplication.class, args);
    }
}