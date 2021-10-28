//package com.ballovertime.datagen.service;
//
//import com.ballovertime.datagen.service.bbref.BbRefService;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Singleton
//public class BackFillService {
//
//    private final Logger logger = LoggerFactory.getLogger(BackFillService.class);
//
//    private final BbRefService bbRefService;
//    private final PersistService persistService;
//
//    @Inject
//    public BackFillService(BbRefService bbRefService, PersistService persistService) {
//        this.bbRefService = bbRefService;
//        this.persistService = persistService;
//    }
//
//    public void backFillSchedule() {
////        List<Season> seasons = bbRefService.fetchSeasons();
////        for (int i=0; i<seasons.size(); i++) {
////            persistService.persistSchedule(seasons.get(i));
////        }
//    }
//    public void backFilLBoxScores() {
////        List<BbRefBoxScore> emptyBoxScore = boxScoreRepo.fetchEmptyBoxScores();
////        for (BbRefBoxScore boxScore : emptyBoxScore) {
////            persistService.persistBoxScore(boxScore);
////        }
//    }
//
//    public void backfillPlayers() {
//
////        Set<String> bbRefPlayerIds = boxScoreStatRepo.fetchDistinctBbRefPlayerIds();
////        Set<String> existingBbRefPlayerIds = playerRepo.fetchBbRefPlayerIds();
//
//
//
//
//
////        if (diff.hasAdded()) {
////            logger.info("Backfilling {} players", diff.getAdded().size());
////        } else {
////            logger.info("There are no player records to backfill");
////        }
//    }
//}
