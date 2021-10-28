package com.ballovertime.datagen.service;

import com.ballovertime.datagen.Config;
import com.ballovertime.datagen.model.Season;
import com.ballovertime.datagen.repo.SeasonRepo2;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Singleton
public class PersistService2 {

    private final Logger logger = LoggerFactory.getLogger(PersistService2.class);

    private final ConnectionFactory cf;
    private final SeasonRepo2 seasonRepo;

    @Inject
    public PersistService2(ConnectionFactory cf, SeasonRepo2 seasonRepo) {
        this.cf = cf;
        this.seasonRepo = seasonRepo;
    }

    public Mono<Tuple2<Season, Boolean>> mergeSeason(Season season) {
        logger.info("Merging if necessary {}", season.seasonId());
        return seasonRepo.insertIfNecessary(season)
                .map(inserted -> Tuples.of(season, inserted));
    }
}
