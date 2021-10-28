package com.ballovertime.datagen.service;

import com.ballovertime.datagen.model.BbRefBoxScore;
import com.ballovertime.datagen.model.Season;
import com.ballovertime.datagen.model.SeasonMonth;
import com.ballovertime.datagen.service.bbref.BbRefService;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Function;

@Singleton
public class FillService {

    private final Logger logger = LoggerFactory.getLogger(FillService.class);

    private final BbRefService bbRefService;
    private final PersistService2 persistService;

    public FillService(BbRefService bbRefService, PersistService2 persistService) {
        this.bbRefService = bbRefService;
        this.persistService = persistService;
    }

    public Mono<Void> fill() {

        return bbRefService.fetchSeasons()
                .flatMapIterable(Function.identity())
                .concatMap(season -> persistService.mergeSeason(season))
                .map(tup -> {
                    if (tup.getT2()) {
                        logger.info("Inserted season {}", tup.getT1().seasonId());
                    }
                    return tup.getT1();
                })
                .concatMap(season -> {

                    return bbRefService.fetchAvailableMonths(season)
                            .flatMapIterable(Function.identity())
                            .filter(seasonMonth -> YearMonth.now().plusMonths(1)
                                    .isAfter(seasonMonth.year().atMonth(seasonMonth.month())))
                            .map(seasonMonth -> Tuples.of(season, seasonMonth))
                            .delayElements(Duration.ofMillis(5000));

                })
                .doOnNext(tup -> logger.info("Season month {}", tup.getT2().seasonMonthId()))
                .concatMap(tup -> bbRefService.fetchMonthBoxScores(tup.getT1(), tup.getT2())
                        .flatMapIterable(Function.identity())
                        .delayElements(Duration.ofMillis(5000)))
                .concatMap(boxScore -> {
                    logger.info("Processing boxScore {}", boxScore.bbRefBoxScoreId());
                    return bbRefService.fetchBoxScoreStats(boxScore);
                })
                .then();
    }


    private record SeasonMonthKey(Season season, String monthKey){}

    private record SeasonMonthBoxScore(Season season, SeasonMonth month, BbRefBoxScore boxScore){}

}
