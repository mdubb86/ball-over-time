package com.ballovertime.datagen.service.bbref;

import com.ballovertime.datagen.model.BbRefBoxScore;
import com.ballovertime.datagen.model.BbRefBoxScoreStat;
import com.ballovertime.datagen.model.Season;
import com.ballovertime.datagen.model.SeasonMonth;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Singleton
public class BbRefService {

    private final Logger logger = LoggerFactory.getLogger(BbRefService.class);

    private final HttpClient client;
    private final BbRefUriService uriService;
    private final BbRefParsingService parsingService;
    private final CachedBoxScoreRepo cachedBoxScoreRepo;

    @Inject
    public BbRefService(HttpClient client, BbRefUriService uriService, BbRefParsingService parsingService,
                        CachedBoxScoreRepo cachedBoxScoreRepo) {
        this.client = client;
        this.uriService = uriService;
        this.parsingService = parsingService;
        this.cachedBoxScoreRepo = cachedBoxScoreRepo;
    }

//    public Mono<Player> fetchPlayerInfo(String bbRefPlayerId) {
//        return Mono.from(client.retrieve(HttpRequest.GET(uriService.buildPlayerPageUri(bbRefPlayerId))))
//                .map(parsingService::parsePlayerPage);
//    }
//
    public Mono<List<Season>> fetchSeasons() {
        return Mono.from(client.retrieve(HttpRequest.GET(uriService.buildLeaguesPageUri())))
                .map(parsingService::parseLeaguePage);
    }

    public Mono<List<SeasonMonth>> fetchAvailableMonths(Season season) {
        return Mono.from(client.retrieve(HttpRequest.GET(uriService.buildSeasonPageUri(season.bbRefSeasonId()))))
                .map(content -> parsingService.parseSeasonMonths(season, content));
    }

    public Mono<List<BbRefBoxScore>> fetchMonthBoxScores(Season season, SeasonMonth seasonMonth) {
        return Mono.from(client.retrieve(HttpRequest.GET(uriService
                        .buildSeasonMonthPageUri(season.bbRefSeasonId(), seasonMonth.bbRefMonthKey()))))
                .map(content -> parsingService.parseSeasonMonthBoxScores(seasonMonth.seasonMonthId(), content));
    }

    public Mono<List<BbRefBoxScoreStat>> fetchBoxScoreStats(BbRefBoxScore boxScore) {
        return cachedBoxScoreRepo.loadBoxScorePageContent(boxScore.bbRefBoxScoreId())
                .flatMap(cachedContent -> {
                    if (cachedContent == null) {
                        logger.info("BoxScore {} is not cached");
                        return Mono.from(client.retrieve(HttpRequest.GET(uriService
                                .buildBoxScorePageUri(boxScore.bbRefBoxScoreId()))));
                    } else {
                        logger.info("BoxScore {} is cached");
                        return Mono.just(cachedContent);
                    }
                })
                .map(content -> parsingService.parseBoxScoreStats(boxScore, content));
    }


}

