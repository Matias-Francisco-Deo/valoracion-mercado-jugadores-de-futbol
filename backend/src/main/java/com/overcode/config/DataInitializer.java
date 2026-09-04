package com.overcode.config;

import com.overcode.persistency.dto.PlayerRecord;
import com.overcode.persistency.dto.PositionRecord;
import com.overcode.persistency.dto.UserJPADTO;
import com.overcode.persistency.repository.PlayerRepository;
import com.overcode.persistency.repository.PositionRepository;
import com.overcode.persistency.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer {

    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;

    public DataInitializer(PlayerRepository playerRepository,
                          UserRepository userRepository,
                          PositionRepository positionRepository) {
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        if (userRepository.existsByUsername("superuser")) {
            return;
        }

        UserJPADTO superUser = userRepository.save(new UserJPADTO("superuser", "superuser@market.local", "password", 0));

        List<String> names = List.of(
            "Lionel Messi",
            "Kylian Mbappé",
            "Erling Haaland",
            "Vinicius Júnior",
            "Rodri",
            "Jude Bellingham"
        );

        for (String name : names) {
            PlayerRecord player = playerRepository.save(new PlayerRecord(name, 100, 100));
            positionRepository.save(new PositionRecord(superUser.getId(), player.getId(), 100));
        }
    }
}
