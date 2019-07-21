package com.meridian.ball.repository;

import static jooq.Tables.GAME;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jooq.tables.pojos.Game;
import jooq.tables.records.GameRecord;

@Repository
public class GameRepository {
    
//    List<Game> findAllByOrderByDateAsc();
//    int deleteByDate(LocalDate date);
    
    private final DSLContext create;
    
    @Autowired
    public GameRepository(DSLContext create) {
        this.create = create;
    }
    
    public boolean exists(String gameId) {
        return 1 == create.select(DSL.count()).where(GAME.GAME_ID.eq(gameId)).fetchOne(0, int.class);
    }
    
    public String store(Game game) {
        GameRecord record = create.newRecord(GAME, game);
        record.store();
        return game.getGameId();
    }
}
