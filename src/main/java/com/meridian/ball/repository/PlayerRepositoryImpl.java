package com.meridian.ball.repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.meridian.ball.model.Player;

public class PlayerRepositoryImpl implements PlayerRepositoryCustom {
    
    private final NamedParameterJdbcTemplate npjt;
    private final RowMapper<Player> rowMapper;
    
    public PlayerRepositoryImpl(NamedParameterJdbcTemplate npjt) {
        this.npjt = npjt; 
        this.rowMapper = (ResultSet rs, int index) -> {
            Player player = new Player();
            player.setPlayerId(rs.getInt("PLAYER_ID"));
            player.setDisplayName(rs.getString("DISPLAY_NAME"));
            return player;
        };
    }
    
    @Override
    public List<Player> findPlayersByIds(Set<Integer> playerIds) {
        String sql = "select player_id, display_name from player where player_id in (:playerIds)";
        SqlParameterSource params = new MapSqlParameterSource("playerIds", playerIds);
        return npjt.query(sql, params, rowMapper);
    }
}
