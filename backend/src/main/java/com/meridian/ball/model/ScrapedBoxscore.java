package com.meridian.ball.model;

import java.util.Map;

public class ScrapedBoxscore {
    
    private final String teamId;
    private final String playerId;
    private final String gameId;
    private final Map<Stat, Object> values;
    
    public ScrapedBoxscore(String teamId, String playerId, String gameId, Map<Stat, Object> values) {
        this.teamId = teamId;
        this.playerId = playerId;
        this.gameId = gameId;
        this.values = values;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getGameId() {
        return gameId;
    }

    public Map<Stat, Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "ScrapedBoxscore [teamId=" + teamId + ", playerId=" + playerId
                + ", gameId=" + gameId + ", values=" + values + "]";
    }
}
