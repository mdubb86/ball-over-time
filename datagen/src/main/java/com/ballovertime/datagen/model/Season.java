package com.ballovertime.datagen.model;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Year;

@MappedEntity
public record Season(@Id String seasonId, String league, Year startYear, Year endYear, String bbRefSeasonId) {}
