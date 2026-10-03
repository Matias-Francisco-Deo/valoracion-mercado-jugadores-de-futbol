package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.FootballDataAPI.*;
import com.overcode.persistence.dto.external.TeamDraftDTO;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository

public class ExternalPlayerDAOFootballDataAPIImpl implements ExternalDraftPlayerDAO {

    private final WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(ExternalPlayerDAOFootballDataAPIImpl.class);

    public ExternalPlayerDAOFootballDataAPIImpl(@Value("${football-data.api-key}") String apiKey, @Value("${football-data.base_url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Auth-Token", apiKey)
                .build();
    }

    @Getter
    private final List<LeagueRequestDTO> leaguesToUse = new ArrayList<>(List.of(
                    new LeagueRequestDTO("Primera Division", "Spain"),
                    new LeagueRequestDTO("Ligue 1", "France"),
                    new LeagueRequestDTO("Premier League", "England"),
                    new LeagueRequestDTO("Bundesliga", "Germany"),
                    new LeagueRequestDTO("Serie A", "Italy")));

    public void addLeague(LeagueRequestDTO league) {
        leaguesToUse.add(league);
    }

    @Override
    public Optional<List<TeamDraftDTO>> listarEquiposDeJugadores(Integer maxTeams) {

        Optional<List<CompetitionDTO>> optionalCompetitionDTOS = getCompetitions();

        if (optionalCompetitionDTOS.isEmpty()) return Optional.empty();

        List<Optional<List<TeamDraftDTO>>> teams =
                optionalCompetitionDTOS.get().stream().map(this::getTeamsOfCompetition).toList();

        return Optional.of(teams.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(List::stream)
                .limit(maxTeams)
                .toList());
    }

    private Optional<List<TeamDraftDTO>> getTeamsOfCompetition(CompetitionDTO competition) {
        Optional<List<TeamDraftFootballDataDTO>> optionalTeams = getTeamsOfCompetitionFromAPI(competition);

        if (optionalTeams.isEmpty()) return Optional.empty();

        List<TeamDraftDTO> teams = optionalTeams.get().stream()
                .map(team -> team.toTeamDraftDTO(competition.name())).toList();

        return Optional.of(teams);
    }

    @SneakyThrows
    public Optional<List<TeamDraftFootballDataDTO>> getTeamsOfCompetitionFromAPI(CompetitionDTO competition){

        Thread.sleep(5000);

        CompetitionTeamsDTO nullableTeams = webClient.get().uri("/competitions/" + competition.id() + "/teams")
                .retrieve()
                .bodyToMono(CompetitionTeamsDTO.class)
                .onErrorResume(error -> {
                    log.error("Error occurred while fetching team: {}", error.getMessage());
                    return Mono.empty();
                })
                .block(Duration.ofSeconds(10));

        if (nullableTeams == null) return Optional.empty();

        return Optional.of(nullableTeams.teams());
    }


    public Optional<List<CompetitionDTO>> getCompetitions() {
        CompetitionsResponseDTO nullableCompetitions = webClient.get().uri("/competitions")
                .retrieve()
                .bodyToMono(CompetitionsResponseDTO.class)
                .onErrorResume(error -> {
                    log.error("Error occurred while fetching competitions: {}", error.getMessage());
                    return Mono.empty();
                })
                .block(Duration.ofSeconds(60));

        if (nullableCompetitions == null) return Optional.empty();

        List<CompetitionDTO> competitionsToUse = nullableCompetitions.competitions().stream()
                .filter(this::isBetweenRequiredLeagues).toList();

        return Optional.of(competitionsToUse);
    }

    private boolean isBetweenRequiredLeagues(CompetitionDTO competitionDTO) {
        return leaguesToUse.stream().anyMatch(league -> isRequiredLeague(competitionDTO, league)
        );
    }

    private static boolean isRequiredLeague(CompetitionDTO competitionDTO, LeagueRequestDTO league) {
        return league.name().equals(competitionDTO.name()) && league.country().equals(competitionDTO.getCountry());
    }
}
