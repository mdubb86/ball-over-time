package com.meridian.ball.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.meridian.ball.model.Player;
import com.meridian.ball.model.SavedChart;
import com.meridian.ball.model.Stat;
import com.meridian.ball.repository.PlayerRepository;
import com.meridian.ball.repository.SavedChartRepository;
import com.meridian.ball.service.StatService.Aggregation;

@Service
public class SavedChartService {

    private final Logger logger = LoggerFactory.getLogger(SavedChartService.class);

    private final SavedChartRepository savedChartRepo;
    private final PlayerRepository playerRepo;

    public SavedChartService(SavedChartRepository savedChartRepo, PlayerRepository playerRepo) {
        this.savedChartRepo = savedChartRepo;
        this.playerRepo = playerRepo;
    }
    
    public Map<String, Object> loadChart(String chartId) {
        SavedChart chart = savedChartRepo.findOne(chartId);
        List<Player> players = new ArrayList<>();
        for (String playerId : chart.getPlayerIds().split(",")) {
            Player player = playerRepo.findOne(Integer.parseInt(playerId));
            players.add(player);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("players", players);
        result.put("stat", chart.getStat().getName());
        result.put("aggregatioin", chart.getAggregation().name().toLowerCase());
        return result;
    }

    public String saveChart(Stat stat, Aggregation aggregation, String playerIds) {
        logger.info("Creating saved chart for {} {} {}", stat, aggregation, playerIds);
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
