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
    
    public Player findById(String playerId) {
        return playerRepo.findOne(playerId);
    }
    
    public List<Player> findByIds(String playerIdsString) {
        Set<String> playerIds = Stream.of(playerIdsString.split(","))
                .collect(Collectors.toSet());
        return playerRepo.findPlayersByIds(playerIds);
    }

    public List<Player> findByUsernameContaining(String query) {
        return playerRepo.findByNameContainingIgnoreCaseOrderByNameDesc(query);
    }
}
