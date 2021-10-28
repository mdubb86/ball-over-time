create TABLE cached_page (
     url VARCHAR PRIMARY KEY,
     code SMALLINT NOT NULL,
     content BLOB NOT NULL,
     ts TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE season (
    season_id VARCHAR PRIMARY KEY,
    league VARCHAR NOT NULL,
    start_year TINYINT NOT NULL,
    end_year TINYINT NOT NULL,
    bbref_season_id VARCHAR NOT NULL
);

-- CREATE TABLE season (
--     season_id TEXT PRIMARY KEY,
--     league TEXT NOT NULL,
--     start_year INTEGER NOT NULL,
--     end_year INTEGER NOT NULL,
--     bbref_season_id TEXT NOT NULL
-- );
--
-- CREATE TABLE season_month (
--     season_month_id TEXT PRIMARY KEY,
--     season_id TEXT NOT NULL,
--     month INTEGER NOT NULL,
--     year INTEGER NOT NULL,
--     bbref_season_month_id TEXT NOT NULL,
--     FOREIGN KEY(season_id) REFERENCES season(season_id)
-- );
--
-- CREATE TABLE bbref_box_score (
--     bbref_box_score_id TEXT PRIMARY KEY,
--     season_month_id TEXT NOT NULL,
--     date TEXT NOT NULL,
--     home_bbref_team_id TEXT NOT NULL,
--     away_bbref_team_id TEXT NOT NULL,
--     home_team_points INTEGER NOT NULL,
--     away_team_points INTEGER NOT NULL,
--     FOREIGN KEY(season_month_id) REFERENCES season_month(season_month_id)
-- );
--
-- CREATE TABLE bbref_box_score_stat (
--     bbref_box_score_id TEXT NOT NULL,
--     bbref_player_id TEXT NOT NULL,
--     player_name TEXT NOT NULL,
--     stat TEXT NOT NULL,
--     stat_value TEXT NOT NULL,
--     PRIMARY KEY(bbref_box_score_id, bbref_player_id, stat)
--     FOREIGN KEY(bbref_box_score_id) REFERENCES bbref_box_score(bbref_box_score_id)
-- );
--
-- CREATE TABLE team (
--     team_key TEXT PRIMARY KEY,
--     name TEXT NOT NULL,
--     year INTEGER NOT NULL,
--     season_key TEXT NOT NULL,
--     UNIQUE(name, year),
--     FOREIGN KEY(season_key) REFERENCES season(season_key)
-- );
--
-- CREATE TABLE player (
--     player_id TEXT PRIMARY KEY,
--     name TEXT NOT NULL,
--     image_id TEXT,
--     bbref_player_id TEXT,
--     nba_dot_com_player_id TEXT
-- );
--
-- CREATE TABLE game (
--     game_id TEXT PRIMARY KEY,
--     season_month_key TEXT NOT NULL,
--     ymd TEXT NOT NULL,
--     home_team_id TEXT NOT NULL,
--     away_team_id TEXT NOT NULL,
--     bbref_game_id TEXT,
--     nba_dot_com_game_id TEXT,
--     FOREIGN KEY(season_month_key) REFERENCES season_month(season_month_key),
--     FOREIGN KEY(home_team_id) REFERENCES team(team_id),
--     FOREIGN KEY(away_team_id) REFERENCES team(team_id)
-- );
--
-- CREATE TABLE line (
--     line_id TEXT PRIMARY KEY,
--     season_id TEXT REFERENCES season(season_id),
--     team_id TEXT REFERENCES team(team_id),
--     player_id TEXT REFERENCES player(player_id),
--     game_id TEXT REFERENCES game(game_id),
--     ast INTEGER,
--     ast_pct TEXT,
--     blk INTEGER,
--     blk_pct TEXT,
--     def_rating INTEGER,
--     dreb INTEGER,
--     dreb_pct TEXT,
--     oreb INTEGER,
--     oreb_pct TEXT,
--     fg3a INTEGER,
--     fg3m INTEGER,
--     fga INTEGER,
--     fgm INTEGER,
--     fta INTEGER,
--     ftm INTEGER,
--     min TEXT,
--     off_rating INTEGER,
--     reb INTEGER,
--     pf INTEGER,
--     plus_minus INTEGER,
--     pts INTEGER,
--     stl INTEGER,
--     tov INTEGER,
--     tov_pct TEXT,
--     ts_pct TEXT,
--     usg_pct TEXT,
--     fg_pct TEXT,
--     fg3_pct TEXT,
--     ft_pct TEXT,
--     efg_pct TEXT,
--     FOREIGN KEY(team_id) REFERENCES team(team_id),
--     FOREIGN KEY(player_id) REFERENCES player(player_id),
--     FOREIGN KEY(game_id) REFERENCES game(game_id)
-- );