package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.FootballDataAPI.*;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import lombok.Getter;
import lombok.SneakyThrows;
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

    public ExternalPlayerDAOFootballDataAPIImpl(@Value("${football-data.api-key}") String apiKey, @Value("${football-data.base_url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Auth-Token", apiKey)
                .build();
    }

    @Getter
    private final List<LeagueRequestDTO> leaguesToUse = new ArrayList<>(List.of(
                    new LeagueRequestDTO("Primera Division", "Spain"), // TODO revisar si es la liga correcta
                    new LeagueRequestDTO("Ligue 1", "France"),
                    new LeagueRequestDTO("Premier League", "England"),
                    new LeagueRequestDTO("Bundesliga", "Germany"),
                    new LeagueRequestDTO("Serie A", "Italy")));

    public void addLeague(LeagueRequestDTO league) {
        leaguesToUse.add(league);
    }

    @Override
    public Optional<List<PlayerDraftDTO>> listarJugadores() {

        Optional<List<CompetitionDTO>> competitions = getCompetitions();

        return competitions.flatMap(this::getPlayersOfCompetitions);

    }

    @SneakyThrows
    private Optional<List<PlayerDraftDTO>> getPlayersOfCompetitions(List<CompetitionDTO> competitionDTOS) {
        List<Optional<List<PlayerDraftDTO>>> optionalPlayers = competitionDTOS.stream().map(this::getPlayersOfCompetition).toList();

        if (optionalPlayers.stream().allMatch(Optional::isEmpty)) return Optional.empty();

        List<PlayerDraftDTO> players = optionalPlayers.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(List::stream).toList();

        return Optional.of(players);
    }

    private Optional<List<PlayerDraftDTO>> getPlayersOfCompetition(CompetitionDTO competition) {
        Optional<List<TeamDraftDTO>> optionalTeams = getTeamsOfCompetition(competition);

        if (optionalTeams.isEmpty()) return Optional.empty();

        List<PlayerDraftDTO> players = optionalTeams.get().stream()
                .flatMap(
                        team ->
                                team.squad().stream().map(player ->
                                new PlayerDraftDTO(player.name(), team.name()
                                ))).toList();

        return Optional.of(players);
    }

    @SneakyThrows
    public Optional<List<TeamDraftDTO>> getTeamsOfCompetition(CompetitionDTO competition){

        Thread.sleep(5000);

        CompetitionTeamsDTO nullableTeams = webClient.get().uri("/competitions/" + competition.id() + "/teams")
                .retrieve()
                .bodyToMono(CompetitionTeamsDTO.class)
                .onErrorResume(error -> {
                    System.err.println("Error occurred while fetching team: " + error.getMessage());
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
                    System.err.println("Error occurred while fetching competitions: " + error.getMessage());
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
