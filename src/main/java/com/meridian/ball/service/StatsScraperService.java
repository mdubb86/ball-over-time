package com.meridian.ball.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.meridian.ball.StatScraperException;
import com.meridian.ball.model.Game;
import com.meridian.ball.model.KeyDate;
import com.meridian.ball.model.Player;
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

    private final SeasonService seasonService;
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

    private final AtomicBoolean locked;

    @Autowired
    public StatsScraperService(SeasonService seasonService, KeyDateRepository keyDateRepo, TeamRepository teamRepo, PlayerRepository playerRepo,
            GameRepository gameRepo, StatLineRepository statLineRepo, ObjectMapper objectMapper, PlatformTransactionManager transactionManager) {

        this.seasonService = seasonService;
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
        this.locked = new AtomicBoolean(false);

        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
        Unirest.setHttpClient(httpClient);

        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @PostConstruct
    public void startOnStartup() throws UnirestException {
        this.startProcess();
    }

    @Scheduled(cron = "0 0 6 * * ?")     // Testing expression, generates monthly every hour
    public void scheduledStart() {
        this.startProcess();
    }

    private void startProcess() {
        if (this.locked.compareAndSet(false, true)) {
            Runnable runnable = () -> {
                this.process();
            };
            Thread thread = new Thread(runnable, "scraper");
            thread.setDaemon(true);
            thread.start();
        } else {
            logger.info("Already running scraper");
        }
    }

    private void process() {
        LocalDate date = this.getStart();
        LocalDate end = LocalDate.now();
        while (date.isBefore(end) && !seasonService.isPreseason(date)) {
            boolean success = this.processGames(date);
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
            start = new KeyDate();
            start.setKey("last");
            start.setDate(LocalDate.parse("01/01/1960", DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            keyDateRepo.save(start);
        }
        return start.getDate();
    }

    private boolean processGames(LocalDate date) {
        return transactionTemplate.execute(status -> {
            try {
                logger.info("Processing games on {}", date);
                String url = String.format("http://stats.nba.com/stats/scoreboardV2?DayOffset=0&LeagueID=00&gameDate=%02d/%02d/%d", date.getMonthValue(), date.getDayOfMonth(), date.getYear());
                HttpResponse<JsonNode> res = this.get(url).asJson();
                if (res.getStatus() == HttpStatus.SC_OK) {
                    JsonNode node = res.getBody();
                    JSONArray resultSets = node.getObject().getJSONArray("resultSets");
                    for (int i=0; i<resultSets.length(); i++) {
                        JSONObject resultSet = resultSets.getJSONObject(i);
                        if ("GameHeader".equals(resultSet.get("name"))) {
                            List<Map<String, Object>> gameObjects = associate(resultSet.getJSONArray("headers"), resultSet.getJSONArray("rowSet"), gameFields);


                            for (Map<String, Object> gameObj : gameObjects) {
                                Game game = objectMapper.convertValue(gameObj, Game.class);

                                if (game.getGameCode().endsWith("ESTWST") || game.getGameCode().endsWith("WSTEST")) {
                                    logger.info("Skipping All-Star game {}", game.getGameCode());
                                    continue;
                                }

                                if (gameRepo.findOne(game.getGameId()) != null) {
                                    logger.info("Game {} ({}) has already been processed", game.getGameCode(), game.getGameId());
                                    continue;
                                }

                                int homeTeamId = (int) gameObj.get("HOME_TEAM_ID");
                                int visitorTeamId = (int) gameObj.get("VISITOR_TEAM_ID");

                                if (homeTeamId == 15010 || visitorTeamId == 15010 || 
                                        homeTeamId == 15009 || visitorTeamId == 15009 ||
                                        homeTeamId == 104 || visitorTeamId == 104 ||
                                        homeTeamId == 35 || visitorTeamId == 35 ||
                                        homeTeamId == 68 || visitorTeamId == 68 ||
                                        homeTeamId == 41 || visitorTeamId == 41 ||
                                        homeTeamId == 45 || visitorTeamId == 45 ||
                                        homeTeamId == 12303 || visitorTeamId == 12303 ||
                                        homeTeamId == 12304 || visitorTeamId == 12304 ||
                                        homeTeamId == 12305 || visitorTeamId == 12305 ||
                                        homeTeamId == 12306 || visitorTeamId == 12306 ||
                                        homeTeamId == 12307 || visitorTeamId == 12307 ||
                                        homeTeamId == 12308 || visitorTeamId == 12308 ||
                                        homeTeamId == 12309 || visitorTeamId == 12309 ||
                                        homeTeamId == 12310 || visitorTeamId == 12310 ||
                                        homeTeamId == 12311 || visitorTeamId == 12311 ||
                                        homeTeamId == 12312 || visitorTeamId == 12312 ||
                                        homeTeamId == 12313 || visitorTeamId == 12313 ||
                                        homeTeamId == 12314 || visitorTeamId == 12314 ||
                                        homeTeamId == 12315 || visitorTeamId == 12315 ||
                                        homeTeamId == 12316 || visitorTeamId == 12316 ||
                                        homeTeamId == 12317 || visitorTeamId == 12317 ||
                                        homeTeamId == 12318 || visitorTeamId == 12318 ||
                                        homeTeamId == 12319 || visitorTeamId == 12319 ||
                                        homeTeamId == 12320 || visitorTeamId == 12320 ||
                                        homeTeamId == 94    || visitorTeamId == 94) {
                                    logger.info("Skipping game with weird team(s) {}", game.getGameCode());
                                    continue;
                                }

                                Team homeTeam = findExistingTeam(homeTeamId);
                                if (homeTeam == null) {
                                    logger.info("Fetching home team information for {} in {} ({})", homeTeamId, game.getGameCode(), game.getGameId());
                                    homeTeam = fetchTeam(homeTeamId);
                                    teamRepo.save(homeTeam);
                                    logger.info("Fetched home team information for {} in {} ({})", homeTeam.getNickname(), game.getGameCode(), game.getGameId());
                                }

                                Team visitingTeam = findExistingTeam(visitorTeamId);
                                if (visitingTeam == null) {
                                    logger.info("Fetching visiting team information for {} in {}", visitorTeamId, game.getGameCode());
                                    visitingTeam = fetchTeam(visitorTeamId);
                                    teamRepo.save(visitingTeam);
                                    logger.info("Fetched visting team information for {} in {}", visitingTeam.getNickname(), game.getGameCode());
                                }

                                game.setHome(homeTeam);
                                game.setVistor(visitingTeam);
                                gameRepo.save(game);
                                this.processBoxscore(game);
                            }
                        }
                    }
                }
                keyDateRepo.save(new KeyDate("last", date));
                return true;
            } catch (Throwable t) {
                logger.error("Unable to process games on {}", date, t);
                status.setRollbackOnly();
                return false;
            }
        });
    }

    public Team findExistingTeam(int teamId) {
        return teamRepo.findOne(teamId);
    }

    public Team fetchTeam(int teamId) throws UnirestException {
        String url = String.format("http://stats.nba.com/stats/teamdetails?teamID=%s", teamId);
        HttpResponse<JsonNode> res = this.get(url).asJson();
        if (res.getStatus() == HttpStatus.SC_OK) {
            JsonNode node = res.getBody();
            JSONArray resultSets = node.getObject().getJSONArray("resultSets");
            for (int i=0; i<resultSets.length(); i++) {
                JSONObject resultSet = resultSets.getJSONObject(i);
                if ("TeamBackground".equals(resultSet.get("name"))) {
                    List<Map<String, Object>> teamObjects = associate(resultSet.getJSONArray("headers"), resultSet.getJSONArray("rowSet"), teamFields);
                    return objectMapper.convertValue(teamObjects.get(0), Team.class);
                }
            }
        }
        return null;
    }

    private void processBoxscore(Game game) throws UnirestException {
        logger.info("Fetching boxscore for {}", game.getGameCode());
        String gameId = game.getGameId();
        Map<Integer, Map<String, Object>> traditionalBoxscores = fetchTraditionalBoxscore(gameId);
        Map<Integer, Map<String, Object>> advancedBoxscores = fetchAdvancedBoxscore(gameId);

        for (Entry<Integer, Map<String, Object>> entry : traditionalBoxscores.entrySet()) {
            if (entry != null && entry.getKey() != null) {
                int playerId = entry.getKey();
                Map<String, Object> fields = entry.getValue();

                // Merge advanced metrics, if available
                if (advancedBoxscores.containsKey(playerId)) {
                    fields.putAll(advancedBoxscores.get(playerId));
                }
                StatLine statLine = objectMapper.convertValue(fields, StatLine.class);
                statLine.setGame(game);
                statLine.setTeam(findExistingTeam((int) fields.get("TEAM_ID")));
                Player player = findExistingPlayer(playerId);
                if (player == null) {
                    logger.info("Looking up unknown player {}", playerId);
                    player = fetchPlayer(playerId);

                    if (player == null) {
                        logger.error("Unable to find player {}", playerId);
                        continue;
                    }
                    playerRepo.save(player);
                }
                statLine.setPlayer(player);

                // See if statline already exists
                StatLine exists = statLineRepo.findOneByPlayerAndGame(player, game);
                if (exists == null) {
                    logger.info("Created statline for {} in {}", statLine.getPlayer().getDisplayName(), statLine.getGame().getGameCode());
                    statLineRepo.save(statLine);
                } else {
                    statLine.setStatLineId(exists.getStatLineId());
                    logger.info("Updated statline for {} in {}", statLine.getPlayer().getDisplayName(), statLine.getGame().getGameCode());
                    statLineRepo.save(statLine);
                }
            }
        }
    }

    private Map<Integer, Map<String, Object>> fetchTraditionalBoxscore(String gameId) throws UnirestException {
        String url = String.format("http://stats.nba.com/stats/boxscoretraditionalv2?GameID=%s&EndPeriod=10&EndRange=28800&RangeType=0&StartPeriod=1&StartRange=0", gameId);
        HttpResponse<JsonNode> res = this.get(url).asJson();
        Map<Integer, Map<String, Object>> boxscoreByPlayerId = new HashMap<>();
        if (res.getStatus() == HttpStatus.SC_OK) {
            JsonNode node = res.getBody();
            JSONArray resultSets = node.getObject().getJSONArray("resultSets");
            for (int i=0; i<resultSets.length(); i++) {
                JSONObject resultSet = resultSets.getJSONObject(i);
                if ("PlayerStats".equals(resultSet.get("name"))) {
                    List<Map<String, Object>> boxscoreObjects = associate(resultSet.getJSONArray("headers"), resultSet.getJSONArray("rowSet"), traditionalBoxScoreFields);
                    for (Map<String, Object> boxscore : boxscoreObjects) {
                        boxscoreByPlayerId.put((Integer) boxscore.get("PLAYER_ID"), boxscore);
                    }
                }
            }
        }
        return boxscoreByPlayerId;
    }

    private Map<Integer, Map<String, Object>> fetchAdvancedBoxscore(String gameId) throws UnirestException {
        String url = String.format("http://stats.nba.com/stats/boxscoreadvancedv2?GameID=%s&EndPeriod=10&EndRange=28800&RangeType=0&StartPeriod=1&StartRange=0", gameId);
        HttpResponse<JsonNode> res = this.get(url).asJson();
        Map<Integer, Map<String, Object>> boxscoreByPlayerId = new HashMap<>();
        if (res.getStatus() == HttpStatus.SC_OK) {
            JsonNode node = res.getBody();
            JSONArray resultSets = node.getObject().getJSONArray("resultSets");
            for (int i=0; i<resultSets.length(); i++) {
                JSONObject resultSet = resultSets.getJSONObject(i);
                if ("PlayerStats".equals(resultSet.get("name"))) {
                    List<Map<String, Object>> boxscoreObjects = associate(resultSet.getJSONArray("headers"), resultSet.getJSONArray("rowSet"), advancedBoxScoreFields);
                    for (Map<String, Object> boxscore : boxscoreObjects) {
                        boxscoreByPlayerId.put((Integer) boxscore.get("PLAYER_ID"), boxscore);
                    }
                }
            }
        }
        return boxscoreByPlayerId;
    }

    public Player findExistingPlayer(int playerId) {
        return playerRepo.findOne(playerId);
    }

    public Player fetchPlayer(int playerId) throws UnirestException {
        String url = String.format("http://stats.nba.com/stats/commonplayerinfo?LeagueID=00&PlayerID=%s&SeasonType=Regular+Season", playerId);
        HttpResponse<JsonNode> res = this.get(url).asJson();
        if (res.getStatus() == HttpStatus.SC_OK) {
            JsonNode node = res.getBody();
            JSONArray resultSets = node.getObject().getJSONArray("resultSets");
            for (int i=0; i<resultSets.length(); i++) {
                JSONObject resultSet = resultSets.getJSONObject(i);
                if ("CommonPlayerInfo".equals(resultSet.get("name"))) {
                    List<Map<String, Object>> teamObjects = associate(resultSet.getJSONArray("headers"), resultSet.getJSONArray("rowSet"), playerFields);
                    return objectMapper.convertValue(teamObjects.get(0), Player.class);
                }
            }
        }
        return null;
    }

    private List<Map<String, Object>> associate(JSONArray headers, JSONArray rowSets, Set<String> fields) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i=0; i<rowSets.length(); i++) {
            JSONArray row = rowSets.getJSONArray(i);
            logger.debug("Row: {}", row);
            Map<String, Object> obj = new HashMap<>();
            for (int j=0; j<headers.length(); j++) {
                String header = headers.getString(j);
                if (fields.contains(header) && !row.isNull(j)) {
                    logger.debug("Row val for {}: {} ({})", header, row.get(j), row.get(j).getClass());
                    obj.put(header, row.get(j));
                } else {
                    logger.debug("Row val for {}: {} ({}) is ignored", header, row.get(j), row.get(j).getClass());
                }
            }
            result.add(obj);
        }
        return result;
    }

    private GetRequest get(String url) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) { }
        return Unirest.get(url)
                .header("Connection", "keep-alive")
                .header("Host", "stats.nba.com")
                .header("Referer", "http://stats.nba.com/scores/")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("x-nba-stats-origin", "stats")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:42.0) Gecko/20100101 Firefox/42.0")
                .header("Accept", "application/json, text/plain, */*")
                .header("x-nba-stats-token", "true");
    }
}
