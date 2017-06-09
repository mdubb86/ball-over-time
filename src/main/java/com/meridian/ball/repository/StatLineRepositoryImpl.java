package com.meridian.ball.repository;

import java.sql.ResultSet;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.meridian.ball.model.Stat;

public class StatLineRepositoryImpl implements StatLineRepositoryCustom {
    
    private final NamedParameterJdbcTemplate npjt;
    private final RowMapper<Object[]> rowMapper;
    
    public StatLineRepositoryImpl(NamedParameterJdbcTemplate npjt) {
        this.npjt = npjt; 
        this.rowMapper = (ResultSet rs, int index) -> {
            Object[] objs = new Object[2];
            objs[0] = rs.getDate("DATE").toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
            objs[1] = rs.getDouble("VALUE");
            return objs;
        };
    }

    @Override
    public List<Object[]> getStatOverTime(Stat stat, int playerId) {
        String sql = "select date, " + stat.getDbField() + " as value from stat_line s join game g on s.game_id = g.game_id where player_id = :playerId order by g.date";
        SqlParameterSource params = new MapSqlParameterSource("playerId", playerId)
            .addValue("dbField", stat.getDbField());
        return npjt.query(sql, params, rowMapper);
    }

    @Override
    public List<Object[]> getStatOverTimeByMonth(Stat stat, int playerId) {
        String sql = "select make_date(sub.year, sub.month, 1) as date, sub.value from (select CAST(EXTRACT(year from g.date) as integer) as year, CAST(EXTRACT(month from g.date) as integer) as month, avg(" + stat.getDbField() + ") as value from stat_line s join game g on s.game_id = g.game_id  where player_id = :playerId group by EXTRACT(year from g.date), EXTRACT(month from g.date)) as sub order by date";
        SqlParameterSource params = new MapSqlParameterSource("playerId", playerId);
        return npjt.query(sql, params, rowMapper);
    }
    
    @Override
    public List<Object[]> getStatOverTimeBySeason(Stat stat, int playerId) {
        String sql = "select make_date(sub.season, 1, 1) as date, sub.value from (select CASE WHEN CAST(EXTRACT(month from g.date) as integer) > 8 THEN CAST(EXTRACT(year from g.date) as integer) ELSE CAST(EXTRACT(year from g.date) as integer) - 1 END as season, avg(" + stat.getDbField() + ") as value from stat_line s join game g on s.game_id = g.game_id where player_id = :playerId group by season) as sub order by date";
        SqlParameterSource params = new MapSqlParameterSource("playerId", playerId);
        return npjt.query(sql, params, rowMapper);
    }
}
