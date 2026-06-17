package com.compasschat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CompassChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompassChatApplication.class, args);
    }
}
