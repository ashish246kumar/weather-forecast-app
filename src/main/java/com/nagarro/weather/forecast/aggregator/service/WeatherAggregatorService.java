package com.nagarro.weather.forecast.aggregator.service;

import com.nagarro.weather.forecast.aggregator.exception.WeatherServiceException;
import com.nagarro.weather.forecast.aggregator.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Service
@Slf4j
public class WeatherAggregatorService {


    @Autowired
    private  AccuWeatherService accuWeatherService;
    @Autowired
    private OpenWeatherService openWeatherService;


    public CompletableFuture<WeatherResponse> getAggregatedWeather(String city, String zip, String countryCode) {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        CompletableFuture<String> accuLocationKey = accuWeatherService.getLocationKey(city).exceptionally(ex -> {
            throw new WeatherServiceException("AccuWeather service is currently unavailable.");
        });

        CompletableFuture<WeatherInfo> accuWeather=accuLocationKey.thenCompose(key ->
                accuWeatherService.getWeatherByLocationKey(key)
        ).exceptionally(ex -> {
            throw new WeatherServiceException("Error retrieving data from AccuWeather.");
        });
        CompletableFuture<Coord> openWeatherCoordinates = openWeatherService.getCoordinates(zip, countryCode)
                .exceptionally(ex -> {
                    throw new WeatherServiceException("OpenWeather service is currently unavailable.");
                });
        CompletableFuture<OpenWeatherData> openWeather = openWeatherCoordinates.thenCompose(coords ->
                openWeatherService.getWeatherByCoordinates(coords.getLat(), coords.getLon())
        ).exceptionally(ex -> {
            throw new WeatherServiceException("Error retrieving data from OpenWeather.");
        });

        return CompletableFuture.allOf(accuWeather,openWeather).thenApplyAsync(v ->  {
           try {
               WeatherInfo weatherInfo = accuWeather.join();
               OpenWeatherData openWeatherData = openWeather.join();
               return transformResponse.apply(combineWeatherData(weatherInfo, openWeatherData));
           }
           catch (Exception ex){
               log.error(ex.getMessage());
               throw new WeatherServiceException(ex.getMessage());

           }
        },executor);

    }

    private final Function<WeatherResponse, WeatherResponse> transformResponse = response -> {
        if (!"C".equals(response.getTemperature().getUnit())) {
            double celsiusValue = convertToCelsius(response.getTemperature().getValue());
            response.setTemperature(new Temperature(celsiusValue,"C"));
        }
        return response;
    };
    private double convertToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }
    private WeatherResponse combineWeatherData(WeatherInfo weatherInfo,OpenWeatherData openWeatherData){
        Temperature temperature=Temperature.builder().unit(weatherInfo.getTemperature().getMetric().getUnit()).Value(weatherInfo.getTemperature().getMetric().getValue()).build();
            return WeatherResponse.builder()
                    .weatherText(weatherInfo.getWeatherText())
                    .hasPrecipitation(weatherInfo.isHasPrecipitation())
                    .precipitationType(weatherInfo.getPrecipitationType())
                    .isDayTime(weatherInfo.isDayTime())
                    .temperature(temperature)
                    .feelsLike(openWeatherData.getMain().getFeels_like())
                    .pressure(openWeatherData.getMain().getPressure())
                    .humidity(openWeatherData.getMain().getHumidity())
                    .visibility(openWeatherData.getVisibility())
                    .wind(openWeatherData.getWind())
                    .sunrise(openWeatherData.getSys().getSunrise())
                    .sunset(openWeatherData.getSys().getSunset())
                    .build();
    }

    }
