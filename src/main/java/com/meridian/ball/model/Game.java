package com.meridian.ball.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.meridian.ball.json.GameDeserializer;

@Entity
@Table
@JsonDeserialize(using = GameDeserializer.class)
public class Game {
    
    @Id
    private String gameId;
    private String gameCode;
    private LocalDate date;
    
    @ManyToOne
    @JoinColumn(name = "homeTeamId")
    private Team home;
    
    @ManyToOne
    @JoinColumn(name = "vistorTeamId")
    private Team vistor;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Team getHome() {
        return home;
    }

    public void setHome(Team home) {
        this.home = home;
    }

    public Team getVistor() {
        return vistor;
    }

    public void setVistor(Team vistor) {
        this.vistor = vistor;
    }
}