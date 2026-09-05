package com.overcode.config;

import com.overcode.persistence.dto.PlayerRecord;
import com.overcode.persistence.dto.PositionRecord;
import com.overcode.persistence.dto.UserJPADTO;
import com.overcode.persistence.repository.dao.PlayerDAO;
import com.overcode.persistence.repository.PositionRepository;
import com.overcode.persistence.repository.dao.UserDAO;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer {

    private final PlayerDAO playerDAO;
    private final UserDAO userDAO;
    private final PositionRepository positionRepository;

    public DataInitializer(PlayerDAO playerDAO,
                           UserDAO userDAO,
                           PositionRepository positionRepository) {
        this.playerDAO = playerDAO;
        this.userDAO = userDAO;
        this.positionRepository = positionRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        if (userDAO.existsByUsername("superuser")) {
            return;
        }

        UserJPADTO superUser = userDAO.save(new UserJPADTO("superuser", "superuser@market.local", "password", 0));

        List<String> names = List.of(
            "Lionel Messi",
            "Kylian Mbappé",
            "Erling Haaland",
            "Vinicius Júnior",
            "Rodri",
            "Jude Bellingham"
        );

        for (String name : names) {
            PlayerRecord player = playerDAO.save(new PlayerRecord(name, 100, 100));
            positionRepository.save(new PositionRecord(superUser.getId(), player.getId(), 100));
        }
    }
}
