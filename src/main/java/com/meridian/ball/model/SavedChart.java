package com.meridian.ball.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.meridian.ball.service.StatService.Aggregation;

@Entity
@Table
public class SavedChart {

    @Id
    private String chartId;
    
    private String playerIds;
    private Stat stat;
    private Aggregation aggregation;

    public String getChartId() {
        return chartId;
    }
    public void setChartId(String chartId) {
        this.chartId = chartId;
    }
    public String getPlayerIds() {
        return playerIds;
    }
    public void setPlayerIds(String playerIds) {
        this.playerIds = playerIds;
    }
    public Stat getStat() {
        return stat;
    }
    public void setStat(Stat stat) {
        this.stat = stat;
    }
    public Aggregation getAggregation() {
        return aggregation;
    }
    public void setAggregation(Aggregation aggregation) {
        this.aggregation = aggregation;
    }
} 