package com.meridian.ball.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class KeyDate {
    
    @Id
    private String key;
    private LocalDate date;
    
    public KeyDate() {}
    
    public KeyDate(String key, LocalDate date) {
        this.key = key;
        this.date = date;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
