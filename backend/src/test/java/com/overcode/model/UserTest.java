package com.overcode.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void shouldRejectNegativeCreditBalance() {
        assertThrows(IllegalArgumentException.class, () -> new User(1L, "alice", "alice@example.com", "secret", -1));
    }

    @Test
    void shouldRejectBlankUsername() {
        assertThrows(IllegalArgumentException.class, () -> User.create("   ", "alice@example.com", "secret"));
    }

    @Test
    void shouldRejectBlankPassword() {
        assertThrows(IllegalArgumentException.class, () -> User.create("alice", "alice@example.com", "   "));
    }
}
