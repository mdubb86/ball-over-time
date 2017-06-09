package com.meridian.ball.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.meridian.ball.model.SavedChart;
import com.meridian.ball.model.Stat;
import com.meridian.ball.repository.SavedChartRepository;
import com.meridian.ball.service.StatService.Aggregation;

@Service
public class SavedChartService {
    
    private final SavedChartRepository savedChartRepo;
    
    public SavedChartService(SavedChartRepository savedChartRepo) {
        this.savedChartRepo = savedChartRepo;
    }
    
    public String saveChart(Stat stat, Aggregation aggregation, String playerIds) {
        String id = nextId();
        SavedChart chart = new SavedChart();
        chart.setChartId(id);
        chart.setPlayerIds(playerIds);
        chart.setStat(stat);
        chart.setAggregation(aggregation);
        savedChartRepo.save(chart);
        return id;
    }
    
    private String nextId() {
        return UUID.randomUUID().toString().replace("-", "").substring(8);
    }
}
