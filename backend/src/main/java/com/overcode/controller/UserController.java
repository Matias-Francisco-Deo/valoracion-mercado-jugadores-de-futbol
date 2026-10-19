package com.overcode.controller;

import com.overcode.controller.dto.token.SummaryTokenPageResponseDTO;
import com.overcode.controller.dto.transaction.TransactionPageResponseDTO;
import com.overcode.controller.dto.transaction.TransactionResponseDTO;
import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.User;
import com.overcode.model.dto.SummaryTokenPlayerDTO;
import com.overcode.model.transaction.Transaction;
import com.overcode.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for user information")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        User user = userService.recuperar(id);
        UserResponseDTO dto = UserResponseDTO.desdeModelo(user);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/{userId}/tokens")
    public ResponseEntity<SummaryTokenPageResponseDTO> buscarSummaryTokensPorUsuario(
            @PathVariable Long userId,
            @RequestParam(required = false) String playerName,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        Page<SummaryTokenPlayerDTO> resultado = userService.buscarSummaryTokensPorUsuario(userId, playerName, pageable);

        SummaryTokenPageResponseDTO respuesta = SummaryTokenPageResponseDTO.desdeModelo(resultado);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{userId}/transactions")
    public  ResponseEntity<TransactionPageResponseDTO> getTransacciones(
            @PathVariable Long userId,
            Pageable pageable
    ){
        Page<Transaction> transactions = userService.recuperarTransaccionesDeUnUser(userId, pageable);

        return ResponseEntity.ok(TransactionPageResponseDTO.desdeModelo(transactions));
    }

}
