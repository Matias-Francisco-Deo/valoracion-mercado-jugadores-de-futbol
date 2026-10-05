package com.overcode;

import com.overcode.config.environment.FootballDataProperties;
import com.overcode.config.environment.SuperuserProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableCaching
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties({FootballDataProperties.class, SuperuserProperties.class})
public class OvercodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OvercodeApplication.class, args);
    }
}
