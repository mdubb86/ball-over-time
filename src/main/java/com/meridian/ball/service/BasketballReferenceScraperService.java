package com.meridian.ball.service;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.meridian.ball.model.Player;
import com.meridian.ball.model.ScrapedBoxscore;
import com.meridian.ball.model.ScrapedGame;
import com.meridian.ball.model.Stat;
import com.meridian.ball.model.Team;

@Service
public class BasketballReferenceScraperService implements ScraperService {

    private final Logger logger = LoggerFactory.getLogger(BasketballReferenceScraperService.class);

    public Team scrapeTeam(String teamId, LocalDate date) throws IOException {
        String url = String.format("https://www.basketball-reference.com/teams/%s/%d.html", teamId.toUpperCase(), date.getMonthValue() > 7 ? date.getYear() + 1: date.getYear());
        logger.info("Scraping team {} from {}", teamId, url);
        Document doc = get(url);
        String name = doc.select("h1 span").get(1).text();

        Team team = new Team();
        team.setTeamId(teamId);
        team.setName(name);
        return team;
    }

    public Player scrapePlayer(String playerId) throws IOException {
        String url = String.format("https://www.basketball-reference.com/players/%s/%s.html", playerId.substring(0, 1), playerId);
        logger.info("Scraping player {} from {}", playerId, url);
        Document doc = get(url);
        Player player = new Player();
        player.setPlayerId(playerId);
        player.setName(doc.select("h1").text());
        if (doc.select("#info img").size() > 0) {
            player.setPictureUrl(doc.select("#info img").get(0).attr("src"));
        }
        return player;
    }

    public List<ScrapedGame> scrapeGames(LocalDate date) throws IOException {
        String url = String.format("https://www.basketball-reference.com/boxscores/?month=%d&day=%d&year=%d", date.getMonthValue(), date.getDayOfMonth(), date.getYear());
        logger.info("Processing games on {} at {}", date, url);
        Document doc = get(url);
        List<ScrapedGame> games = new ArrayList<>();
        for (Element gameSummary : doc.select(".game_summary")) {
            String gameId = extractGameIdFromLink(gameSummary.select("td.gamelink a").attr("href"));
            String awayTeamId = extractTeamIdFromLink(gameSummary.select("tr").get(0).select("td").get(0).select("a").attr("href"));
            String homeTeamId = extractTeamIdFromLink(gameSummary.select("tr").get(1).select("td").get(0).select("a").attr("href"));
            boolean homeWin = gameSummary.select("tr").get(1).hasClass("winner");
            logger.debug("Found {} - {} vs {} {}", gameId, awayTeamId, homeTeamId, homeWin);
            games.add(new ScrapedGame(gameId, date, awayTeamId, homeTeamId, homeWin));
        }
        return games;
    }

    public List<ScrapedBoxscore> scrapeBoxscore(String gameId) throws IOException {
        String url = String.format("https://www.basketball-reference.com/boxscores/%s.html", gameId);
        logger.info("Processing boxscore for {} at {}", gameId, url);
        Document gameDoc = get(url);
        Map<String, Map<String, Map<Stat, Object>>> result = new HashMap<>();
        for (Element statsTable : gameDoc.select(".stats_table")) {
            String teamId = extractTeamIdFromStatsTableId(statsTable.attr("id"));
            Map<String, Map<Stat, Object>> teamMap = result.get(teamId);
            if (teamMap == null) {
                teamMap = new HashMap<>();
                result.put(teamId, teamMap);
            }
            for (Element statRowEl : statsTable.select("tbody tr:not(.thead)")) {
                String playerId = extractPlayerIdFromLink(statRowEl.select("th a").attr("href"));
                Map<Stat, Object> playerMap = teamMap.get(playerId);
                if (playerMap == null) {
                    playerMap = new HashMap<>();
                    teamMap.put(playerId, playerMap);
                }
                for (Element statEl : statRowEl.select("td")) {
                    String name = statEl.attr("data-stat");
                    String value = statEl.text();
                    Optional<Stat> optStat = Stat.fromBbRefName(name);
                    if (optStat.isPresent()) {
                        Stat stat = optStat.get();
                        logger.debug("Found stat {}: {} for {}", stat, value, playerId);
                        try {
                            playerMap.put(stat, stat.parse(value));
                        } catch (Throwable t) {
                            logger.warn("Unable to parse {} as a valid value for {}", value, stat);
                        }
                    } else {
                        logger.debug("Ignoring unrecognized stat {}", name);
                    }
                }
            }
        }
        // Remove empty player maps
        result.forEach((teamId, playerMaps) -> playerMaps.values().removeIf(playerMap -> playerMap.isEmpty()));

        // Convert to ScrapedBoxscore List
        List<ScrapedBoxscore> list = new ArrayList<>();
        result.forEach((teamId, playerMaps) -> playerMaps.forEach((playerId, statMap) -> list.add(new ScrapedBoxscore(teamId, playerId, gameId, statMap))));
        return list;
    }

