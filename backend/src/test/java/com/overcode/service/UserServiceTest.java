package com.overcode.service;

import com.overcode.model.User;
import com.overcode.persistence.repository.dao.PlayerDAOJPA;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EmailRepetidoException;
import com.overcode.service.interfaces.UserService;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest()
class UserServiceTest {

    public static User USER1;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerDAOJPA playerRepository;

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        USER1 = new User("alice", "alice@example.com", "secret", 0, 0);
        testService.eliminarUsuarios();

    }

    @Test
    void crearUnUsuarioExitosamente() {
        User created = userService.create(USER1);

        assertNotNull(created.getId());
        assertEquals("alice", created.getUsername());
        assertEquals("alice@example.com", created.getEmail());
        assertEquals(0, created.getCreditBalance());
        assertEquals(0, created.getTokens());
    }

    @Test
    void estableceTokensEnCeroCuandoEsNegativo() { // TODO hacer mejores test de service. Test para cuando al instanciar uno tiene todo en 0
        User user = USER1;
        user.setTokens(-10);

        assertEquals(0, user.getTokens());
    }

//    @Test
//    void rechazaCreacionConEmailDuplicado() {
//        playerService.crear(JUGADOR_1);
//
//        EmailRepetidoException exception = assertThrows(EmailRepetidoException.class,
//                () -> playerService.crear(JUGADOR_1));
//
//        assertTrue(exception.getMessage().contains("Messi"));
//    }

    @Test
    void estableceCreditoEnCeroCuandoEsNegativo() {
        User user = USER1;
        user.setCreditBalance(-10);

        assertEquals(0, user.getCreditBalance());
    }

    @AfterEach
    void teardown() {
        testService.eliminarUsuarios();
    }

}
