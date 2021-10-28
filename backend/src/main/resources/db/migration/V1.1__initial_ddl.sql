CREATE TABLE processed (
    ymd PRIMARY KEY,
    processed INTEGER
);

create TABLE cached_page (
    url TEXT PRIMARY KEY,
    content TEXT NOT NULL
)

CREATE TABLE team (
    team_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    bbref_team_id TEXT,
    nba_dot_com_team_id TEXT
);

CREATE TABLE game (
    game_id TEXT PRIMARY KEY,
    ymd TEXT NOT NULL,
    home_win INTEGER NOT NULL,
    home_team_id TEXT NOT NULL,
    away_team_id TEXT NOT NULL,
    bbref_game_id TEXT,
    nba_dot_com_game_id TEXT,
    FOREIGN KEY(home_team_id) REFERENCES team(team_id),
    FOREIGN KEY(away_team_id) REFERENCES team(team_id)
);

CREATE TABLE player (
    player_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    image_id TEXT,
    bbref_player_id TEXT,
    nba_dot_com_player_id TEXT
);

CREATE TABLE line (
    line_id TEXT PRIMARY KEY,
    team_id TEXT REFERENCES team(team_id),
    player_id TEXT REFERENCES player(player_id),
    game_id TEXT REFERENCES game(game_id),
    ast INTEGER,
    ast_pct TEXT,
    blk INTEGER,
    blk_pct TEXT,
    def_rating INTEGER,
    dreb INTEGER,
    dreb_pct TEXT,
    oreb INTEGER,
    oreb_pct TEXT,
    fg3a INTEGER,
    fg3m INTEGER,
    fga INTEGER,
    fgm INTEGER,
    fta INTEGER,
    ftm INTEGER,
    min TEXT,
    off_rating INTEGER,
    reb INTEGER,
    pf INTEGER,
    plus_minus INTEGER,
    pts INTEGER,
    stl INTEGER,
    tov INTEGER,
    tov_pct TEXT,
    ts_pct TEXT,
    usg_pct TEXT,
    fg_pct TEXT,
    fg3_pct TEXT,
    ft_pct TEXT,
    efg_pct TEXT,
    FOREIGN KEY(team_id) REFERENCES team(team_id),
    FOREIGN KEY(player_id) REFERENCES player(player_id),
    FOREIGN KEY(game_id) REFERENCES game(game_id)
);

-- CREATE VIEW player_career AS
--   select p.name, p.player_id, p.nba_dot_com_player_id, min(extract(year from g.date))::SMALLINT as rookie_year, max(extract(year from g.date))::SMALLINT as final_year
--   from player p
--     join stat_line sl on p.player_id = sl.player_id
--     join game g on sl.game_id = g.game_id
--   group by p.player_id;



