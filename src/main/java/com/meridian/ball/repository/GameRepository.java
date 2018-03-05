package com.meridian.ball.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.meridian.ball.model.Game;

public interface GameRepository extends CrudRepository<Game, String> {
    
    List<Game> findAllByOrderByDateAsc();
    int deleteByDate(LocalDate date);

}
