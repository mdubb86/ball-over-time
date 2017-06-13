package com.meridian.ball.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.meridian.ball.json.PlayerSerializer;

@Entity
@Table
@JsonSerialize(using = PlayerSerializer.class)
public class Player {
    
    @Id
    @JsonProperty("PERSON_ID")
    private Integer playerId;
    private String firstName;
    private String lastName;
    @JsonProperty("DISPLAY_FIRST_LAST")
    private String displayName;
    private String position;

    public Integer getPlayerId() {
        return playerId;
    }
    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    @Override
    public String toString() {
        return "Player [playerId=" + playerId + ", firstName=" + firstName
                + ", lastName=" + lastName + ", displayName=" + displayName
                + ", position=" + position + "]";
    }
}
