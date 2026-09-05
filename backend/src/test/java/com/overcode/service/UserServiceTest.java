package com.overcode.service;

import com.overcode.model.User;
import com.overcode.persistence.repository.PositionRepository;
import com.overcode.persistence.repository.TransactionRepository;
import com.overcode.persistence.repository.dao.PlayerDAO;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.impl.UserServiceImpl;
import com.overcode.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest()
class UserServiceTest {

//    @Configuration
//    @Import(UserServiceImpl.class)
//    static class TestConfig {
//
//        @Bean
//        UserRepository userRepository() {
//            return mock(UserRepository.class);
//        }
//
//        @Bean
//        PositionRepository positionRepository() {
//            return mock(PositionRepository.class);
//        }
//
//        @Bean
//        TransactionRepository transactionRepository() {
//            return mock(TransactionRepository.class);
//        }
//    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerDAO playerRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldCreateUser() {
        User newUser = new User(null, "alice", "alice@example.com", "secret", 0, 0);
        User created = userService.create(newUser);

        assertNotNull(created.getId());
        assertEquals("alice", created.getUsername());
        assertEquals("alice@example.com", created.getEmail());
        assertEquals(0, created.getCreditBalance());
        assertEquals(0, created.getTokens());
    }
}
