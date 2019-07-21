package com.meridian.ball.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Service
public class NbaDotComService {

    private final Logger logger = LoggerFactory.getLogger(NbaDotComService.class);

    private final ObjectMapper objectMapper;

    @Autowired
    public NbaDotComService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<NbaDotComPlayerCareer> getNbaDotComPlayerCareers() throws UnirestException, JsonProcessingException, IOException {
        HttpResponse<String> rsp = Unirest.get("http://stats.nba.com/js/data/ptsd/stats_ptsd.js").asString();
        String body = rsp.getBody();
        JsonNode node = objectMapper.readTree(body.substring(body.indexOf("{"), body.length() - 1));
        JsonNode playersNode = node.get("data").get("players");
        List<NbaDotComPlayerCareer> careerTimelinesFromNbaDotCom = new ArrayList<>();
        for (JsonNode playerNode : playersNode) {
            NbaDotComPlayerCareer playerCareerTimeline = new NbaDotComPlayerCareer();
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

    public Optional<String> downloadPlayerImage(long nbaDotComPlayerId, Path dest) {
        slowItDown();
        String url = String.format("https://ak-static.cms.nba.com/wp-content/uploads/headshots/nba/latest/260x190/%d.png", nbaDotComPlayerId);
        logger.debug("Searching for player image at {}", url);
        try {
            HttpResponse<InputStream> rsp = Unirest.get(url).header("content-type", "*/*").asBinary();
            MessageDigest md = MessageDigest.getInstance("MD5");

            if (rsp.getStatus() == HttpStatus.SC_OK) {
                InputStream inStream = new DigestInputStream(rsp.getBody(), md);
                OutputStream outStream = new FileOutputStream(dest.toFile());
                FileCopyUtils.copy(inStream, outStream);
                return Optional.of(DatatypeConverter.printHexBinary(md.digest()).toLowerCase());
            } else if (rsp.getStatus() == HttpStatus.SC_FORBIDDEN) {
                logger.debug("Player image from nba.com for {} player not available", nbaDotComPlayerId);
                return Optional.empty();
            } else {
                logger.warn("Unsuccessful request to {} returned status code {} for player {}", url, rsp.getStatus(), nbaDotComPlayerId);
                return Optional.empty();
            }
        }
        catch (Throwable t) {
            logger.warn("Failed to download image player {} from {}", nbaDotComPlayerId, url, t);
            return Optional.empty();
        }
    }

    private void slowItDown() {
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e) {}

    }

    public class NbaDotComPlayerCareer {

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
