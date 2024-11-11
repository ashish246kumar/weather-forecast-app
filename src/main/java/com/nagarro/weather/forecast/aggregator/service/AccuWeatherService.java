package com.nagarro.weather.forecast.aggregator.service;

import com.nagarro.weather.forecast.aggregator.model.WeatherInfo;

import java.util.concurrent.CompletableFuture;

public interface AccuWeatherService {
    CompletableFuture<String> getLocationKey(String city);
    CompletableFuture<WeatherInfo> getWeatherByLocationKey(String locationKey);


}
