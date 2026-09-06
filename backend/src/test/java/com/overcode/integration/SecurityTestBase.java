package com.overcode.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class SecurityTestBase {
    // Base class for security integration tests (H2, test profile)
}
