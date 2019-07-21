package com.meridian.ball.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meridian.ball.model.Stat;
import com.meridian.ball.repository.StatLineRepository;

@Service
public class StatService {

    private final StatLineRepository statRepo;
    
    public enum Aggregation {
        NONE,
        MONTH,
        SEASON
    }

    @Autowired
    public StatService(StatLineRepository statRepo) {
        this.statRepo = statRepo;
    }
    
//    public long getCount() {
//        return statRepo.count();
//    }
    
    public List<Stat> getStats() {
        return Stream.of(Stat.values())
                .sorted((s1, s2) -> s1.getDisplayName().compareTo(s2.getDisplayName()))
                .collect(Collectors.toList());
    }
    
//    public List<Object[]> getStatsOverTime(Stat stat, String playerId, Aggregation aggregation) {
//        switch (aggregation) {
//            case NONE:
//                return statRepo.getStatOverTime(stat, playerId);
//            case MONTH:
//                return statRepo.getStatOverTimeByMonth(stat, playerId);
//            case SEASON:
//                return statRepo.getStatOverTimeBySeason(stat, playerId);
//            default:
//                return Collections.emptyList();
//        }
//    }
}
