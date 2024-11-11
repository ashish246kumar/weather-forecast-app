package com.nagarro.weather.forecast.aggregator.controller;

import com.nagarro.weather.forecast.aggregator.model.WeatherResponse;
import com.nagarro.weather.forecast.aggregator.service.WeatherAggregatorService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
public class WeatherController {


    private WeatherAggregatorService weatherAggregatorService;


    @GetMapping("/weather")
    public CompletableFuture<ResponseEntity<?>> getWeatherDetail(@RequestParam String city, @RequestParam String zip) {
        String[] zipParts = zip.split(",");
        String zipCode = zipParts[0];
        String countryCode = zipParts[1];
        return weatherAggregatorService.getAggregatedWeather(city, zipCode, countryCode)
                .thenApply(ResponseEntity::ok);


    }




}
