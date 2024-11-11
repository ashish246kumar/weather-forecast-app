package com.nagarro.weather.forecast.aggregator.service;

import com.nagarro.weather.forecast.aggregator.exception.WeatherServiceException;
import com.nagarro.weather.forecast.aggregator.model.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherAggregatorServiceTest {

    @InjectMocks
    private WeatherAggregatorService weatherAggregatorService;

    @Mock
    private AccuWeatherService accuWeatherService;

    @Mock
    private OpenWeatherService openWeatherService;

    private ExecutorService executor;

    @BeforeEach
    public void setUp() {
        executor = Executors.newFixedThreadPool(5);
    }

@Test
public void testGetAggregatedWeather_SuccessfulResponse() throws Exception {
    String city = "Deoghar";
    String zip = "814112";
    String countryCode = "IN";
    String mockLocationKey = "12345";
    WeatherResponse expectedResponse = WeatherResponse.builder()
            .weatherText("Sunny")
            .hasPrecipitation(false)
            .precipitationType(null)
            .isDayTime(true)
            .build();
    WeatherInfo mockWeatherInfo = new WeatherInfo();
    mockWeatherInfo.setWeatherText("Sunny");
    mockWeatherInfo.setHasPrecipitation(false);
    mockWeatherInfo.setPrecipitationType(null);
    mockWeatherInfo.setDayTime(true);
    Metric tempMetric = new Metric( 70.0,"F");
    TemperatureRespponse temperatureInfo = new TemperatureRespponse(tempMetric);
    mockWeatherInfo.setTemperature(temperatureInfo);
    Coord mockCoordinates = new Coord(51.5074, -0.1278);
    OpenWeatherData mockOpenWeatherData=OpenWeatherData.builder().main(new Main()).sys(new Sys()).wind(new Wind()).build();
    when(accuWeatherService.getLocationKey(anyString())).thenReturn(CompletableFuture.completedFuture(mockLocationKey));
    when(accuWeatherService.getWeatherByLocationKey(anyString())).thenReturn(CompletableFuture.completedFuture(mockWeatherInfo));
    when(openWeatherService.getCoordinates(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(mockCoordinates));
    when(openWeatherService.getWeatherByCoordinates(anyDouble(), anyDouble()))
            .thenReturn(CompletableFuture.completedFuture(mockOpenWeatherData));
    CompletableFuture<WeatherResponse> resultFuture = weatherAggregatorService.getAggregatedWeather(city, zip, countryCode);
    WeatherResponse actualResponse = resultFuture.join();

    assertEquals(expectedResponse.getWeatherText(), actualResponse.getWeatherText());
    assertEquals(expectedResponse.isHasPrecipitation(), actualResponse.isHasPrecipitation());
    assertEquals(expectedResponse.getPrecipitationType(), actualResponse.getPrecipitationType());
    assertEquals(expectedResponse.isDayTime(), actualResponse.isDayTime());
    assertEquals(expectedResponse.getPressure(), actualResponse.getPressure());
    assertEquals(expectedResponse.getHumidity(), actualResponse.getHumidity());
    assertEquals(expectedResponse.getVisibility(), actualResponse.getVisibility());
}

    @Test
    void testGetAggregatedWeather_WhenAccuWeatherServiceFails() {
        String city = "Deoghar";
        String zip = "814112";
        String countryCode = "IN";

        // Mock AccuWeather service to throw an exception
        when(accuWeatherService.getLocationKey(anyString()))
                .thenThrow(new WeatherServiceException("AccuWeather service is currently unavailable."));

        // Assert that the exception is thrown when AccuWeather service fails
        assertThrows(
                WeatherServiceException.class,
                () -> weatherAggregatorService.getAggregatedWeather(city, zip, countryCode).join()
        );
    }
    @Test
    void testGetAggregatedWeather_WhenOpenWeatherServiceFails() {
        String city = "Deoghar";
        String zip = "814112";
        String countryCode = "IN";
        String mockLocationKey = "12345";
        WeatherInfo mockWeatherInfo = new WeatherInfo();
        mockWeatherInfo.setWeatherText("Sunny");
        mockWeatherInfo.setHasPrecipitation(false);
        mockWeatherInfo.setPrecipitationType(null);
        mockWeatherInfo.setDayTime(true);
        Metric tempMetric = new Metric(70.0, "F");
        TemperatureRespponse temperatureInfo = new TemperatureRespponse(tempMetric);
        mockWeatherInfo.setTemperature(temperatureInfo);

        // Mock AccuWeather service to return valid data
        when(accuWeatherService.getLocationKey(anyString()))
                .thenReturn(CompletableFuture.completedFuture(mockLocationKey));
        when(accuWeatherService.getWeatherByLocationKey(anyString()))
                .thenReturn(CompletableFuture.completedFuture(mockWeatherInfo));

        // Mock OpenWeather service to throw an exception
        when(openWeatherService.getCoordinates(anyString(), anyString()))
                .thenThrow(new WeatherServiceException("OpenWeather service is currently unavailable."));

        // Assert that the exception is thrown when OpenWeather service fails
        assertThrows(
                WeatherServiceException.class,
                () -> weatherAggregatorService.getAggregatedWeather(city, zip, countryCode).join()
        );
    }



}
