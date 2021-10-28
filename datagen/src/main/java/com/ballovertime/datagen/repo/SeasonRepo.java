//package com.ballovertime.datagen.repo;
//
//import com.ballovertime.datagen.model.Season;
//import dev.jarcadia.dbx.Dbx;
//import dev.jarcadia.dbx.ValuesProducer;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//import org.apache.commons.dbutils.ResultSetHandler;
//
//import java.time.Year;
//import java.util.Collection;
//import java.util.List;
//
//@Singleton
//public class SeasonRepo {
//
//    private final Dbx dbx;
//    private final ResultSetHandler<List<Season>> rsHandler;
//
//    @Inject
//    public SeasonRepo(Dbx dbx) {
//        this.dbx = dbx;
//        this.rsHandler = Dbx.listOf(rs -> new Season(rs.getString("season_id"),
//                rs.getString("league"),
//                Year.of(rs.getInt("start_year")),
//                Year.of(rs.getInt("end_year")),
//                rs.getString("bbref_season_id")));
//    }
//
//    public List<Season> fetchAll() {
//        return dbx.query("select * from season", rsHandler);
//    }
//
//    public boolean merge(Season season) {
//        return 1 == dbx.update("insert into season (season_id, league, start_year, end_year, bbref_season_id) " +
//                "values (?, ?, ?, ?, ?) on conflict do nothing", season.seasonId(), season.league(), season.startYear(), season.endYear(),
//                season.bbRefSeasonId());
//    }
//
//
//}
