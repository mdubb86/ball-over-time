package com.meridian.ball.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meridian.ball.model.Game;
import com.meridian.ball.model.KeyDate;
import com.meridian.ball.model.Player;
import com.meridian.ball.model.ScrapedBoxscore;
import com.meridian.ball.model.ScrapedGame;
import com.meridian.ball.model.Stat;
import com.meridian.ball.model.StatLine;
import com.meridian.ball.model.Team;
import com.meridian.ball.repository.GameRepository;
import com.meridian.ball.repository.KeyDateRepository;
import com.meridian.ball.repository.PlayerRepository;
import com.meridian.ball.repository.StatLineRepository;
import com.meridian.ball.repository.TeamRepository;

@Service
public class StatsScraperService {

    private final Logger logger = LoggerFactory.getLogger(StatsScraperService.class);

    private final ScraperService scraperService;
    private final EntityManager entityManager;
    private final KeyDateRepository keyDateRepo;
    private final ObjectMapper objectMapper;
    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final GameRepository gameRepo;
    private final StatLineRepository statLineRepo; 
    private final TransactionTemplate transactionTemplate;

    private final Set<String> teamFields;
    private final Set<String> playerFields;
    private final Set<String> gameFields;
    private final Set<String> traditionalBoxScoreFields;
    private final Set<String> advancedBoxScoreFields;

    private final Map<Stat, Method> statMethodMap;

    private final AtomicBoolean locked;

    @Autowired
    public StatsScraperService(ScraperService scraperService,
            EntityManager entityManager,
            KeyDateRepository keyDateRepo,
            TeamRepository teamRepo,
            PlayerRepository playerRepo,
            GameRepository gameRepo,
            StatLineRepository statLineRepo,
            ObjectMapper objectMapper,
            PlatformTransactionManager transactionManager) {

        this.scraperService = scraperService;
        this.entityManager = entityManager;
        this.keyDateRepo = keyDateRepo;
        this.gameRepo = gameRepo;
        this.playerRepo = playerRepo;
        this.teamRepo = teamRepo;
        this.statLineRepo = statLineRepo;
        this.objectMapper = objectMapper;
        this.teamFields = new HashSet<>();
        this.teamFields.addAll(Arrays.asList("TEAM_ID", "ABBREVIATION", "NICKNAME", "CITY"));
        this.playerFields = new HashSet<>();
        this.playerFields.addAll(Arrays.asList("PERSON_ID", "FIRST_NAME", "LAST_NAME", "DISPLAY_FIRST_LAST", "POSITION"));
        this.gameFields = new HashSet<>();
        this.gameFields.addAll(Arrays.asList("GAME_ID", "GAMECODE", "GAME_DATE_EST", "HOME_TEAM_ID", "VISITOR_TEAM_ID"));
        this.traditionalBoxScoreFields = new HashSet<>();
        this.traditionalBoxScoreFields.addAll(Arrays.asList("TEAM_ID", "PLAYER_ID", "START_POSITION", "COMMENT", "MIN", "PTS", "FGM", "FGA",
                "FG3M", "FG3A", "FTM", "FTA", "OREB", "DREB", "AST", "STL", "BLK", "TO", "PF", "PLUS_MINUS"));
        this.advancedBoxScoreFields = new HashSet<>();
        this.advancedBoxScoreFields.addAll(Arrays.asList("PLAYER_ID", "OFF_RATING", "DEF_RATING", "NET_RATING", "AST_PCT",
                "AST_TOV", "AST_RATIO", "OREB_PCT", "DREB_PCT", "REB_RCT", "TM_TOV_PCT", "EFG_PCT", "TS_PCT", "USG_PCT", "PACE", "PIE"));
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.locked = new AtomicBoolean(false);

        statMethodMap = new HashMap<>();
        Method[] methods = StatLine.class.getDeclaredMethods();
        for (Stat stat : Stat.values()) {
            if (!stat.isCalculated()) {
                String expectedMethodName = "set" + stat.getDbAccessor().substring(0, 1).toUpperCase() + stat.getDbAccessor().substring(1);
                while (expectedMethodName.contains("_")) {
                    int index = expectedMethodName.indexOf("_");
                    expectedMethodName = expectedMethodName.substring(0, index) +
                            expectedMethodName.substring(index + 1, index + 2).toUpperCase() +
                            expectedMethodName.substring(index + 2);
                }
                for (Method method : methods) {
                    if (expectedMethodName.equals(method.getName())) {
                        statMethodMap.put(stat, method);
                    }
                }
                if (!statMethodMap.containsKey(stat)) {
                    throw new RuntimeException("Unable to map stat " + stat + " to a StatLine setter, expected to find method " + expectedMethodName);
                }

            }
        }
    }
    
