create materialized view stats_by_month as
  select
    make_date(sub.year, sub.month, 1) as date,
    sub.*
  from (
    select
      player_id,
      extract(year from g.date)::integer as year,
      extract(month from g.date)::integer as month,
      avg(ast)::numeric(4, 2) as ast,
      avg(ast_pct)::numeric(3, 2) as ast_pct,
      avg(blk)::numeric(4, 2) as blk,
      avg(blk_pct)::numeric(3, 2) as blk_pct,
      avg(def_rating)::numeric(5, 2) as def_rating,
      avg(dreb)::numeric(4, 2) as dreb,
      avg(dreb_pct)::numeric(3, 2) as dreb_pct,
      avg(oreb)::numeric(4, 2) as oreb,
      avg(oreb_pct)::numeric(3, 2) as oreb_pct,
      avg(fg3a)::numeric(4, 2) as fg3a,
      avg(fg3m)::numeric(4, 2) as fg3m,
      avg(fga)::numeric(4, 2) as fga,
      avg(fgm)::numeric(4, 2) as fgm,
      avg(fta)::numeric(4, 2) as fta,
      avg(ftm)::numeric(4, 2) as ftm,
      avg(min)::numeric(4, 2) as min,
      avg(off_rating)::numeric(5, 2) as off_rating,
      avg(reb)::numeric(4, 2) as reb,
      avg(pf)::numeric(4, 2) as pf,
      avg(plus_minus)::numeric(4, 2) as plus_minus,
      avg(pts)::numeric(4, 2) as pts,
      avg(stl)::numeric(4, 2) as stl,
      avg(tov)::numeric(4, 2) as tov,
      avg(tov_pct)::numeric(3, 2) as tov_pct,
      avg(ts_pct)::numeric(3, 2) as ts_pct,
      avg(usg_pct)::numeric(3, 2) as usg_pct,
      avg(fg_pct)::numeric(3, 2) as fg_pct,
      avg(fg3_pct)::numeric(3, 2) as fg3_pct,
      avg(ft_pct)::numeric(3, 2) as ft_pct,
      avg(efg_pct)::numeric(3, 2) as efg_pct
    from stat_line s
      join game g on s.game_id = g.game_id 
    group by player_id, year, month
  ) as sub
  order by date
with data;
create unique index stats_by_month_uidx on stats_by_month (player_id, year, month);

create materialized view stats_by_season as
  select
    make_date(sub.season, 1, 1) as date,
    sub.*
  from (
    select
      player_id,
      case
        when extract(month from g.date) > 8
        then extract(year from g.date)::integer
        else extract(year from g.date)::integer - 1
      end as season,
      avg(ast)::numeric(4, 2) as ast,
      avg(ast_pct)::numeric(3, 2) as ast_pct,
      avg(blk)::numeric(4, 2) as blk,
      avg(blk_pct)::numeric(3, 2) as blk_pct,
      avg(def_rating)::numeric(5, 2) as def_rating,
      avg(dreb)::numeric(4, 2) as dreb,
      avg(dreb_pct)::numeric(3, 2) as dreb_pct,
      avg(oreb)::numeric(4, 2) as oreb,
      avg(oreb_pct)::numeric(3, 2) as oreb_pct,
      avg(fg3a)::numeric(4, 2) as fg3a,
      avg(fg3m)::numeric(4, 2) as fg3m,
      avg(fga)::numeric(4, 2) as fga,
      avg(fgm)::numeric(4, 2) as fgm,
      avg(fta)::numeric(4, 2) as fta,
      avg(ftm)::numeric(4, 2) as ftm,
      avg(min)::numeric(4, 2) as min,
      avg(off_rating)::numeric(5, 2) as off_rating,
      avg(reb)::numeric(4, 2) as reb,
      avg(pf)::numeric(4, 2) as pf,
      avg(plus_minus)::numeric(4, 2) as plus_minus,
      avg(pts)::numeric(4, 2) as pts,
      avg(stl)::numeric(4, 2) as stl,
      avg(tov)::numeric(4, 2) as tov,
      avg(tov_pct)::numeric(3, 2) as tov_pct,
      avg(ts_pct)::numeric(3, 2) as ts_pct,
      avg(usg_pct)::numeric(3, 2) as usg_pct,
      avg(fg_pct)::numeric(3, 2) as fg_pct,
      avg(fg3_pct)::numeric(3, 2) as fg3_pct,
      avg(ft_pct)::numeric(3, 2) as ft_pct,
      avg(efg_pct)::numeric(3, 2) as efg_pct
    from stat_line s
      join game g on s.game_id = g.game_id 
    group by player_id, season 
  ) as sub
  order by date
with data;
create unique index stats_by_season_uidx on stats_by_season (player_id, season);