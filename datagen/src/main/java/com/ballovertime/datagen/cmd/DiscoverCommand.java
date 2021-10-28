package com.ballovertime.datagen.cmd;

import com.ballovertime.datagen.model.BbRefBoxScore;
import com.ballovertime.datagen.model.BbRefBoxScoreStat;
import com.ballovertime.datagen.service.FillService;
import io.micronaut.configuration.picocli.PicocliRunner;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CommandLine.Command(name = "discover", description = "...",
        mixinStandardHelpOptions = true)
public class DiscoverCommand implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(DiscoverCommand.class);

    /**
     *
     * Flow
     *
     * Season(s) + merge > Season Month(s) + merge >
     *
     */

    private final FillService fillService;

    @Inject
    public DiscoverCommand(FillService fillService) {
        this.fillService = fillService;
    }


    public void run() {

        fillService.fill()
                .block();





//        backFillService.backfillPlayers();

//        persistService.persistPlayer("murrade01");





//        Map<Season, Map<SeasonMonth, List<BbRefBoxScore>>> discovered = discoveryService.exploreSchedule(false);
//
//        List<Season> existingSeasons = seasonRepo.fetchAll();
//        Diff<Season> seasonDiff = Diff.diff(existingSeasons, discovered.keySet(), Comparator.comparing(Season::seasonId));
//
//        if (seasonDiff.hasAdded()) {
//            int inserted = seasonRepo.insert(seasonDiff.getAdded());
//            logger.info("Inserted {} seasons: {}", inserted, seasonDiff.getAdded());
//        } else {
//            logger.info("No new seasons discovered ({} existing)", existingSeasons.size());
//        }
//
//        List<SeasonMonth> discoveredSeasonMonths = discovered.values().stream()
//                .flatMap(map -> map.keySet().stream())
//                .collect(Collectors.toList());
//        List<SeasonMonth> existingSeasonMonths = seasonMonthRepo.fetchAll();
//        Diff<SeasonMonth> seasonMonthDiff = Diff.diff(existingSeasonMonths, discoveredSeasonMonths,
//                Comparator.comparing(SeasonMonth::seasonMonthId));
//
//        if (seasonMonthDiff.hasAdded()) {
//            int inserted = seasonMonthRepo.insert(seasonMonthDiff.getAdded());
//            logger.info("Inserted {} season months: {}", inserted, seasonMonthDiff.getAdded());
//        } else {
//            logger.info("No new season months discovered ({} existing)", existingSeasonMonths.size());
//        }
//
//        List<BbRefBoxScore> discoveredBoxScores = discovered.values().stream()
//                .flatMap(map -> map.entrySet().stream().flatMap(e -> e.getValue().stream()))
//                .collect(Collectors.toList());
//        List<BbRefBoxScore> existingBoxScores = boxScoreRepo.fetchAll();
//        Diff<BbRefBoxScore> boxScoreDiff = Diff.diff(existingBoxScores, discoveredBoxScores,
//                Comparator.comparing(BbRefBoxScore::bbRefBoxScoreId));
//
//        if (boxScoreDiff.hasAdded()) {
//            int inserted = boxScoreRepo.insert(boxScoreDiff.getAdded());
//            logger.info("Inserted {} box scores: {}", inserted, boxScoreDiff.getAdded());
//        } else {
//            logger.info("No new box scores discovered ({} existing)", existingBoxScores.size());
//        }
//
//        List<BbRefBoxScore> unexploredBoxScores = boxScoreRepo.fetchUnexplored();
//        logger.info("There are {} unexplored box scores", unexploredBoxScores.size());
//        for (BbRefBoxScore boxScore : unexploredBoxScores) {
//
//            logger.info("Exploring box score {}", boxScore.bbRefBoxScoreId());
//            List<BbRefBoxScoreStat> stats = discoveryService.exploreBoxScore(boxScore);
//
//            List<BbRefBoxScoreStat> tidiedStats = tidy(boxScore, stats);
//            logger.info("Discovered {} stats in box score {}", tidiedStats.size(), boxScore.bbRefBoxScoreId());
//
//            statRepo.insert(tidiedStats);
//        }
    }

    private List<BbRefBoxScoreStat> tidy(BbRefBoxScore boxScore, List<BbRefBoxScoreStat> stats) {
        // Grouped by player > stat
        // If any lists are a length greater than 1, something weird happened
        Map<String, Map<String, List<BbRefBoxScoreStat>>> groupedStats = stats.stream()
                .collect(Collectors.groupingBy(BbRefBoxScoreStat::bbRefPlayerId,
                        Collectors.groupingBy(BbRefBoxScoreStat::stat)));


        List<BbRefBoxScoreStat> result = new ArrayList<>();
        for (String bbRefPlayerId : groupedStats.keySet()) {
            for (String stat : groupedStats.get(bbRefPlayerId).keySet()) {
                List<BbRefBoxScoreStat> group = groupedStats.get(bbRefPlayerId).get(stat);
                Set<String> values = group.stream()
                        .map(BbRefBoxScoreStat::statValue)
                        .collect(Collectors.toSet());

                if (values.size() > 1) {
                    logger.warn("Box score {} reports {} {} with different values {}", boxScore.bbRefBoxScoreId(),
                            bbRefPlayerId, stat, values);
                }

                result.add(group.get(0));
            }
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(DiscoverCommand.class, args);
    }
}
