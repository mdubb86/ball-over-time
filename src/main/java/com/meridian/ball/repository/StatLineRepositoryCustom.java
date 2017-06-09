package com.meridian.ball.repository;

import java.util.List;

import com.meridian.ball.model.Stat;

public interface StatLineRepositoryCustom {
    
    public List<Object[]> getStatOverTime(Stat stat, int playerId);
    public List<Object[]> getStatOverTimeByMonth(Stat stat, int playerId);
    public List<Object[]> getStatOverTimeBySeason(Stat stat, int playerId);

}
