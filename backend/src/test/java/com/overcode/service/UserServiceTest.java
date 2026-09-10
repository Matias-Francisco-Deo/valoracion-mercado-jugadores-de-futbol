package com.overcode.service;

import com.overcode.model.User;
import com.overcode.service.exception.EmailRepetidoException;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.exception.NombreRepetidoException;
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
    private TestService testService;

    @BeforeEach
    void setUp() {
        USER1 = new User("alice", "alice@example.com", "secret");
        testService.eliminarUsuarios();

    }

    @Test
    void crearUnUsuarioExitosamente() {
        User created = userService.guardar(USER1);

        assertNotNull(created.getId());
        assertEquals("alice", created.getUsername());
        assertEquals("alice@example.com", created.getEmail());
    }

    @Test
    void alCrearYRecuperarElUsuarioSuBalanceDeCreditosEsCero() {
        User created = userService.guardar(USER1);
        User retrieved = userService.recuperar(created.getId());

        assertNotNull(retrieved.getId());
        assertEquals(0, retrieved.getCreditBalance());
    }

    @Test
    void alCrearYRecuperarElUsuarioSusTokensSonCero() {
        User created = userService.guardar(USER1);
        User retrieved = userService.recuperar(created.getId());

        assertNotNull(retrieved.getId());
        assertEquals(0, retrieved.getTokens().size());
    }

    @Test
    void rechazaCreacionConNombreDuplicado() {
        User user2 = new User(USER1.getUsername(), "alice2@example.com", "secret");
        userService.guardar(USER1);

        NombreRepetidoException exception = assertThrows(NombreRepetidoException.class,
                () -> userService.guardar(user2));

        assertTrue(exception.getMessage().contains(USER1.getUsername()));
    }

    @Test
    void rechazaCreacionConEmailDuplicado() {
        User user2 = new User("usuarioRepetido", USER1.getEmail(), "secret");
        userService.guardar(USER1);

        EmailRepetidoException exception = assertThrows(EmailRepetidoException.class,
                () -> userService.guardar(user2));

        assertTrue(exception.getMessage().contains(USER1.getEmail()));
    }

    @Test
    void crearSuperusuario() { // TODO testear que tenga todos los tokens
        User superuser = userService.crearSuperusuario();
        assertNotNull(superuser.getId());
    }

    @Test
    void siSuperusuarioYaExisteNoEsError() {
        userService.crearSuperusuario();
        assertDoesNotThrow(() -> userService.crearSuperusuario());
    }


    @AfterEach
    void teardown() {
        testService.eliminarUsuarios();
    }

}
