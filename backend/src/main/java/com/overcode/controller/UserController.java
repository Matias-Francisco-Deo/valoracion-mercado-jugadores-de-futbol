package com.overcode.controller;

import com.overcode.controller.dto.user.CreateUserRequestDTO;
import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.User;
import com.overcode.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/users")
//    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
//        User userModelo = request.aModelo();
//        User userCreado = userService.guardar(userModelo);
//        UserResponseDTO dto = UserResponseDTO.desdeModelo(userCreado);
//        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
//    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        User user = userService.recuperar(id);
        UserResponseDTO dto = UserResponseDTO.desdeModelo(user);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

}
