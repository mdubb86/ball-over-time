//package com.ballovertime.datagen.repo;
//
//import com.ballovertime.datagen.model.Season;
//import com.ballovertime.datagen.model.SeasonMonth;
//import dev.jarcadia.dbx.Dbx;
//import dev.jarcadia.dbx.util.BatchResult;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//import org.apache.commons.dbutils.ResultSetHandler;
//
//import java.time.Month;
//import java.time.Year;
//import java.util.Collection;
//import java.util.List;
//
//@Singleton
//public class SeasonMonthRepo {
//
//    private final Dbx dbx;
//    private final ResultSetHandler<List<SeasonMonth>> rsHandler;
//
//    @Inject
//    public SeasonMonthRepo(Dbx dbx) {
//        this.dbx = dbx;
//        this.rsHandler = Dbx.listOf(rs -> new SeasonMonth(rs.getString("season_month_id"),
//                rs.getString("season_id"),
//                Month.of(rs.getInt("month")),
//                Year.of(rs.getInt("year")),
//                rs.getString("bbref_season_month_id")));
//    }
//
//    public List<SeasonMonth> fetchAll() {
//        return dbx.query("select * from season_month", rsHandler);
//    }
//
//    public int merge(Collection<SeasonMonth> seasonMonths) {
//        return BatchResult.sum(dbx.batch("insert into season_month (season_month_id, season_id, month, year," +
//                        "bbref_season_month_id) values (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING", seasonMonths,
//                sm -> new Object[]{sm.seasonMonthId(), sm.seasonId(), sm.month().getValue(), sm.year().getValue(),
//                        sm.bbRefSeasonMonthId()}));
//    }
//
//
//}
