package com.overcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableCaching
@EnableScheduling
public class OvercodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OvercodeApplication.class, args);
    }
}
