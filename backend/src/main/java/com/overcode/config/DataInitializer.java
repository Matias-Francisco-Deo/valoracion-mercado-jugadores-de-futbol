package com.overcode.config;

import com.overcode.model.User;
import com.overcode.persistence.dto.PlayerRecord;
import com.overcode.persistence.repository.dao.PlayerDAO;
import com.overcode.persistence.repository.interfaces.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer {

    private final PlayerDAO playerDAO;
    private final UserRepository userRepository;
//    private final PositionRepository positionRepository;

    public DataInitializer(PlayerDAO playerDAO,
                           UserRepository userRepository
//                           PositionRepository positionRepository
    ) {
        this.playerDAO = playerDAO;
        this.userRepository = userRepository;
//        this.positionRepository = positionRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        if (userRepository.existsByUsername("superuser")) {
            return;
        }

        User superuser = new User("superuser", "superuser@market.local", "password", 0, 0);
        User superUser = userRepository.save(superuser);

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
