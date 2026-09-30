package com.overcode.controller.dto.player;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayerFilter {

    private String clubName;
    private String league;


    public PlayerFilter(String clubName, String league) {
        this.clubName = clubName;
        this.league = league;
    }

    public static PlayerFilter withClubname(String clubName) {
        return new PlayerFilter(clubName, null);
    }

    public static PlayerFilter withLeague(String league) {
        return new PlayerFilter(null, league);
    }
}
