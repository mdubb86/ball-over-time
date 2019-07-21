package com.meridian.ball.model;

import java.time.LocalDate;

public class ScrapedGame {
    
    private final String gameId;
    private final LocalDate date;
    private final String awayTeamId;
    private final String homeTeamId;
    private final boolean homeWin;

    public ScrapedGame(String gameId, LocalDate date, String awayTeamId, String homeTeamId, boolean homeWin) {
        this.gameId = gameId;
        this.date = date;
        this.awayTeamId = awayTeamId;
        this.homeTeamId = homeTeamId;
        this.homeWin = homeWin;
    }

    public String getGameId() {
        return gameId;
    }
    
    public LocalDate getDate() {
        return date;
    }

    public String getAwayTeamId() {
        return awayTeamId;
    }

    public String getHomeTeamId() {
        return homeTeamId;
    }

    public boolean isHomeWin() {
        return homeWin;
    }

    @Override
    public String toString() {
        return "ScrapedGame [gameId=" + gameId + ", awayTeamId=" + awayTeamId
                + ", homeTeamId=" + homeTeamId + ", homeWin=" + homeWin + "]";
    }
}
