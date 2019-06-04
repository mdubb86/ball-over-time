package com.meridian.ball.model;

import java.util.Optional;

public enum Stat {
    MINUTES("min", "mp", "Minutes Played", "Minutes Played", StatDataType.TIME, true, false),
    FIELD_GOALS_MADE("fgm", "fg", "Field Goals", "Field Goals Made", StatDataType.INT, true, false),
    FIELD_GOALS_ATTEMPTED("fga", "fga", "Field Goals Attempted", "fga", StatDataType.INT, true, false),
    FIELD_GOAL_PERCENTAGE("fga / nullif(fgm, 0)", null, "FG%", "Field Goal Percentage", StatDataType.DOUBLE, true, true),
    THREE_POINTERS_MADE("fg3m", "fg3", "3 Pointers", "3 Pointers Made", StatDataType.INT, true, false),
    THREE_POINTERS_ATTEMPTED("fg3a", "fg3a", "3 Pointers Attempted", "3 Pointers Attempted", StatDataType.INT, true, false),
    THREE_POINT_PERCENTAGE("fg3m / nullif(fg3a, 0)", null, "3P%", "Three Point Percentage", StatDataType.DOUBLE, true, true),
    FREE_THROWS_MADE("ftm", "ft", "Free Throws", "Free Throws Made", StatDataType.INT, true, false),
    FREE_THROWS_ATTEMPTED("fta", "fta", "Free Throws Attempted", "Free Throws Attempted", StatDataType.INT, true, false),
    FREE_THROW_PERCENTAGE("ftm / nullif(fta, 0)", null, "FT%", "Free Throw Percentage", StatDataType.DOUBLE, true, true),
    OFFENSIVE_REBOUNDS("oreb", "orb", "Offensive Rebounds", "Offensive Rebounds", StatDataType.INT, true, false),
    DEFENSIVE_REBOUNDS("dreb", "drb", "Defensive Rebounds", "Defensive Rebounds", StatDataType.INT, true, false),
    REBOUNDS("oreb + dreb", null, "Rebounds", "Total Rebounds", StatDataType.INT, true, true),
    ASSISTS("ast", "ast", "Assists", "Assists", StatDataType.INT, true, false),
    BLOCKED_SHOTS("blk", "blk", "Blocks", "Blocked Shots", StatDataType.INT, true, false),
    TURNOVERS("tov", "tov", "Tunnovers", "Turnovers", StatDataType.INT, true, false),
    STEALS("stl", "stl", "Steals", "Steals", StatDataType.INT, true, false),
    PERSONAL_FOULS("pf", "pf", "Personal Fouls", "Personal Fouls", StatDataType.INT, true, false),
    POINTS("pts", "pts", "Points", "Points", StatDataType.INT, true, false),
    PLUS_MINUS("plus_minus", "plus_minus", "+/-", "Plus-Minus", StatDataType.INT, true, false),
    TRUE_SHOOTING_PERCENTAGE("ts_pct", "ts_pct", "True Shooting Percentage", "A measure of shooting efficiency that takes into account 2-point field goals, 3-point field goals, and free throws.", StatDataType.DOUBLE, true, false),
    EFFECTIVE_FIELD_GOAL_PERCENTAGE("(fgm + 0.5 * fg3m) / nullif(fga, 0)", null, "Effective Field Goal Percentage", "This statistic adjusts for the fact that a 3-point field goal is worth one more point than a 2-point field goal.", StatDataType.DOUBLE, true, true),
    OFFENSIVE_REBOUND_PERCENTAGE("oreb_pct", "orb_pct", "Offensive Rebound Percentage", "An estimate of the percentage of available offensive rebounds a player grabbed while he was on the floor.", StatDataType.DOUBLE, true, false),
    DEFENSIVE_REBOUND_PERCENTAGE("dreb_pct", "drb_pct", "Defensive Rebound Percentage", "An estimate of the percentage of available defensive rebounds a player grabbed while he was on the floor.", StatDataType.DOUBLE, true, false),
    REBOUND_PERCENTAGE("oreb_pct / nullif(dreb_pct, 0)", null, "Rebound Percentage", "An estimate of the percentage of available rebounds a player grabbed while he was on the floor.", StatDataType.DOUBLE, true, true),
    ASSIST_PERCENTAGE("ast_pct", "ast_pct", "Assist Percentage", "An estimate of the percentage of teammate field goals a player assisted while he was on the floor.", StatDataType.DOUBLE, true, false),
    BLOCK_PERCENTAGE("blk_pct", "blk_pct", "Block Percentage", "An estimate of the percentage of opponent two-point field goal attempts blocked by the player while he was on the floor.", StatDataType.DOUBLE, true, false),
    TURNOVER_PERCENTAGE("tov_pct", "tov_pct", "Turnover Percentage", "An estimate of turnovers committed per 100 plays.", StatDataType.DOUBLE, true, false),
    USAGE_PERCENTAGE("usg_pct", "usg_pct", "Usage Percentage", "An estimate of the percentage of team plays used by a player while he was on the floor.", StatDataType.DOUBLE, true, false),
    OFFESNSIVE_RATING("off_rating", "off_rtg", "Offensive Rating", "An estimate of points produced (players) or scored (teams) per 100 possessions.", StatDataType.INT, true, false),
    DEFENSIVE_RATING("def_rating", "def_rtg", "Defensive Rating", "An estimate of points allowed per 100 possessions", StatDataType.INT, true, false),
    //    NET_RATING("NETRTG", "Net Rating", "net_rating", false),
    ;

    private final String dbAccessor;
    private final String bbRefName;
    private final String displayName;
    private final StatDataType dataType;
    private final boolean traditional;
    private final boolean calculated;

    private Stat(String dbField, String bbRefName, String displayName,
            String description, StatDataType dataType, boolean traditional, boolean calculated) {
        this.bbRefName = bbRefName;
        this.displayName = displayName;
        this.dbAccessor = dbField;
        this.dataType = dataType;
        this.traditional = traditional;
        this.calculated = calculated;
    }

    public String getBbRefName() {
        return bbRefName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDbAccessor() {
        return dbAccessor;
    }

    public boolean isTraditional() {
        return traditional;
    }
    
    public boolean isCalculated() {
        return calculated;
    }

    public String getName() {
        return name().toLowerCase().replace("_", "-");
    }

    public static Optional<Stat> fromBbRefName(String bbRefName) {
        for (Stat stat : Stat.values()) {
            if (bbRefName.equals(stat.getBbRefName())) {
                return Optional.of(stat);
            }
        }
        return Optional.empty();
    }

    public Object parse(String value) {
        if (value == null || "".equals(value)) {
            return null;
        }
        switch (this.dataType) {
            case INT:
                return Integer.parseInt(value);
            case DOUBLE:
                return Double.parseDouble(value);
            case TIME:
                int idx = value.indexOf(":");
                if (idx == -1) {
                    return null;
                }
                int minutes = Integer.parseInt(value.substring(0, idx));
                int seconds = Integer.parseInt(value.substring(idx + 1));
                double partialMinutes = seconds / 60.0;
                return minutes + partialMinutes;
            default:
                return value;
        }
    }
}
