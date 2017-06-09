package com.meridian.ball.model;

public enum Stat {
    MINUTES("MIN", "Minutes Played", "min", true),
    FIELD_GOALS_MADE("FGM", "Field Goals Made", "fgm", true),
    FIELD_GOALS_ATTEMPTED("FGA", "Field Goals Attempted", "fga", true),
    FIELD_GOAL_PERCENTAGE("FG%", "Field Goal Percentage", "fga / nullif(fgm, 0)", true),
    THREE_POINTERS_MADE("3PM", "Three Pointers Made", "fg3m", true),
    THREE_POINTERS_ATTEMPTED("3PA", "Three Pointers Attempted", "fg3a", true),
    THREE_POINT_PERCENTAGE("3P%", "Three Point Percentage", "fg3m / nullif(fg3a, 0)", true),
    FREE_THROWS_MADE("FTM", "Free Throws Made", "ftm", true),
    FREE_THROWS_ATTEMPTED("FTA", "Free Throws Attempted", "fta", true),
    FREE_THROW_PERCENTAGE("FT%", "Free Throw Percentage", "ftm / nullif(fta, 0)", true),
    OFFENSIVE_REBOUNDS("OREB", "Offensive Rebounds", "oreb", true),
    DEFENSIVE_REBOUNDS("DREB", "Defensive Rebounds", "dreb", true),
    REBOUNDS("REB", "Rebounds", "oreb + dreb", true),
    ASSISTS("AST", "Assists", "ast", true),
    TURNOVERS("TOV", "Turnovers", "turn_obers", true),
    STEALS("STL", "Steals", "stl", true),
    BLOCKED_SHOTS("BLK", "Blocked Shots", "blk", true),
    PERSONAL_FOULS("PF", "Personal Foul", "pf", true),
    POINTS("PTS", "Points", "pts", true),
    PLUS_MINUS("+/-", "Plus-Minus", "plus_minus", true),
    OFFESNSIVE_RATING("OFFRTG", "Offensive Rating", "off_rating", false),
    DEFENSIVE_RATING("DEFRTG", "Defensive Rating", "def_rating", false),
    NET_RATING("NETRTG", "Net Rating", "net_rating", false),
    ASSIST_PERCENTAGE("AST%", "Assist Percentage", "ast_pct", false),
    ASSIST_TO_TURNOVER_RATIO("AST/TO", "Assist to Turnover Ratio", "ast_tov", false),
    ASSIST_RATIO("AST RATIO", "Assist Ratio", "ast_ratio", false),
    OFFENSIVE_REBOUND_PERCENTAGE("OREB%", "Offensive Rebound Percentage", "oreb_pct",    false),
    DEFENSIVE_REBOUND_PERCENTAGE("DREB%", "Defensive Rebound Percentage", "dreb_pct", false),
    REBOUND_PERCENTAGE("REB%", "Rebound Percentage", "oreb_pct / nullif(dreb_pct, 0)", false),
    TURNOVER_PERCENTAGE("TO RATIO", "Turnover Percentage", "tm_tov_pct", false),
    EFFECTIVE_FIELD_GOAL_PERCENTAGE("EFG%", "Effective Field Goal Percentage", "efg_pct", false),
    TRUE_SHOOTING_PERCENTAGE("TS%", "True Shooting Percentage", "ts_pct", false),
    USAGE_PERCENTAGE("USG%", "Usage Percentage", "usg_pct", false),
    PACE("PACE", "Pace", "pace", false),
    PLAYER_IMPACT_ESTIMATE("PIE", "Player Impact Estimate", "pie", false);
    
    private final String abbr;
    private final String displayName;
    private final String dbField;
    private final boolean traditional;

    private Stat(String abbr, String displayName, String dbField, boolean traditional) {
        this.abbr = abbr;
        this.displayName = displayName;
        this.dbField = dbField;
        this.traditional = traditional;
    }

    public String getAbbr() {
        return abbr;
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
    
    public String getName() {
        return name().toLowerCase().replace("_", "-");
    }
}
