package com.nagarro.weather.forecast.aggregator.service;

import com.nagarro.weather.forecast.aggregator.model.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OpenWeatherServiceImpl implements OpenWeatherService{
    @Autowired
    private RestTemplate restTemplate;
    @Value("${openweather.api.base-url}")
    private String baseUrl;
    @Value("${openweather.api.base-url2}")
    private String baseUrl2;

    @Value("${openWeatherApiKey}")
    private String apiKey;
    @CircuitBreaker(name = "weatherService")
    @Override
    public CompletableFuture<Coord> getCoordinates(String zip, String countryCode) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("zip", zip + "," + countryCode)
                .queryParam("appid", apiKey)
                .toUriString();
        return CompletableFuture.supplyAsync(() -> {
            return restTemplate.getForObject(url, Coord.class);

        });
    }

    @CircuitBreaker(name = "weatherService")
    @Override
    public CompletableFuture<OpenWeatherData> getWeatherByCoordinates(double lat, double lon) {
        String url=UriComponentsBuilder.fromHttpUrl(baseUrl2)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .toUriString();
        return CompletableFuture.supplyAsync(() -> {
            return restTemplate.getForObject(url, OpenWeatherData.class);

        });
    }

}
