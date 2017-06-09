package com.meridian.ball.repository;

import org.springframework.data.repository.CrudRepository;

import com.meridian.ball.model.Game;

public interface GameRepository extends CrudRepository<Game, String> {

}
