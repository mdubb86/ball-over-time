package com.meridian.ball.model;

public class PlayerNameAndId {
    
    private final int id;
    private final String displayName;
    
    public PlayerNameAndId(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
}
