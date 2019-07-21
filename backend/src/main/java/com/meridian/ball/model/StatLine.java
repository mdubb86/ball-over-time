//package com.meridian.ball.model;
//
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
////@Entity
////@Table(name = "stat_line")
//public class StatLine {
//    
////    @Id
//    private String statLineId;
//    
////    @ManyToOne
////    @JoinColumn(name = "gameId")
//    private Game game;
//
////    @ManyToOne
////    @JoinColumn(name = "playerId")
//    @JsonIgnore
//    private Player player;
//    
////    @ManyToOne
////    @JoinColumn(name = "teamId")
//    private Team team;
//
//    private Double min;
//    private Integer fgm;
//    private Integer fga;
//    private Integer fg3m;
//    private Integer fg3a;
//    private Integer fta;
//    private Integer ftm;
//    private Integer oreb;
//    private Integer dreb;
//    private Integer ast;
//    private Integer blk;
//    private Integer tov;
//    private Integer stl;
//    private Integer pf;
//    private Integer pts;
//    private Integer plusMinus;
//    private Double tsPct;
//    private Double orebPct;
//    private Double drebPct;
//    private Double astPct;
//    private Double blkPct;
//    private Double tovPct;
//    private Double usgPct;
//    private Integer offRating;
//    private Integer defRating;
//    public String getStatLineId() {
//        return statLineId;
//    }
//    public void setStatLineId(String statLineId) {
//        this.statLineId = statLineId;
//    }
//    public Game getGame() {
//        return game;
//    }
//    public void setGame(Game game) {
//        this.game = game;
//    }
//    public Player getPlayer() {
//        return player;
//    }
//    public void setPlayer(Player player) {
//        this.player = player;
//    }
//    public Team getTeam() {
//        return team;
//    }
//    public void setTeam(Team team) {
//        this.team = team;
//    }
//    public Double getMin() {
//        return min;
//    }
//    public void setMin(Double min) {
//        this.min = min;
//    }
//    public Integer getFgm() {
//        return fgm;
//    }
//    public void setFgm(Integer fgm) {
//        this.fgm = fgm;
//    }
//    public Integer getFga() {
//        return fga;
//    }
//    public void setFga(Integer fga) {
//        this.fga = fga;
//    }
//    public Integer getFg3m() {
//        return fg3m;
//    }
//    public void setFg3m(Integer fg3m) {
//        this.fg3m = fg3m;
//    }
//    public Integer getFg3a() {
//        return fg3a;
//    }
//    public void setFg3a(Integer fg3a) {
//        this.fg3a = fg3a;
//    }
//    public Integer getFta() {
//        return fta;
//    }
//    public void setFta(Integer fta) {
//        this.fta = fta;
//    }
//    public Integer getFtm() {
//        return ftm;
//    }
//    public void setFtm(Integer ftm) {
//        this.ftm = ftm;
//    }
//    public Integer getOreb() {
//        return oreb;
//    }
//    public void setOreb(Integer oreb) {
//        this.oreb = oreb;
//    }
//    public Integer getDreb() {
//        return dreb;
//    }
//    public void setDreb(Integer dreb) {
//        this.dreb = dreb;
//    }
//    public Integer getAst() {
//        return ast;
//    }
//    public void setAst(Integer ast) {
//        this.ast = ast;
//    }
//    public Integer getBlk() {
//        return blk;
//    }
//    public void setBlk(Integer blk) {
//        this.blk = blk;
//    }
//    public Integer getTov() {
//        return tov;
//    }
//    public void setTov(Integer tov) {
//        this.tov = tov;
//    }
//    public Integer getStl() {
//        return stl;
//    }
//    public void setStl(Integer stl) {
//        this.stl = stl;
//    }
//    public Integer getPf() {
//        return pf;
//    }
//    public void setPf(Integer pf) {
//        this.pf = pf;
//    }
//    public Integer getPts() {
//        return pts;
//    }
//    public void setPts(Integer pts) {
//        this.pts = pts;
//    }
//    public Integer getPlusMinus() {
//        return plusMinus;
//    }
//    public void setPlusMinus(Integer plusMinus) {
//        this.plusMinus = plusMinus;
//    }
//    public Double getTsPct() {
//        return tsPct;
//    }
//    public void setTsPct(Double tsPct) {
//        this.tsPct = tsPct;
//    }
//    public Double getOrebPct() {
//        return orebPct;
//    }
//    public void setOrebPct(Double orebPct) {
//        this.orebPct = orebPct;
//    }
//    public Double getDrebPct() {
//        return drebPct;
//    }
//    public void setDrebPct(Double drebPct) {
//        this.drebPct = drebPct;
//    }
//    public Double getAstPct() {
//        return astPct;
//    }
//    public void setAstPct(Double astPct) {
//        this.astPct = astPct;
//    }
//    public Double getBlkPct() {
//        return blkPct;
//    }
//    public void setBlkPct(Double blkPct) {
//        this.blkPct = blkPct;
//    }
//    public Double getTovPct() {
//        return tovPct;
//    }
//    public void setTovPct(Double tovPct) {
//        this.tovPct = tovPct;
//    }
//    public Double getUsgPct() {
//        return usgPct;
//    }
//    public void setUsgPct(Double usgPct) {
//        this.usgPct = usgPct;
//    }
//    public Integer getOffRating() {
//        return offRating;
//    }
//    public void setOffRating(Integer offRating) {
//        this.offRating = offRating;
//    }
//    public Integer getDefRating() {
//        return defRating;
//    }
//    public void setDefRating(Integer defRating) {
//        this.defRating = defRating;
//    }
//    
//    
//}