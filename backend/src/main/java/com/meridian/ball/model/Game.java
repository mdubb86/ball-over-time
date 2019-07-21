//package com.meridian.ball.model;
//
//import java.time.LocalDate;
//
////@Entity
////@Table
//public class Game {
//    
////    @Id
//    private String gameId;
//    private LocalDate date;
//    
////    @ManyToOne
////    @JoinColumn(name = "homeTeamId")
//    private Team home;
//    
////    @ManyToOne
////    @JoinColumn(name = "vistorTeamId")
//    private Team vistor;
//    
//    private boolean homeWin;
//
//    public String getGameId() {
//        return gameId;
//    }
//
//    public void setGameId(String gameId) {
//        this.gameId = gameId;
//    }
//
//    public LocalDate getDate() {
//        return date;
//    }
//
//    public void setDate(LocalDate date) {
//        this.date = date;
//    }
//
//    public Team getHome() {
//        return home;
//    }
//
//    public void setHome(Team home) {
//        this.home = home;
//    }
//
//    public Team getVistor() {
//        return vistor;
//    }
//
//    public void setVistor(Team vistor) {
//        this.vistor = vistor;
//    }
//
//    public boolean getHomeWin() {
//        return homeWin;
//    }
//
//    public void setHomeWin(boolean homeWin) {
//        this.homeWin = homeWin;
//    }
//}