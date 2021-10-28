//package com.ballovertime.datagen.service;
//
//import com.ballovertime.datagen.model.*;
//import com.ballovertime.datagen.repo.PlayerRepo;
//import com.ballovertime.datagen.service.bbref.BbRefService;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Singleton
//public class PersistService {
//
//    private final Logger logger = LoggerFactory.getLogger(PersistService.class);
//
//    private final BbRefService bbRefService;
//    private final PlayerRepo playerRepo;
//
//    @Inject
//    public PersistService(BbRefService bbRefService, PlayerRepo playerRepo) {
//        this.bbRefService = bbRefService;
//        this.playerRepo = playerRepo;
//    }
//
//    public void persistSchedule(Season season) {
//
////        logger.info("Exploring schedule for {}", season.seasonId());
////        List<String> availableMonths = bbRefService.fetchAvailableMonths(season.bbRefSeasonId());
////
////        logger.info("Discovered {} available months for {}", availableMonths.size(), season.seasonId());
////
////        List<SeasonMonth> seasonMonths = new ArrayList<>();
////        List<BbRefBoxScore> boxScores = new ArrayList<>();
////        for (String monthKey: availableMonths) {
////            logger.info("Exploring box scores for {} of {}", monthKey, season.seasonId());
////
////            Map.Entry<SeasonMonth, List<BbRefBoxScore>> entry = bbRefService
////                    .fetchMonthBoxScores(season, monthKey);
////
////            SeasonMonth seasonMonth = entry.getKey();
////            List<BbRefBoxScore> monthBoxScores = entry.getValue();
////
////            if (monthBoxScores.size() > 0) {
////                logger.info("Discovered {} box scores for {}", monthBoxScores.size(), seasonMonth.seasonMonthId());
////            }
////            seasonMonths.add(seasonMonth);
////            boxScores.addAll(monthBoxScores);
////        }
//
////        txManager.doInTransaction(() -> {
////            boolean insertedSeason = seasonRepo.merge(season);
////            int insertedSeasonMonths = seasonMonthRepo.merge(seasonMonths);
////            List<BbRefBoxScore> insertedBoxScores = boxScoreRepo.merge(boxScores);
////
////            logger.info("Season {} {}. Inserted {} season months and {} box scores.", season.seasonId(),
////                    insertedSeason ? "already exists" : "inserted", insertedSeasonMonths, insertedBoxScores.size());
////        });
//    }
//
//    public void persistBoxScore(BbRefBoxScore boxScore) {
//        List<BbRefBoxScoreStat> stats = bbRefService.fetchBoxScoreStats(boxScore);
//        logger.info("Downloaded {} stats for box score {}", stats.size(), boxScore.bbRefBoxScoreId());
//
////        txManager.doInTransaction(() -> {
////            int inserted = boxScoreStatRepo.merge(stats);
////            logger.info("Inserted {} stats for box score {}", inserted, boxScore.bbRefBoxScoreId());
////        });
//    }
//
//    public void persistPlayer(String bbrefPlayerId) {
//
//        bbRefService.fetchPlayerInfo("murrade01").onErrorStop().block();
////                .subscribe(player -> {
////
////                    logger.info("Just got this player: {}", player);
////
////                }, error -> {
////
////                    logger.error("Got this error", error);
////
////                });
//
//
//
//    }
//
//
////    public Map<Season, Map<SeasonMonth, List<BbRefBoxScore>>> exploreSchedule(boolean activeOnly) {
////
////        Map<Season, Map<SeasonMonth, List<BbRefBoxScore>>> results = new HashMap<>();
////
////        List<Season> seasons = scheduleExplorer.discoverSeasons().stream()
////                .filter(entry -> !activeOnly || entry.getValue())
////                .map(Map.Entry::getKey)
////                .collect(Collectors.toList());
////
////        logger.info("Discovered {} seasons", seasons.size());
////        for (Season season : seasons) {
////            logger.info("Exploring schedules for {}", season.seasonId());
////            List<String> availableMonths = scheduleExplorer.discoverAvailableSeasonLinkKeys(season.bbRefSeasonId());
////            logger.info("Discovered {} available months for {}", availableMonths.size(), season.seasonId());
////            for (String monthKey: availableMonths) {
////                logger.info("Exploring box scores for {} of {}", monthKey, season.seasonId());
////
////                Map.Entry<SeasonMonth, List<BbRefBoxScore>> entry = scheduleExplorer
////                        .exploreSeasonMonth(season.seasonId(), season.bbRefSeasonId(), monthKey);
////
////                SeasonMonth seasonMonth = entry.getKey();
////                List<BbRefBoxScore> boxScores = entry.getValue();
////
////                logger.info("Discovered {} box scores for {}", boxScores.size(), seasonMonth.seasonMonthId());
////
////                results.computeIfAbsent(season, s -> new HashMap<>()).put(seasonMonth, boxScores);
////            }
////        }
////        return results;
////    }
//
//
////    public List<BbRefBoxScoreStat> exploreBoxScore(BbRefBoxScore boxScore) {
////        return scheduleExplorer.analyzeBoxScore(boxScore);
////    }
//
//}
