package com.example.testdrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TestdriveApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestdriveApplication.class, args);
    }
}
