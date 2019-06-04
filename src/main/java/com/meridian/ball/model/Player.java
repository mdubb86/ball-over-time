package com.meridian.ball.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Player {
    
    @Id
    private String playerId;
    private String name;
    private String pictureUrl;
    
    private Long nbaDotComPlayerId;
    private Boolean hasNbaDotComImage;
    private String nbaDotComImageChecksum;

    public String getPlayerId() {
        return playerId;
    }
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPictureUrl() {
        return pictureUrl;
    }
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
    public Long getNbaDotComPlayerId() {
        return nbaDotComPlayerId;
    }
    public void setNbaDotComPlayerId(Long nbaDotComPlayerId) {
        this.nbaDotComPlayerId = nbaDotComPlayerId;
    }
    public Boolean getHasNbaDotComImage() {
        return hasNbaDotComImage;
    }
    public void setHasNbaDotComImage(Boolean hasNbaDotComImage) {
        this.hasNbaDotComImage = hasNbaDotComImage;
    }
    public String getNbaDotComImageChecksum() {
        return nbaDotComImageChecksum;
    }
    public void setNbaDotComImageChecksum(String nbaDotComImageChecksum) {
        this.nbaDotComImageChecksum = nbaDotComImageChecksum;
    }
}
