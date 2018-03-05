-- Connect to postgres
\c postgres

-- Drop existing
drop database if exists nba;
drop role if exists nba;

create user nba with password 'nba';
create database nba owner nba;
alter database nba set search_path to 'nba';

\c nba;
drop schema public;
create schema nba authorization nba;

