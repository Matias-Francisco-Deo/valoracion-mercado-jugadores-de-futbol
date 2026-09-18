package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.FootballDataAPI.*;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class ExternalPlayerDAOFootballDataAPIImpl implements ExternalPlayerDAOFootballDataAPI {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.football-data.org/v4")
            .defaultHeader("X-Auth-Token", "") // TODO poner Api Key en .env
            .build();

    @Getter
    private final List<LeagueRequestDTO> leaguesToUse = new ArrayList<>(List.of
            (
                    new LeagueRequestDTO("La Liga Play-Offs", "Spain"),
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

    private Optional<List<PlayerDraftDTO>> getPlayersOfCompetitions(List<CompetitionDTO> competitionDTOS) {
        List<Optional<List<PlayerDraftDTO>>> optionalPlayers = competitionDTOS.stream().map(this::getPlayersOfCompetition).toList();

        if (optionalPlayers.stream().allMatch(Optional::isEmpty)) return Optional.empty();

        List<PlayerDraftDTO> players = optionalPlayers.stream().filter(Optional::isPresent).map(Optional::get).flatMap(List::stream).toList();

        return Optional.of(players);
    }

    private Optional<List<PlayerDraftDTO>> getPlayersOfCompetition(CompetitionDTO competition) {
        Optional<List<TeamDTO>> teams = getTeamsOfCompetition(competition);

        if (teams.isEmpty()) return Optional.empty();
        List<Optional<List<PlayerDraftDTO>>> optionalPlayers = teams.get().stream().map(this::getPlayersOfTeam).toList();

        if (optionalPlayers.stream().allMatch(Optional::isEmpty)) return Optional.empty();

        List<PlayerDraftDTO> players = optionalPlayers.stream().filter(Optional::isPresent).map(Optional::get).flatMap(List::stream).toList();

        return Optional.of(players);
    }

    private Optional<List<TeamDTO>> getTeamsOfCompetition(CompetitionDTO competition) {
    }

    private Optional<List<PlayerDraftDTO>> getPlayersOfTeam(TeamDTO team) {


    }


    public Optional<List<CompetitionDTO>> getCompetitions() {
        CompetitionsResponseDTO nullableCompetitions = webClient.get().uri("/competitions")
                .retrieve()
                .bodyToMono(CompetitionsResponseDTO.class)
                .onErrorResume(error -> {
                    System.err.println("Error occurred while fetching competitions: " + error.getMessage());
                    return Mono.empty();
                })
                .block(Duration.ofSeconds(10));

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
