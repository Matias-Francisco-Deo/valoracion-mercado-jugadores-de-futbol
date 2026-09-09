package com.overcode.config;

import com.overcode.model.User;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.PlayerService;
import com.overcode.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer {
    private final UserRepository userRepository;
    private final PlayerService playerService;

    public DataInitializer(
            UserRepository userRepository, PlayerService playerService
    ) {
        this.userRepository = userRepository;
        this.playerService = playerService;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        if (userRepository.existsByUsername("superuser")) { // usar service?
            return;
        }

        User superuser = new User("superuser", "superuser@market.local", "password", 0, 0);
        userRepository.guardar(superuser);

        List<String> names = List.of(
            "Lionel Messi",
            "Kylian Mbappé",
            "Erling Haaland",
            "Vinicius Júnior",
            "Rodri",
            "Jude Bellingham"
        );

//        for (String name : names) {
//            PlayerRecord player = playerDAO.save(new PlayerRecord(name, 100, 100));
//            // positionRepository.save(new PositionRecord(superUser.getId(), player.getId(), 100));
//        }
    }
}
