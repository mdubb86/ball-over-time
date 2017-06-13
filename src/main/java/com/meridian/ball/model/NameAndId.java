package com.meridian.ball.model;

public class NameAndId {
    
    private final Player player;
    
    public NameAndId(Player player) {
        this.player = player;
    }
    
    public Integer getPlayerId() {
        return player.getPlayerId();
    }

    public String getDisplayName() {
        return player.getDisplayName();
    }
}
