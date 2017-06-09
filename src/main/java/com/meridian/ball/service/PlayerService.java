package com.meridian.ball.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.meridian.ball.model.PlayerNameAndId;
import com.meridian.ball.repository.PlayerRepository;

@Service
public class PlayerService {
    
    private final PlayerRepository playerRepo;
    
    public PlayerService(PlayerRepository playerRepo) {
        this.playerRepo = playerRepo;
    }
    
    public List<PlayerNameAndId> findByUsernameContaining(String query) {
        return playerRepo.findByDisplayNameContainingIgnoreCaseOrderByDisplayNameDesc(query).stream()
                .map(p -> new PlayerNameAndId(p.getPlayerId(), p.getDisplayName()))
                .collect(Collectors.toList());
    }
}
