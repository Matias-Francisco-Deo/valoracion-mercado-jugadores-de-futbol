package com.overcode.testUtils;

import jakarta.servlet.Filter;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

@TestConfiguration
    public class DateMockConfiguration {

        @Bean
        RequestDate requestDate() {
            return new RequestDate();
        }

        @Bean
        FilterRegistrationBean<Filter> dateMockingFilter(RequestDate requestDate) {
            Filter filter = (request, response, chain) -> {
                LocalDate date = requestDate.getDate();
                try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
                    dates.when(() -> LocalDate.now(ZoneOffset.UTC)).thenReturn(date);
                    chain.doFilter(request, response);
                }
            };
            FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>(filter);
            registration.addUrlPatterns("/*");
            registration.setOrder(Integer.MIN_VALUE);
            return registration;
        }
    }