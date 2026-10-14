package com.overcode.controller;

import com.overcode.controller.dto.user.PortfolioDataDTO;
import com.overcode.controller.dto.user.PortfolioResponseDTO;
import com.overcode.controller.dto.user.UserResponseDTO;
import com.overcode.model.User;
import com.overcode.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{id}/portfolio")
    public PortfolioResponseDTO getPortfolio(
            @PathVariable Long id,
            Pageable pageable//TODO quitar este pageable y dejar una cantidad por defecto en otro lado?
    ){
        PortfolioDataDTO portfolioData = userService.recuperarPortfolio(id,pageable);

        return PortfolioResponseDTO.desdeModelo(portfolioData);
    }
}
