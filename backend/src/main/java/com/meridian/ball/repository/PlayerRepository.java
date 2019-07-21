package com.meridian.ball.repository;

import static jooq.Tables.GAME;
import static jooq.Tables.PLAYER;
import static jooq.Tables.PLAYER_CAREER;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import jooq.tables.pojos.Player;
import jooq.tables.pojos.PlayerCareer;
import jooq.tables.records.PlayerRecord;

public class PlayerRepository  {
    
    private final DSLContext create;
    
    @Autowired
    public PlayerRepository(DSLContext create) {
        this.create = create;
    }

    
    public int count() {
        return create.selectCount().from(PLAYER).fetchOne(0, int.class);
    }
    
    public Player fetchOne(String playerId) {
        return create.select().from(PLAYER).where(PLAYER.PLAYER_ID.eq(playerId)).fetchOne().into(Player.class);
    }
    
    public boolean exists(String playerId) {
        return 1 == create.select(DSL.count()).where(PLAYER.PLAYER_ID.eq(playerId)).fetchOne(0, int.class);
    }
    
    public List<Player> fetchPlayersWithoutNbaDotComId() {
        return create.select().from(PLAYER).where(PLAYER.NBA_DOT_COM_PLAYER_ID.isNull()).fetch().into(Player.class);
    }
    
    public List<Player> fetchPlayersMissingNbaDotComImages() {
        return create.select().from(PLAYER)
                .where(PLAYER.NBA_DOT_COM_PLAYER_ID.isNotNull())
                .and(PLAYER.HAS_NBA_DOT_COM_IMAGE.isNull())
                .fetch().into(Player.class);
    }
    
    public PlayerCareer fetchPlayerCareer(String playerId) {
        return create.select().from(PLAYER_CAREER).where(PLAYER.PLAYER_ID.eq(playerId)).fetchOne().into(PlayerCareer.class);
    }
    
    public String store(Player player) {
        PlayerRecord record = create.newRecord(PLAYER, player);
        record.store();
        return player.getPlayerId();
    }
    
//    List<Player> findByNameContainingIgnoreCaseOrderByNameDesc(String name);
//  public PlayerRepositoryImpl(JdbcTemplate jdbcTemplate,
//  NamedParameterJdbcTemplate npjt) {
//this.jdbcTemplate = jdbcTemplate;
//this.npjt = npjt; 
//this.rowMapper = (ResultSet rs, int index) -> {
//  Player player = new Player();
//  player.setPlayerId(rs.getString("PLAYER_ID"));
//  player.setName(rs.getString("NAME"));
//  return player;
//};
//
//this.careerTimelineRowMapper = (ResultSet rs, int index) -> {
//  PlayerCareerTimeline pct = new PlayerCareerTimeline();
//  pct.setPlayerId(rs.getString("PLAYER_ID"));
//  pct.setNbaDotComPlayerId(rs.getLong("NBA_DOT_COM_PLAYER_ID"));
//  if (rs.wasNull()) {
//      pct.setNbaDotComPlayerId(null);
//  }
//  pct.setName(rs.getString("NAME"));
//  pct.setRookieYear(rs.getInt("ROOKIE_YEAR"));
//  pct.setFinalYear(rs.getInt("FINAL_YEAR"));
//  return pct;
//};
//}
//
//@Override
//public List<Player> findPlayersByIds(Set<String> playerIds) {
//String sql = "select player_id, name from player where player_id in (:playerIds)";
//SqlParameterSource params = new MapSqlParameterSource("playerIds", playerIds);
//return npjt.query(sql, params, rowMapper);
//}
//
//@Override
//public List<PlayerCareerTimeline> getPlayerCareerTimelines() {
//String sql = "select p.name, p.player_id, p.nba_dot_com_player_id, min(extract(year from g.date)) as rookie_year, max(extract(year from g.date)) as final_year from player p join stat_line sl on p.player_id = sl.player_id join game g on sl.game_id = g.game_id group by p.player_id";
//return jdbcTemplate.query(sql, careerTimelineRowMapper);
//}

}
