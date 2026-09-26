package com.overcode.config.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "superuser")
public record SuperuserProperties(String superuserName, String superuserEmail, String superuserPassword) {
}