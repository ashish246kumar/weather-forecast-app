package com.nagarro.weather.forecast.aggregator.service;

import com.nagarro.weather.forecast.aggregator.model.Coord;
import com.nagarro.weather.forecast.aggregator.model.OpenWeatherData;

import java.util.concurrent.CompletableFuture;

public interface OpenWeatherService {
    CompletableFuture<Coord> getCoordinates(String zip, String countryCode);
    CompletableFuture<OpenWeatherData> getWeatherByCoordinates(double lat, double lon);

}
