package com.overcode.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI marketOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Player Token Market API")
                .description("REST API for football player index trading and audit ledger.")
                .version("1.0.0"));
    }
}
