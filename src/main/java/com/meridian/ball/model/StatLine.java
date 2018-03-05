package com.meridian.ball.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.meridian.ball.json.MinutesDeserializer;

@Entity
@Table(name = "stat_line")
public class StatLine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long statLineId;
    
    @ManyToOne(cascade= {CascadeType.REMOVE})
    @JoinColumn(name = "gameId")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "playerId")
    @JsonIgnore
    private Player player;
    
    @ManyToOne
    @JoinColumn(name = "teamId")
    private Team team;

    private String startPosition;
    private String comment;
    @JsonDeserialize(using = MinutesDeserializer.class)
    private Double min;
    private Integer pts;
    private Integer ast;
    private Integer dreb;
    private Integer oreb;
    private Integer fgm;
    private Integer fga;
    private Integer fg3m;
    private Integer fg3a;
    private Integer fta;
    private Integer ftm;
    private Integer blk;
    private Integer stl;
    
    @JsonProperty("to")
    private Integer turnOvers;

    private Integer pf;
    private Double plusMinus;
    private Double offRating;
    private Double defRating;
    private Double netRating;
    private Double usgPct;
    private Double astPct;
    private Double drebPct;
    private Double orebPct;
    private Double tsPct;
    private Double efgPct;
    private Double astRatio;
    private Double tmTovPct;
    private Double astTov;
    private Double pace;
    private Double pie;
    public long getStatLineId() {
        return statLineId;
    }
    public void setStatLineId(long statLineId) {
        this.statLineId = statLineId;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    public String getStartPosition() {
        return startPosition;
    }
    public void setStartPosition(String startPosition) {
        this.startPosition = startPosition;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Double getMin() {
        return min;
    }
    public void setMin(Double min) {
        this.min = min;
    }
    public Integer getPts() {
        return pts;
    }
    public void setPts(Integer pts) {
        this.pts = pts;
    }
    public Integer getTurnOvers() {
        return turnOvers;
    }
    public void setTurnOvers(Integer turnOvers) {
        this.turnOvers = turnOvers;
    }
    public Integer getAst() {
        return ast;
    }
    public void setAst(Integer ast) {
        this.ast = ast;
    }
    public Integer getDreb() {
        return dreb;
    }
    public void setDreb(Integer dreb) {
        this.dreb = dreb;
    }
    public Integer getOreb() {
        return oreb;
    }
    public void setOreb(Integer oreb) {
        this.oreb = oreb;
    }
    public Integer getFgm() {
        return fgm;
    }
    public void setFgm(Integer fgm) {
        this.fgm = fgm;
    }
    public Integer getFga() {
        return fga;
    }
    public void setFga(Integer fga) {
        this.fga = fga;
    }
    public Integer getFg3m() {
        return fg3m;
    }
    public void setFg3m(Integer fg3m) {
        this.fg3m = fg3m;
    }
    public Integer getFg3a() {
        return fg3a;
    }
    public void setFg3a(Integer fg3a) {
        this.fg3a = fg3a;
    }
    public Integer getFta() {
        return fta;
    }
    public void setFta(Integer fta) {
        this.fta = fta;
    }
    public Integer getFtm() {
        return ftm;
    }
    public void setFtm(Integer ftm) {
        this.ftm = ftm;
    }
    public Integer getBlk() {
        return blk;
    }
    public void setBlk(Integer blk) {
        this.blk = blk;
    }
    public Integer getStl() {
        return stl;
    }
    public void setStl(Integer stl) {
        this.stl = stl;
    }
    public Integer getTurnovers() {
        return turnOvers;
    }
    public void setTurnovers(Integer turnovers) {
        this.turnOvers = turnovers;
    }
    public Integer getPf() {
        return pf;
    }
    public void setPf(Integer pf) {
        this.pf = pf;
    }
    public Double getPlusMinus() {
        return plusMinus;
    }
    public void setPlusMinus(Double plusMinus) {
        this.plusMinus = plusMinus;
    }
    public Double getOffRating() {
        return offRating;
    }
    public void setOffRating(Double offRating) {
        this.offRating = offRating;
    }
    public Double getDefRating() {
        return defRating;
    }
    public void setDefRating(Double defRating) {
        this.defRating = defRating;
    }
    public Double getNetRating() {
        return netRating;
    }
    public void setNetRating(Double netRating) {
        this.netRating = netRating;
    }
    public Double getUsgPct() {
        return usgPct;
    }
    public void setUsgPct(Double usgPct) {
        this.usgPct = usgPct;
    }
    public Double getAstPct() {
        return astPct;
    }
    public void setAstPct(Double astPct) {
        this.astPct = astPct;
    }
    public Double getDrebPct() {
        return drebPct;
    }
    public void setDrebPct(Double drebPct) {
        this.drebPct = drebPct;
    }
    public Double getOrebPct() {
        return orebPct;
    }
    public void setOrebPct(Double orebPct) {
        this.orebPct = orebPct;
    }
    public Double getTsPct() {
        return tsPct;
    }
    public void setTsPct(Double tsPct) {
        this.tsPct = tsPct;
    }
    public Double getEfgPct() {
        return efgPct;
    }
    public void setEfgPct(Double efgPct) {
        this.efgPct = efgPct;
    }
    public Double getAstRatio() {
        return astRatio;
    }
    public void setAstRatio(Double astRatio) {
        this.astRatio = astRatio;
    }
    public Double getTmTovPct() {
        return tmTovPct;
    }
    public void setTmTovPct(Double tmTovPct) {
        this.tmTovPct = tmTovPct;
    }
    public Double getAstTov() {
        return astTov;
    }
    public void setAstTov(Double astTov) {
        this.astTov = astTov;
    }
    public Double getPace() {
        return pace;
    }
    public void setPace(Double pace) {
        this.pace = pace;
    }
    public Double getPie() {
        return pie;
    }
    public void setPie(Double pie) {
        this.pie = pie;
    }
}
    