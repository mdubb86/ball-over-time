package com.meridian.ball.repository;

import static jooq.Tables.STAT_LINE;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jooq.tables.pojos.StatLine;
import jooq.tables.records.StatLineRecord;

@Repository
public class StatLineRepository {
    
    private final DSLContext create;
    
    @Autowired
    public StatLineRepository(DSLContext create) {
        this.create = create;
    }
    
    public void store(List<StatLine> statLines) {
        List<StatLineRecord> records = statLines.stream()
                .map(s -> create.newRecord(STAT_LINE, s))
                .collect(Collectors.toList());
        create.batchStore(records);
    }

//    StatLine findOneByPlayerAndGame(Player player, Game game);
//    List<StatLine> findByPlayerPlayerIdOrderByGameDate(int playerId);
//  public StatLineRepositoryImpl(NamedParameterJdbcTemplate npjt) {
//  this.npjt = npjt; 
//  this.rowMapper = (ResultSet rs, int index) -> {
//      Object[] objs = new Object[2];
//      objs[0] = rs.getDate("DATE").toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
//      objs[1] = rs.getDouble("VALUE");
//      return objs;
//  };
//}
//
////@Override
////public int deleteByDate(LocalDate date) {
////  String sql = "delete from stat_line where date == :date";
////  SqlParameterSource params = new MapSqlParameterSource().addValue("date", date, Types.DATE);
////  return npjt.update(sql, params);
////}
}
