CREATE TABLE key_date (
    key varchar(255) PRIMARY KEY,
    date date
);

CREATE TABLE team (
    team_id varchar(255) PRIMARY KEY,
    name varchar(255)
);

CREATE TABLE game (
    game_id varchar(255) PRIMARY KEY,
    date date,
    home_win boolean NOT NULL,
    home_team_id varchar(255) REFERENCES team(team_id),
    vistor_team_id varchar(255) REFERENCES team(team_id)
);

CREATE TABLE player (
    player_id varchar(255) PRIMARY KEY,
    name varchar(255),
    picture_url varchar(255),
    has_nba_dot_com_image boolean,
    nba_dot_com_image_checksum varchar(255),
    nba_dot_com_player_id bigint
);

CREATE TABLE stat_line (
    stat_line_id varchar(255) PRIMARY KEY,
    team_id varchar(255) REFERENCES team(team_id),
    player_id varchar(255) REFERENCES player(player_id),
    game_id varchar(255) REFERENCES game(game_id),
    ast integer,
    ast_pct numeric(4, 2),
    blk integer,
    blk_pct numeric(4, 2),
    def_rating integer,
    dreb integer,
    dreb_pct numeric(4, 2),
    fg3a integer,
    fg3m integer,
    fga integer,
    fgm integer,
    fta integer,
    ftm integer,
    min numeric(4, 2),
    off_rating integer,
    oreb integer,
    oreb_pct numeric(4, 2),
    pf integer,
    plus_minus integer,
    pts integer,
    stl integer,
    tov integer,
    tov_pct numeric(4, 2),
    ts_pct numeric(4, 2),
    usg_pct numeric(4, 2),
    fg_pct numeric(4, 2),
    fg3_pct numeric(4, 2),
    ft_pct numeric(4, 2),
    reb integer,
    efg_pct numeric(4, 2)
);

CREATE VIEW player_career AS
  select p.name, p.player_id, p.nba_dot_com_player_id, min(extract(year from g.date))::SMALLINT as rookie_year, max(extract(year from g.date))::SMALLINT as final_year
  from player p
    join stat_line sl on p.player_id = sl.player_id
    join game g on sl.game_id = g.game_id
  group by p.player_id;


