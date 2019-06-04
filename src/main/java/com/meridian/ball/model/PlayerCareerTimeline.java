package com.meridian.ball.model;

public class PlayerCareerTimeline {
    
    private String playerId;
    private Long nbaDotComPlayerId;
    private String name;
    private int rookieYear;
    private int finalYear;

    public String getPlayerId() {
        return playerId;
    }
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    public Long getNbaDotComPlayerId() {
        return nbaDotComPlayerId;
    }
    public void setNbaDotComPlayerId(Long nbaDotComPlayerId) {
        this.nbaDotComPlayerId = nbaDotComPlayerId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getRookieYear() {
        return rookieYear;
    }
    public void setRookieYear(int rookieYear) {
        this.rookieYear = rookieYear;
    }
    public int getFinalYear() {
        return finalYear;
    }
    public void setFinalYear(int finalYear) {
        this.finalYear = finalYear;
    }
}
