package com.ballovertime.datagen.repo;

import com.ballovertime.datagen.model.Season;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;

@R2dbcRepository(dialect = Dialect.H2)
public abstract class SeasonRepo2 implements ReactorCrudRepository<Season, String> {

    @Query("select * from season where season_id = :seasonId for update")
    public abstract Mono<Season> findBySeasonIdForUpdate(String seasonId);

    @Transactional
    public Mono<Boolean> insertIfNecessary(Season season) {
        return findBySeasonIdForUpdate(season.seasonId())
                .flatMap(existing -> existing == null ?  this.save(season).thenReturn(true) : Mono.just(false));
    }

}
