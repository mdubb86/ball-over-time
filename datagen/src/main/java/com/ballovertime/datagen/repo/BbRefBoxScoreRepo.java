//package com.ballovertime.datagen.repo;
//
//import com.ballovertime.datagen.model.BbRefBoxScore;
//import dev.jarcadia.dbx.Dbx;
//import dev.jarcadia.dbx.util.BatchResult;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//import org.apache.commons.dbutils.ResultSetHandler;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//
//@Singleton
//public class BbRefBoxScoreRepo {
//
//    private final Dbx dbx;
//    private final ResultSetHandler<List<BbRefBoxScore>> rsHandler;
//    private final DateTimeFormatter dtFormatter;
//
//    @Inject
//    public BbRefBoxScoreRepo(Dbx dbx) {
//        this.dbx = dbx;
//        this.dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        this.rsHandler = Dbx.listOf(rs -> new BbRefBoxScore(rs.getString("bbref_box_score_id"),
//                rs.getString("season_month_id"),
//                LocalDate.parse(rs.getString("date"), dtFormatter),
//                rs.getString("home_bbref_team_id"),
//                rs.getString("away_bbref_team_id"),
//                rs.getInt("home_team_points"),
//                rs.getInt("away_team_points")));
//
//    }
//
//    public List<BbRefBoxScore> fetchAll() {
//        return dbx.query("select * from bbref_box_score", rsHandler);
//    }
//
//    /**
//     * Fetches BbRefBoxScore records that have no linked stat records
//     */
//    public List<BbRefBoxScore> fetchEmptyBoxScores() {
//        return dbx.query("select * from bbref_box_score bs where not exists" +
//                        "(select 1 from bbref_box_score_stat where bbref_box_score_id = bs.bbref_box_score_id)",
//                rsHandler);
//    }
//
//    public List<BbRefBoxScore> merge(List<BbRefBoxScore> boxScores) {
//        int[] batchResult = dbx.batch("insert into bbref_box_score (bbref_box_score_id, season_month_id, date," +
//                        "home_bbref_team_id, away_bbref_team_id, home_team_points, away_team_points)" +
//                        "values (?, ?, ?, ?, ?, ?, ?) on conflict do nothing",
//                boxScores, bs -> new Object[]{bs.bbRefBoxScoreId(), bs.seasonMonthId(), bs.date().format(dtFormatter),
//                        bs.homeBbRefTeamId(), bs.awayBbRefTeamId(), bs.homeTeamPoints(), bs.awayTeamPoints()});
//
//        return BatchResult.getModified(boxScores, batchResult);
//    }
//
//
//}
