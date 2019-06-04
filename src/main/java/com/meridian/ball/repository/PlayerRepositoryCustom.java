package com.meridian.ball.repository;

import java.util.List;
import java.util.Set;

import com.meridian.ball.model.Player;
import com.meridian.ball.model.PlayerCareerTimeline;

public interface PlayerRepositoryCustom {
    
    public List<Player> findPlayersByIds(Set<String> playerIds);
    public List<PlayerCareerTimeline> getPlayerCareerTimelines();

}
