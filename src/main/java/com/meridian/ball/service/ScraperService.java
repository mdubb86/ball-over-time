package com.meridian.ball.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.meridian.ball.model.Player;
import com.meridian.ball.model.ScrapedBoxscore;
import com.meridian.ball.model.ScrapedGame;
import com.meridian.ball.model.Team;

public interface ScraperService {

    public Team scrapeTeam(String teamId, LocalDate date) throws IOException;
    public Player scrapePlayer(String playerId) throws IOException;
    public List<ScrapedGame> scrapeGames(LocalDate date) throws IOException;
    public List<ScrapedBoxscore> scrapeBoxscore(String gameId) throws IOException;

}
