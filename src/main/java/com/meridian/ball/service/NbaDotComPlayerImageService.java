package com.meridian.ball.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.meridian.ball.model.Player;
import com.meridian.ball.repository.PlayerRepository;
import com.meridian.ball.service.CloudStorageService.Bucket;

@Service
public class NbaDotComPlayerImageService {
    
    private final Logger logger = LoggerFactory.getLogger(NbaDotComPlayerImageService.class);
    
    private final CloudStorageService cloudStorageService;
    private final PlayerRepository playerRepo;
    
    @Autowired
    public NbaDotComPlayerImageService(CloudStorageService cloudStorageService, PlayerRepository playerRepo) {
        this.cloudStorageService = cloudStorageService;
        this.playerRepo = playerRepo;
    }
    
    @PostConstruct
    public void storeImagesForNewPlayers() {
        for (Player player : playerRepo.findAll()) {
            if (player.getNbaDotComPlayerId() != null && player.getHasNbaDotComImage() == null) {
                logger.info("Searching for image for {} ({})", player.getName(), player.getNbaDotComPlayerId());
                Path dest = Paths.get("/tmp", player.getPlayerId() + ".png");
                try {
                    String checksum = downloadPlayerImage(player.getNbaDotComPlayerId(), dest);
                    if (checksum != null) {
                        logger.info("Successfully downloaded image for {} ({}) to {} checksum: {}", player.getName(), player.getNbaDotComPlayerId(), dest, checksum);
                        cloudStorageService.upload(Bucket.PLAYER_IMAGES, "image/png", player.getPlayerId() + ".png", dest.toFile());
                        Files.delete(dest);
                        player.setHasNbaDotComImage(true);
                        player.setNbaDotComImageChecksum(checksum);
                        playerRepo.save(player);
                    } else {
                        logger.info("Unable to find image for {} ({})", player.getName(), player.getNbaDotComPlayerId(), dest, checksum);
                        player.setHasNbaDotComImage(false);
                        playerRepo.save(player);
                    }
                } catch (Throwable t) {
                    logger.warn("Unable to download image for {} ({})", player.getName(), player.getNbaDotComPlayerId(), t);
                }
            }
        }
    }
    
    private String downloadPlayerImage(long nbaDotComPlayerId, Path dest) throws UnirestException, IOException, NoSuchAlgorithmException {
        slowItDown();
        String url = String.format("https://ak-static.cms.nba.com/wp-content/uploads/headshots/nba/latest/260x190/%d.png", nbaDotComPlayerId);
        logger.debug("Searching for player image at {}", url);
        HttpResponse<InputStream> rsp = Unirest.get(url)
                .header("content-type", "*/*")
                .asBinary();
        
        MessageDigest md = MessageDigest.getInstance("MD5");
        
        if (rsp.getStatus() == HttpStatus.SC_OK) {
            InputStream inStream = new DigestInputStream(rsp.getBody(), md);
            OutputStream outStream = new FileOutputStream(dest.toFile());
            FileCopyUtils.copy(inStream, outStream);
            return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
        } else {
            return null;
        }
    }
    
    private void slowItDown() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) { }

    }
}
