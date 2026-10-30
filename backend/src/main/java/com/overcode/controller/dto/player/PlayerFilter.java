package com.overcode.controller.dto.player;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerFilter {

    private String clubName;
    private String league;
    private String position;


    public PlayerFilter(String clubName, String league, String position) {
        this.clubName = clubName;
        this.league = league;
        this.position = position;
    }

    public static PlayerFilter withClubname(String clubName) {
        return new PlayerFilter(clubName, null,null);
    }

    public static PlayerFilter withLeague(String league) {
        return new PlayerFilter(null, league,null);
    }

    public static PlayerFilter withPosition(String position) {
        return new PlayerFilter(null, null,position);
    }
}
