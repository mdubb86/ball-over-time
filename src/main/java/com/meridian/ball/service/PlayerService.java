package com.meridian.ball.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.meridian.ball.model.Player;
import com.meridian.ball.repository.PlayerRepository;

@Service
public class PlayerService {
    
    private final PlayerRepository playerRepo;
    
    public PlayerService(PlayerRepository playerRepo) {
        this.playerRepo = playerRepo;
    }
    
    public List<Player> findByIds(String playerIdsStr) {
        Set<Integer> playerIds = Stream.of(playerIdsStr.split(","))
                .map(s -> Integer.parseInt(s))
                .collect(Collectors.toSet());
        return playerRepo.findPlayersByIds(playerIds);
    }
    
    public List<Player> findByUsernameContaining(String query) {
        return playerRepo.findByDisplayNameContainingIgnoreCaseOrderByDisplayNameDesc(query);
    }
}
