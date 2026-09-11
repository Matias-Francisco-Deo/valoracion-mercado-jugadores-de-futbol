package com.overcode.config;

import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer {
    private final UserService userService;
    private final PlayerService playerService;

    public DataInitializer(
            UserService userService, PlayerService playerService
    ) {
        this.userService = userService;
        this.playerService = playerService;
    }

    @PostConstruct
    @Transactional
    public void initialize() {

        userService.crearSuperusuario();
//
//        List<String> names = List.of(
//            "Lionel Messi",
//            "Kylian Mbappé",
//            "Erling Haaland",
//            "Vinicius Júnior",
//            "Rodri",
//            "Jude Bellingham"
//        );

//        for (String name : names) {
//            PlayerRecord player = playerDAO.save(new PlayerRecord(name, 100, 100));
//            // positionRepository.save(new PositionRecord(superUser.getId(), player.getId(), 100));
//        }
    }
}
