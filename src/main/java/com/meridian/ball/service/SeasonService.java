package com.meridian.ball.service;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meridian.ball.model.Game;
import com.meridian.ball.model.Season;
import com.meridian.ball.repository.GameRepository;

@Service
public class SeasonService {

    private final Logger logger = LoggerFactory.getLogger(SeasonService.class);

    @Autowired
    GameRepository gameRepo;

    @PostConstruct
    public void eliminatePreseason() {
        Iterable<Game> games = gameRepo.findAllByOrderByDateAsc();

        for(Game game : games) {
            if (isPreseason(game.getDate())) {
                logger.info("Removing {}", game.getGameCode());
                gameRepo.delete(game);
            }

        }
    }

    public boolean isPreseason(LocalDate date) {
        int month = date.get(ChronoField.MONTH_OF_YEAR);
        int seasonYear = date.get(ChronoField.YEAR);
        if (month <= 7) {
            seasonYear--;
        }
        // Find the right enum
        try {
            Season season = Season.valueOf("_" + seasonYear);
            return date.isBefore(season.getStartDate());
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }
}
