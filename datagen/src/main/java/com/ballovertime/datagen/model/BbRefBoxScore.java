package com.ballovertime.datagen.model;

import java.time.LocalDate;

public record BbRefBoxScore(String bbRefBoxScoreId, String seasonMonthId, LocalDate date, String homeBbRefTeamId,
                            String awayBbRefTeamId, int homeTeamPoints, int awayTeamPoints) {}


