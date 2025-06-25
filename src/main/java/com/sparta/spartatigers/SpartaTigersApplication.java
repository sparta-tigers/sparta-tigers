package com.sparta.spartatigers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpartaTigersApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpartaTigersApplication.class, args);
    }
}
