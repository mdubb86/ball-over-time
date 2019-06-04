package com.meridian.ball.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.meridian.ball.model.Player;

public interface PlayerRepository extends CrudRepository<Player, String>, PlayerRepositoryCustom {
    
    List<Player> findByNameContainingIgnoreCaseOrderByNameDesc(String name);

}