    private String extractGameIdFromLink(String link) {
        try {
            int start = link.lastIndexOf("/") + 1;
            int end = link.indexOf(".html");
            return link.substring(start, end);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to extract playerId from " + link, t);
        } 
    }

    private String extractTeamIdFromLink(String link) {
        try {
            int end = link.lastIndexOf("/");
            int start = link.lastIndexOf("/", end - 1) + 1;
            return link.substring(start, end).toLowerCase();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to extract teamId from " + link, t);
        }
    }

    private String extractTeamIdFromStatsTableId(String id) {
        try {
            int start = id.indexOf("_") + 1;
            int end = id.lastIndexOf("_");
            return id.substring(start, end);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to extract teamId from " + id, t);
        }
    }

    private String extractPlayerIdFromLink(String link) {
        try {
            int start = link.lastIndexOf("/") + 1;
            int end = link.indexOf(".html");
            return link.substring(start, end);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to extract playerId from " + link, t);
        }
    }

    public static void main(String[] args) throws IOException {
//        BasketballReferenceScraperService scraper = new BasketballReferenceScraperService();
//        LocalDate date = LocalDate.parse("03/07/1950", DateTimeFormatter.ofPattern("MM/dd/yyyy"));
//        for (ScrapedGame game : scraper.scrapeGames(date)) {
//            System.out.println(game);
//            System.out.println(scraper.scrapeBoxscore(game.getGameId()));
//        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        List<Integer> list = new LinkedList<>();
        list.add(2);
        list.add(3);
        list.add(5);
        
        
        Node node1 = new Node(2);
        Node node2 = new Node(3);
        Node node3 = new Node(5);
        node1.next = node2;
        node2.next = node3;
        
        System.out.println(iterativeSum(node1));
        System.out.println(recursiveSum(node1));
    }
    
    public static int iterativeSum(Node node) {
        int sum = 0;
        while (node != null) {
            sum += node.val;
            node = node.next;
        }
        return sum;
    }
    
    public static int recursiveSum(Node node) {
        if (node.next == null) {
            return node.val;
        }
        return node.val + recursiveSum(node.next);
    }
    
    private static class Node {
        private int val;
        private Node next;
        private Node(int val) {
            this.val = val;
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    private Document get(String url) throws IOException {
        int attempts = 1;
        Throwable failureReason = null;
        while (attempts <= 5) {
            slowItDown();
            try {
                return Jsoup.connect(url).validateTLSCertificates(false).get();
            } catch (SocketException | SocketTimeoutException | HttpStatusException ex) {
                failureReason = ex;
                logger.warn("Network exception fetching {} on attempt {}", url, attempts);
                slowItDown();
                attempts++;
            }
        }
        throw new IOException("Failed to fetch " + url, failureReason);
    }

    private void slowItDown() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) { }

    }
}
