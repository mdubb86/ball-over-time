package com.meridian.ball.repository;

import java.util.List;
import java.util.Set;

import com.meridian.ball.model.Player;

public interface PlayerRepositoryCustom {
    
    public List<Player> findPlayersByIds(Set<Integer> playerIds);

}