    @PostConstruct
    public void runOnStartup() {
        this.start();
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void runOnSchedule() {
        this.start();
    }

    private void start() {
        if (this.locked.compareAndSet(false, true)) {
            Runnable runnable = () -> {
                this.run();
            };
            Thread thread = new Thread(runnable, "scraper");
            thread.setDaemon(true);
            thread.start();
        } else {
            logger.info("Already running scraper");
        }
    }

    private void run() {
        LocalDate date = this.getStart();
        LocalDate end = LocalDate.now();
//        LocalDate end = date.plusDays(1);
        while (date.isBefore(end)) {
            boolean success;
            try {
                success = this.processDate(date);
            } catch (Throwable t) {
                logger.error("Unexpected error processing {}", date, t);
                success = false;
            }
            if (success) {
                date = date.plusDays(1);
            } else {
                break;
            }
        }
        this.locked.set(false);
    }

    private LocalDate getStart() {
        KeyDate start = keyDateRepo.findOne("last");
        if (start == null) {
            LocalDate initial = LocalDate.parse("08/01/1949", DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            keyDateRepo.save(new KeyDate("last", initial));
            return initial;
        } else {
            return start.getDate().plusDays(1);
        }
    }

    private boolean processDate(LocalDate date) throws IOException {
        logger.info("Scraping games on {}", date);
        List<ScrapedGame> scrapedGames = scraperService.scrapeGames(date);
        logger.info("Scraped {} games on {}", scrapedGames.size(), date);

        // Remove games that have already been scraped
        scrapedGames.removeIf(scrapedGame -> gameRepo.findOne(scrapedGame.getGameId()) != null);

        // Determine unique set of teams that do not exist in the database
        Set<String> teamsToInsert = scrapedGames.stream().flatMap(game -> Stream.of(game.getAwayTeamId(), game.getHomeTeamId()))
                .filter(teamId -> teamRepo.findOne(teamId) == null)
                .collect(Collectors.toSet());

        // Insert any new teams
        if (teamsToInsert.size() > 0) {
            logger.info("Identified {} new teams", teamsToInsert.size());
            for (String teamId : teamsToInsert) {
                Team team = scraperService.scrapeTeam(teamId, date);
                teamRepo.save(team);
                logger.info("Inserted {} ({})", team.getName(), team.getTeamId());
            }
        }

        // Process each game
        for (ScrapedGame scrapedGame : scrapedGames) {
            boolean success = processScrapedGame(scrapedGame);
            if (!success) {
                return false;
            }
        }
        keyDateRepo.save(new KeyDate("last", date));
        return true;
    }

    private boolean processScrapedGame(ScrapedGame scrapedGame) throws IOException {
        // Create the db game object to insert
        Game game = new Game();
        game.setGameId(scrapedGame.getGameId());
        game.setDate(scrapedGame.getDate());
        game.setHome(entityManager.getReference(Team.class, scrapedGame.getHomeTeamId()));
        game.setVistor(entityManager.getReference(Team.class, scrapedGame.getAwayTeamId()));
        game.setHomeWin(scrapedGame.isHomeWin());

        // Scrape the boxscore for the game
        List<ScrapedBoxscore> scrapedBoxscores = scraperService.scrapeBoxscore(scrapedGame.getGameId());

        // Determine unique set of players that do not exist in the database
        Set<String> playersToInsert = scrapedBoxscores.stream()
                .map(boxscore -> boxscore.getPlayerId())
                .filter(playerId -> playerRepo.findOne(playerId) == null)
                .collect(Collectors.toSet());

        // Insert any new players
        if (playersToInsert.size() > 0) {
            logger.info("Identified {} new players", playersToInsert.size());
            for (String playerId : playersToInsert) {
                Player player = scraperService.scrapePlayer(playerId);
                playerRepo.save(player);
                logger.info("Inserted {} ({})", player.getName(), player.getPlayerId());
            }
        }

        // Create statLines to insert
        List<StatLine> statLines = scrapedBoxscores.stream().map(sb -> {
            StatLine statLine = new StatLine();
            statLine.setStatLineId(sb.getPlayerId() + "_" + sb.getGameId());
            statLine.setGame(entityManager.getReference(Game.class, sb.getGameId()));
            statLine.setTeam(entityManager.getReference(Team.class, sb.getTeamId()));
            statLine.setPlayer(entityManager.getReference(Player.class, sb.getPlayerId()));
            sb.getValues().forEach((stat, value) -> {
                try {
                    Method setter = statMethodMap.get(stat);
                    setter.invoke(statLine, value);
                } catch (Throwable t) {
                    logger.warn("Unable to invoke setter for {} with value {}", stat, value, t);
                }
            });
            return statLine;
        }).collect(Collectors.toList());

        return transactionTemplate.execute(status -> {
            try {
                gameRepo.save(game);
                statLineRepo.save(statLines);
                return true;
            } catch (Throwable t) {
                status.setRollbackOnly();
                logger.error("Unable to insert game/statlines for {}", scrapedGame.getGameId(), t);
                return false;
            }
        });
    }
}
