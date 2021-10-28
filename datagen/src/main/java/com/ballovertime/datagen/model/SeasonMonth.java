package com.ballovertime.datagen.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

public record SeasonMonth(String seasonMonthId, String seasonId, Month month, Year year, String bbRefMonthKey) { }
