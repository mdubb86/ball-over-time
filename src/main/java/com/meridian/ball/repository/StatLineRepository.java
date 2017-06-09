package com.meridian.ball.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.meridian.ball.model.Game;
import com.meridian.ball.model.Player;
import com.meridian.ball.model.StatLine;

public interface StatLineRepository extends CrudRepository<StatLine, Long>, StatLineRepositoryCustom {

    StatLine findOneByPlayerAndGame(Player player, Game game);
    
    List<StatLine> findByPlayerPlayerIdOrderByGameDate(int playerId);
    
}
