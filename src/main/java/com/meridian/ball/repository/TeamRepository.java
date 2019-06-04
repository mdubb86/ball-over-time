package com.meridian.ball.repository;

import org.springframework.data.repository.CrudRepository;

import com.meridian.ball.model.Team;

public interface TeamRepository extends CrudRepository<Team, String> {

}
