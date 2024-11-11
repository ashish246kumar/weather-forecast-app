package com.nagarro.weather.forecast.aggregator.service;

import com.nagarro.weather.forecast.aggregator.model.Coord;
import com.nagarro.weather.forecast.aggregator.model.OpenWeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import static org.mockito.ArgumentMatchers.eq;

import java.util.concurrent.CompletableFuture;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenWeatherServiceImplTest {

    @InjectMocks
    private OpenWeatherServiceImpl openWeatherServiceImpl;

    @Mock
    private RestTemplate restTemplate;
    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(openWeatherServiceImpl, "baseUrl", "http://api.openweathermap.org/geo/1.0/zip");
        ReflectionTestUtils.setField(openWeatherServiceImpl, "baseUrl2", "https://api.openweathermap.org/data/2.5/weather");
        ReflectionTestUtils.setField(openWeatherServiceImpl, "apiKey", "69017e0b57a66b777aec1aec85cb05a0");
    }
    @Test
    public void testGetCoordinates() {
        String zip = "12345";
        String countryCode = "US";
        String url = UriComponentsBuilder.fromHttpUrl("http://api.openweathermap.org/geo/1.0/zip")
                .queryParam("zip", zip + "," + countryCode)
                .queryParam("appid","69017e0b57a66b777aec1aec85cb05a0")
                .toUriString();

        // Mock the response from the API
        Coord expectedCoord = new Coord(40.7128, -74.0060);  // Example coordinates for New York City
        when(restTemplate.getForObject(eq(url), eq(Coord.class))).thenReturn(expectedCoord);

        // Call the method
        CompletableFuture<Coord> resultFuture = openWeatherServiceImpl.getCoordinates(zip, countryCode);
        Coord actualCoord = resultFuture.join();

        // Verify and assert results
        assertEquals(expectedCoord, actualCoord);
    }
    @Test
    public void testGetWeatherByCoordinates() throws Exception {
        // Given
        double lat = 51.5074;
        double lon = -0.1278;
        String url = UriComponentsBuilder.fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", "69017e0b57a66b777aec1aec85cb05a0")
                .toUriString();

        OpenWeatherData expectedData = new OpenWeatherData();

        when(restTemplate.getForObject(eq(url), eq(OpenWeatherData.class))).thenReturn(expectedData);

        // When
        CompletableFuture<OpenWeatherData> resultFuture = openWeatherServiceImpl.getWeatherByCoordinates(lat, lon);
        OpenWeatherData actualData = resultFuture.join();

        // Then
        verify(restTemplate).getForObject(eq(url), eq(OpenWeatherData.class));
    }
}
