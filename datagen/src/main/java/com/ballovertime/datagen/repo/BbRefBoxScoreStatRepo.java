//package com.ballovertime.datagen.repo;
//
//import com.ballovertime.datagen.model.BbRefBoxScoreStat;
//import dev.jarcadia.dbx.Dbx;
//import dev.jarcadia.dbx.util.BatchResult;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//import org.apache.commons.dbutils.ResultSetHandler;
//
//import java.time.format.DateTimeFormatter;
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//
//@Singleton
//public class BbRefBoxScoreStatRepo {
//
//    private final Dbx dbx;
//    private final ResultSetHandler<List<BbRefBoxScoreStat>> rsHandler;
//    private final DateTimeFormatter dtFormatter;
//
//    @Inject
//    public BbRefBoxScoreStatRepo(Dbx dbx) {
//        this.dbx = dbx;
//        this.dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        this.rsHandler = Dbx.listOf(rs -> new BbRefBoxScoreStat(
//                rs.getString("bbref_box_score_id"),
//                rs.getString("bbref_player_id"),
//                rs.getString("player_name"),
//                rs.getString("stat"),
//                rs.getString("stat_value")
//        ));
//
//    }
//
//    public List<BbRefBoxScoreStat> fetchAll() {
//        return dbx.query("select * from bbref_box_score_stat", rsHandler);
//    }
//
//    public Set<String> fetchDistinctBbRefPlayerIds() {
//        return dbx.query("select distinct(bbref_player_id) from bbref_box_score_stat", Dbx.setOfStrings());
//    }
//
//    public int merge(Collection<BbRefBoxScoreStat> stats) {
//        return BatchResult.sum(dbx.batch("insert into bbref_box_score_stat (bbref_box_score_id, bbref_player_id, player_name," +
//                        "stat, stat_value) values (?, ?, ?, ?, ?) on conflict do nothing",
//                stats, stat -> new Object[]{stat.bbRefBoxScoreId(), stat.bbRefPlayerId(), stat.playerName(),
//                        stat.stat(), stat.statValue()}));
//    }
//
//
//}
