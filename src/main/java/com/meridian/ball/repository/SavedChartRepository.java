package com.meridian.ball.repository;

import org.springframework.data.repository.CrudRepository;

import com.meridian.ball.model.SavedChart;
import com.meridian.ball.model.Stat;
import com.meridian.ball.service.StatService.Aggregation;

public interface SavedChartRepository extends CrudRepository<SavedChart, String> {
}
