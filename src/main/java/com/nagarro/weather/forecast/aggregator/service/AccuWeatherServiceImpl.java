package com.nagarro.weather.forecast.aggregator.service;

import com.nagarro.weather.forecast.aggregator.model.Temperature;
import com.nagarro.weather.forecast.aggregator.model.TemperatureRespponse;
import com.nagarro.weather.forecast.aggregator.model.WeatherInfo;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@Slf4j
public class AccuWeatherServiceImpl implements AccuWeatherService{

    @Autowired
    private RestTemplate restTemplate;
    @Value("${accuWeatherApikey}")
    private String apiKey;
    @Value("${accuweather.api.base-url}")
    private String baseUrl;
    @Value("${accuweather.api.base-url2}")
    private String baseUrl2;


    @CircuitBreaker(name = "weatherService")
    @Override
    public CompletableFuture<String> getLocationKey(String city) {
        String url=UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("q", city)
                .queryParam("apikey", apiKey)
                .toUriString();
        return CompletableFuture.supplyAsync(() -> {
           ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            return (String) response.getBody()[0].get("Key");
    });

    }
    @CircuitBreaker(name = "weatherService")
    @Override
    public CompletableFuture<WeatherInfo> getWeatherByLocationKey(String locationKey) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl2)
                .pathSegment(locationKey)
                .queryParam("apikey", apiKey)
                .toUriString();
        return CompletableFuture.supplyAsync(() -> {
            WeatherInfo[] response = restTemplate.getForObject(url, WeatherInfo[].class);
            return response != null && response.length > 0 ? response[0] : null;
        });

    }

//    public CompletableFuture<String> fallbackLocationKey( Throwable ex) {
//        log.error(ex.getMessage());
//        return CompletableFuture.completedFuture("defaultLocationKey");
//
//    }
//    public CompletableFuture<WeatherInfo> fallbackWeatherInfo( Throwable ex) {
//        log.error(ex.getMessage());
//        TemperatureRespponse temperature=TemperatureRespponse.builder().build();
//        return CompletableFuture.completedFuture(new WeatherInfo("No Data", false, "N/A", false, temperature));
//    }
}
