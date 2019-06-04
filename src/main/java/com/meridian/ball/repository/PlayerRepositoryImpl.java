package com.meridian.ball.repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.meridian.ball.model.Player;
import com.meridian.ball.model.PlayerCareerTimeline;

public class PlayerRepositoryImpl implements PlayerRepositoryCustom {
    
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate npjt;
    private final RowMapper<Player> rowMapper;
    private final RowMapper<PlayerCareerTimeline> careerTimelineRowMapper;
    
    public PlayerRepositoryImpl(JdbcTemplate jdbcTemplate,
            NamedParameterJdbcTemplate npjt) {
        this.jdbcTemplate = jdbcTemplate;
        this.npjt = npjt; 
        this.rowMapper = (ResultSet rs, int index) -> {
            Player player = new Player();
            player.setPlayerId(rs.getString("PLAYER_ID"));
            player.setName(rs.getString("NAME"));
            return player;
        };
        
        this.careerTimelineRowMapper = (ResultSet rs, int index) -> {
            PlayerCareerTimeline pct = new PlayerCareerTimeline();
            pct.setPlayerId(rs.getString("PLAYER_ID"));
            pct.setNbaDotComPlayerId(rs.getLong("NBA_DOT_COM_PLAYER_ID"));
            if (rs.wasNull()) {
                pct.setNbaDotComPlayerId(null);
            }
            pct.setName(rs.getString("NAME"));
            pct.setRookieYear(rs.getInt("ROOKIE_YEAR"));
            pct.setFinalYear(rs.getInt("FINAL_YEAR"));
            return pct;
        };
    }
    
    @Override
    public List<Player> findPlayersByIds(Set<String> playerIds) {
        String sql = "select player_id, name from player where player_id in (:playerIds)";
        SqlParameterSource params = new MapSqlParameterSource("playerIds", playerIds);
        return npjt.query(sql, params, rowMapper);
    }

    @Override
    public List<PlayerCareerTimeline> getPlayerCareerTimelines() {
        String sql = "select p.name, p.player_id, p.nba_dot_com_player_id, min(extract(year from g.date)) as rookie_year, max(extract(year from g.date)) as final_year from player p join stat_line sl on p.player_id = sl.player_id join game g on sl.game_id = g.game_id group by p.player_id";
        return jdbcTemplate.query(sql, careerTimelineRowMapper);
    }
}
