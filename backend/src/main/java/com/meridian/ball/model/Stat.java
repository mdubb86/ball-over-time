package com.meridian.ball.model;

import java.util.Optional;

/*
 * fg_pct NUMERIC(2, 2)
 * fg3_pct NUMERIC(2, 2)
 * ft_pct NUMERIC(2, 2)
 * reb
 * efg_pct
 */

public enum Stat {
    MINUTES("min", "mp", "Minutes Played", "Minutes Played", StatDataType.TIME, true),
    FIELD_GOALS_MADE("fgm", "fg", "Field Goals", "Field Goals Made", StatDataType.INT, true),
    FIELD_GOALS_ATTEMPTED("fga", "fga", "Field Goals Attempted", "fga", StatDataType.INT, true),
    FIELD_GOAL_PERCENTAGE("fg_pct", null, "FG%", "Field Goal Percentage", StatDataType.DOUBLE, true),
    THREE_POINTERS_MADE("fg3m", "fg3", "3 Pointers", "3 Pointers Made", StatDataType.INT, true),
    THREE_POINTERS_ATTEMPTED("fg3a", "fg3a", "3 Pointers Attempted", "3 Pointers Attempted", StatDataType.INT, true),
    THREE_POINT_PERCENTAGE("fg3_pct", null, "3P%", "Three Point Percentage", StatDataType.DOUBLE, true),
    FREE_THROWS_MADE("ftm", "ft", "Free Throws", "Free Throws Made", StatDataType.INT, true),
    FREE_THROWS_ATTEMPTED("fta", "fta", "Free Throws Attempted", "Free Throws Attempted", StatDataType.INT, true),
    FREE_THROW_PERCENTAGE("ft_pct", null, "FT%", "Free Throw Percentage", StatDataType.DOUBLE, true),
    OFFENSIVE_REBOUNDS("oreb", "orb", "Offensive Rebounds", "Offensive Rebounds", StatDataType.INT, true),
    DEFENSIVE_REBOUNDS("dreb", "drb", "Defensive Rebounds", "Defensive Rebounds", StatDataType.INT, true),
    REBOUNDS("reb", null, "Rebounds", "Total Rebounds", StatDataType.INT, true),
    ASSISTS("ast", "ast", "Assists", "Assists", StatDataType.INT, true),
    BLOCKED_SHOTS("blk", "blk", "Blocks", "Blocked Shots", StatDataType.INT, true),
    TURNOVERS("tov", "tov", "Tunnovers", "Turnovers", StatDataType.INT, true),
    STEALS("stl", "stl", "Steals", "Steals", StatDataType.INT, true),
    PERSONAL_FOULS("pf", "pf", "Personal Fouls", "Personal Fouls", StatDataType.INT, true),
    POINTS("pts", "pts", "Points", "Points", StatDataType.INT, true),
    PLUS_MINUS("plus_minus", "plus_minus", "+/-", "Plus-Minus", StatDataType.INT, true),
    TRUE_SHOOTING_PERCENTAGE("ts_pct", "ts_pct", "True Shooting Percentage", "A measure of shooting efficiency that takes into account 2-point field goals, 3-point field goals, and free throws.", StatDataType.DOUBLE, true),
    EFFECTIVE_FIELD_GOAL_PERCENTAGE("efg_pct", null, "Effective Field Goal Percentage", "This statistic adjusts for the fact that a 3-point field goal is worth one more point than a 2-point field goal.", StatDataType.DOUBLE, true),
    OFFENSIVE_REBOUND_PERCENTAGE("oreb_pct", "orb_pct", "Offensive Rebound Percentage", "An estimate of the percentage of available offensive rebounds a player grabbed while he was on the floor.", StatDataType.DOUBLE, true),
    DEFENSIVE_REBOUND_PERCENTAGE("dreb_pct", "drb_pct", "Defensive Rebound Percentage", "An estimate of the percentage of available defensive rebounds a player grabbed while he was on the floor.", StatDataType.DOUBLE, true),
//  TODO: This stat was not scraped and was previously calculated nonsensically
//    REBOUND_PERCENTAGE("oreb_pct / nullif(dreb_pct, 0)", null, "Rebound Percentage", "An estimate of the percentage of available rebounds a player grabbed while he was on the floor.", StatDataType.DOUBLE, true, true),
    ASSIST_PERCENTAGE("ast_pct", "ast_pct", "Assist Percentage", "An estimate of the percentage of teammate field goals a player assisted while he was on the floor.", StatDataType.DOUBLE, true),
    BLOCK_PERCENTAGE("blk_pct", "blk_pct", "Block Percentage", "An estimate of the percentage of opponent two-point field goal attempts blocked by the player while he was on the floor.", StatDataType.DOUBLE, true),
    TURNOVER_PERCENTAGE("tov_pct", "tov_pct", "Turnover Percentage", "An estimate of turnovers committed per 100 plays.", StatDataType.DOUBLE, true),
    USAGE_PERCENTAGE("usg_pct", "usg_pct", "Usage Percentage", "An estimate of the percentage of team plays used by a player while he was on the floor.", StatDataType.DOUBLE, true),
    OFFESNSIVE_RATING("off_rating", "off_rtg", "Offensive Rating", "An estimate of points produced (players) or scored (teams) per 100 possessions.", StatDataType.INT, true),
    DEFENSIVE_RATING("def_rating", "def_rtg", "Defensive Rating", "An estimate of points allowed per 100 possessions", StatDataType.INT, true),
    //    NET_RATING("NETRTG", "Net Rating", "net_rating", false),
    ;

    private final String dbField;
    private final String bbRefName;
    private final String displayName;
    private final StatDataType dataType;
    private final boolean traditional;

    private Stat(String dbField, String bbRefName, String displayName,
            String description, StatDataType dataType, boolean traditional) {
        this.bbRefName = bbRefName;
        this.displayName = displayName;
        this.dbField = dbField;
        this.dataType = dataType;
        this.traditional = traditional;
    }

    public String getBbRefName() {
        return bbRefName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDbField() {
        return dbField;
    }

    public boolean isTraditional() {
        return traditional;
    }
    
    public StatDataType getDataType() {
        return dataType;
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


}
