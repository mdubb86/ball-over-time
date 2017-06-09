package com.meridian.ball.repository;

import org.springframework.data.repository.CrudRepository;

import com.meridian.ball.model.Game;
import com.meridian.ball.model.SavedChart;

public interface SavedChartRepository extends CrudRepository<SavedChart, String> {

}
