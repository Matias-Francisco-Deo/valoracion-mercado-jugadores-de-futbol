package com.overcode.config;

import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
public class DataInitializerTest {

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
    }

    @Test
    void alInvocarInitializeSeGeneraElSuperusuario() {
        assertTrue(userRepository.findByUsername("superuser").isEmpty());

        dataInitializer.initialize();

        assertTrue(userRepository.findByUsername("superuser").isPresent());
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
    }
}
