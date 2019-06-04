package com.meridian.ball.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.meridian.ball.model.Player;
import com.meridian.ball.model.PlayerCareerTimeline;
import com.meridian.ball.repository.PlayerRepository;

@Service
public class NbaDotComPlayerService {
    
    private final Logger logger = LoggerFactory.getLogger(NbaDotComPlayerService.class);
    
    private final PlayerRepository playerRepo;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public NbaDotComPlayerService(PlayerRepository playerRepo, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.playerRepo = playerRepo;
    }
    
    public void searchForNbaDotComdPlayerIds() throws UnirestException, JsonProcessingException, IOException {
        logger.info("Searching for missing NBA.com playerIds");
        List<PlayerCareerTimeline> careerTimelinesFromDb = playerRepo.getPlayerCareerTimelines();
        List<NbaDotComPlayerCareerTimeline> careerTimelinesFromNbaDotCom = getNbaDotComPlayerCareerTimelines();
        
        for (PlayerCareerTimeline dbTimeline : careerTimelinesFromDb) {
            List<NbaDotComPlayerCareerTimeline> matches = careerTimelinesFromNbaDotCom.stream()
                    .filter(fetchedTimeline ->  dbTimeline.getName().replace(".", "").replace("'", "").equalsIgnoreCase(fetchedTimeline.getName().replace(".", "").replace("'", "")))
                    .collect(Collectors.toList());
            
            NbaDotComPlayerCareerTimeline match = null;
            if (matches.size() == 0) {
                logger.debug("No matches for {} ({} - {})", dbTimeline.getName(), dbTimeline.getRookieYear(), dbTimeline.getFinalYear());
            } else if (matches.size() == 1) {
                match = matches.get(0);
            } else if (matches.size() > 1) {
                logger.debug("Multiple matches for {} ({} - {})", dbTimeline.getName(), dbTimeline.getRookieYear(), dbTimeline.getFinalYear());
                match = findBestMatch(dbTimeline, matches);
                logger.debug("Best match is {} - {} ({} - {})", match.getName(), match.getId(), match.getRookieYear(), match.getFinalYear());
            }
            
            if (match != null && dbTimeline.getNbaDotComPlayerId() == null) {
                logger.info("Updating {} ({}) with NBA.com playerId {}", dbTimeline.getName(), dbTimeline.getPlayerId(), match.getId());
                Player player = playerRepo.findOne(dbTimeline.getPlayerId());
                player.setNbaDotComPlayerId(match.getId());
                playerRepo.save(player);
            }
        }
        logger.info("Done searching for missing NBA.com playerIds");
    }
    
    private List<NbaDotComPlayerCareerTimeline> getNbaDotComPlayerCareerTimelines() throws UnirestException, JsonProcessingException, IOException {
        HttpResponse<String> rsp = Unirest.get("http://stats.nba.com/js/data/ptsd/stats_ptsd.js").asString();
        String body = rsp.getBody();
        JsonNode node = objectMapper.readTree(body.substring(body.indexOf("{"), body.length() - 1));
        JsonNode playersNode = node.get("data").get("players");
        List<NbaDotComPlayerCareerTimeline> careerTimelinesFromNbaDotCom = new ArrayList<>();
        for (JsonNode playerNode : playersNode) {
            NbaDotComPlayerCareerTimeline playerCareerTimeline = new NbaDotComPlayerCareerTimeline();
            playerCareerTimeline.setId(playerNode.get(0).asLong());
            String name = playerNode.get(1).asText();
            int commaIndex = name.indexOf(", ");
            if (commaIndex >= 0) {
                String firstName = name.substring(commaIndex + 2);
                String lastName = name.substring(0, commaIndex);
                name = firstName + " " + lastName;
            }
            playerCareerTimeline.setName(name);
            playerCareerTimeline.setRookieYear(playerNode.get(3).asInt());
            playerCareerTimeline.setFinalYear(playerNode.get(4).asInt());
            careerTimelinesFromNbaDotCom.add(playerCareerTimeline);
        }
        return careerTimelinesFromNbaDotCom;
    }
    
    private NbaDotComPlayerCareerTimeline findBestMatch(PlayerCareerTimeline dbTimeline, List<NbaDotComPlayerCareerTimeline> matches) {
        int min = Integer.MAX_VALUE;
        NbaDotComPlayerCareerTimeline best = null;
        for (NbaDotComPlayerCareerTimeline match : matches) {
            int timelineSeparation = Math.abs(dbTimeline.getRookieYear() - match.getRookieYear()) + Math.abs(dbTimeline.getFinalYear() - match.getFinalYear());
            logger.debug("Match {} - {} ({} - {}) is separated by {} years", match.getName(), match.getId(), match.getRookieYear(), match.getFinalYear(), timelineSeparation);
            if (timelineSeparation <= min) {
                min = timelineSeparation;
                best = match;
            }
        }
        return best;
    }
    
    private class NbaDotComPlayerCareerTimeline {
        private long id;
        private String name;
        private int rookieYear;
        private int finalYear;

        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getRookieYear() {
            return rookieYear;
        }
        public void setRookieYear(int rookieYear) {
            this.rookieYear = rookieYear;
        }
        public int getFinalYear() {
            return finalYear;
        }
        public void setFinalYear(int finalYear) {
            this.finalYear = finalYear;
        }
    }
}
