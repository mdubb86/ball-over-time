package com.ballovertime.datagen.service.bbref;

import io.micronaut.http.uri.UriBuilder;
import jakarta.inject.Singleton;

import java.net.URI;
import java.util.Map;

@Singleton
public class BbRefUriService {

    public URI buildPlayerPageUri(String bbRefPlayerId) {
        return UriBuilder.of("https://www.basketball-reference.com")
                .path("players")
                .path("{initial}")
                .path("{id}.html")
                .expand(Map.of("initial", "m", "id", "murrade01"));
    }

    public URI buildLeaguesPageUri() {
        return UriBuilder.of("https://www.basketball-reference.com")
                .path("leagues")
                .build();
    }

    public URI buildSeasonPageUri(String bbRefSeasonId) {
        return UriBuilder.of("https://www.basketball-reference.com")
                .path("leagues")
                .path("{id}_games.html")
                .expand(Map.of("id", bbRefSeasonId));
    }

    public URI buildSeasonMonthPageUri(String bbRefSeasonId, String month) {
        return UriBuilder.of("https://www.basketball-reference.com")
                .path("leagues")
                .path("{seasonId}_games-{month}.html")
                .expand(Map.of("seasonId", bbRefSeasonId, "month", month));
    }

    public URI buildBoxScorePageUri(String bbRefBoxScoreId) {
        return UriBuilder.of("https://www.basketball-reference.com")
                .path("boxscores")
                .path("{id}.html")
                .expand(Map.of("id", bbRefBoxScoreId));
    }


}
