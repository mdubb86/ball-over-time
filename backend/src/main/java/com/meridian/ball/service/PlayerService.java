package com.meridian.ball.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.meridian.ball.repository.PlayerRepository;
import com.meridian.ball.service.CloudStorageService.Bucket;
import com.meridian.ball.service.NbaDotComService.NbaDotComPlayerCareer;

import jooq.tables.pojos.Player;
import jooq.tables.pojos.PlayerCareer;

@Service
public class PlayerService {

    private final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private final NbaDotComService nbaDotComService;
    private final CloudStorageService cloudStorageService;
    private final PlayerRepository playerRepo;

    public PlayerService(NbaDotComService nbaDotComService, PlayerRepository playerRepo, CloudStorageService cloudStorageService) {
        this.nbaDotComService = nbaDotComService;
        this.cloudStorageService = cloudStorageService;
        this.playerRepo = playerRepo;
    }

    // public Player findById(String playerId) {
    // return playerRepo.findOne(playerId);
    // }
    //
    // public List<Player> findByIds(String playerIdsString) {
    // Set<String> playerIds = Stream.of(playerIdsString.split(","))
    // .collect(Collectors.toSet());
    // return playerRepo.findPlayersByIds(playerIds);
    // }
    //
    // public List<Player> findByUsernameContaining(String query) {
    // return playerRepo.findByNameContainingIgnoreCaseOrderByNameDesc(query);
    //

    public void downloandMissingNbaDotComPlayerImages() {
        for (Player player : playerRepo.fetchPlayersMissingNbaDotComImages()) {
            logger.info("Searching nba.com for image for {} ({})", player.getName(), player.getPlayerId());
            Path dest = Paths.get("/tmp", player.getPlayerId() + ".png");
            Optional<String> downloadedChecksum = nbaDotComService.downloadPlayerImage(player.getNbaDotComPlayerId(), dest);
            if (downloadedChecksum.isPresent()) {
                logger.info("Successfully downloaded player image for {} ({})", player.getName(), player.getPlayerId());

                try {
                    cloudStorageService.upload(Bucket.PLAYER_IMAGES, "image/png", player.getPlayerId() + ".png", dest.toFile());
                    Files.delete(dest);

                    player.setHasNbaDotComImage(true);
                    player.setNbaDotComImageChecksum(downloadedChecksum.get());
                    playerRepo.store(player);
                }
                catch (Throwable t) {
                    logger.warn("Failed to upload player image {} for {} ({})", dest, player.getName(), player.getPlayerId(), t);
                }
            }
        }
    }

    public void discoverNbaDotComPlayerIds() throws JsonProcessingException, UnirestException, IOException {
        List<Player> playersWithoutIds = playerRepo.fetchPlayersWithoutNbaDotComId();

        if (playersWithoutIds.size() > 0) {
            List<NbaDotComPlayerCareer> nbaDotComCareers = nbaDotComService.getNbaDotComPlayerCareers();
            logger.info("Searching for {} players without nba.com IDs in {} players from nba.com", playersWithoutIds.size(), nbaDotComCareers.size());

            for (Player player : playersWithoutIds) {

                List<NbaDotComPlayerCareer> matches = nbaDotComCareers.stream().filter(nbaDotComCareer -> compareNames(player.getName(), nbaDotComCareer.getName())).collect(Collectors.toList());

                Optional<NbaDotComPlayerCareer> match = findBestMatch(player, matches);

                if (match.isPresent()) {
                    logger.info("Updating {} ({}) with NBA.com playerId {}", player.getName(), player.getPlayerId(), match.get().getId());
                    player.setNbaDotComPlayerId(match.get().getId());
                    playerRepo.store(player);
                }
            }
        }
    }

    private Optional<NbaDotComPlayerCareer> findBestMatch(Player player, List<NbaDotComPlayerCareer> matches) {
        if (matches.size() == 0) {
            logger.debug("No matches for {}", player.getName());
            return Optional.empty();
        } else if (matches.size() == 1) {
            logger.debug("Found match for {}", player.getName());
            return Optional.of(matches.get(0));
        } else {
            logger.debug("Multiple matches for {}", player.getName());
            PlayerCareer career = playerRepo.fetchPlayerCareer(player.getPlayerId());

            int min = Integer.MAX_VALUE;
            NbaDotComPlayerCareer best = null;
            for (NbaDotComPlayerCareer match : matches) {
                int timelineSeparation = Math.abs(career.getRookieYear() - match.getRookieYear()) + Math.abs(career.getFinalYear() - match.getFinalYear());
                logger.debug("Match {} - {} ({} - {}) is separated by {} years", match.getName(), match.getId(), match.getRookieYear(), match.getFinalYear(), timelineSeparation);
                if (timelineSeparation <= min) {
                    min = timelineSeparation;
                    best = match;
                }
            }
            logger.debug("Best match is {} - {} ({} - {})", best.getName(), best.getId(), best.getRookieYear(), best.getFinalYear());
            return Optional.of(best);
        }
    }

    private boolean compareNames(String name1, String name2) {
        return removePuncuation(name1).equalsIgnoreCase(removePuncuation(name2));
    }

    private String removePuncuation(String str) {
        return str.replace(".", "").replace("'", "");
    }

}
