package com.nagarro.weather.forecast.aggregator.service;

import com.nagarro.weather.forecast.aggregator.model.Metric;

import com.nagarro.weather.forecast.aggregator.model.TemperatureRespponse;
import com.nagarro.weather.forecast.aggregator.model.WeatherInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AccuWeatherServiceImplTest {

    @InjectMocks
    private AccuWeatherServiceImpl accuWeatherServiceImpl;

    @Mock
    private RestTemplate restTemplate;

@BeforeEach
public void setUp() {
    // Inject values into the service's private fields
    ReflectionTestUtils.setField(accuWeatherServiceImpl, "baseUrl", "https://dataservice.accuweather.com/locations/v1/search");
    ReflectionTestUtils.setField(accuWeatherServiceImpl, "baseUrl2", "https://dataservice.accuweather.com/currentconditions/v1");
    ReflectionTestUtils.setField(accuWeatherServiceImpl, "apiKey", "O3pWXByyepG5BeukHHQAZ4wlZVtV32TC");
}
    @Test
    public void testGetLocationKey_VerifyRestTemplateCall() {
    String expectedKey = "12345";
        String city = "London";
        String apiKey = "O3pWXByyepG5BeukHHQAZ4wlZVtV32TC";
        String url = UriComponentsBuilder.fromHttpUrl("https://dataservice.accuweather.com/locations/v1/search")
                .queryParam("q", city)
                .queryParam("apikey", apiKey)
                .toUriString();
        Map<String, String> locationResponse = new HashMap<>();
        locationResponse.put("Key", expectedKey);
        ResponseEntity<Map[]> responseEntity = new ResponseEntity<>(new Map[]{locationResponse}, HttpStatus.OK);
        when(restTemplate.getForEntity(eq(url), eq(Map[].class))).thenReturn(responseEntity);
        CompletableFuture<String> resultFuture = accuWeatherServiceImpl.getLocationKey(city);
        String actualKey = resultFuture.join();
        verify(restTemplate).getForEntity(eq(url), eq(Map[].class));
        assertEquals(expectedKey, actualKey);

    }
    @Test
    public void testGetWeatherByLocationKey_VerifyRestTemplateCall() {
        String locationKey = "12345";
        String apiKey = "O3pWXByyepG5BeukHHQAZ4wlZVtV32TC";
        String url = UriComponentsBuilder.fromHttpUrl("https://dataservice.accuweather.com/currentconditions/v1")
                .pathSegment(locationKey)
                .queryParam("apikey", apiKey)
                .toUriString();

        WeatherInfo expectedWeatherInfo = new WeatherInfo();
        Metric metric=Metric.builder().build();
        TemperatureRespponse temperature=TemperatureRespponse.builder().metric(metric).build();
        expectedWeatherInfo.setTemperature(temperature);  // example field in WeatherInfo
        WeatherInfo[] weatherInfoArray = { expectedWeatherInfo };

        when(restTemplate.getForObject(eq(url), eq(WeatherInfo[].class))).thenReturn(weatherInfoArray);

        // Call the method
        CompletableFuture<WeatherInfo> resultFuture = accuWeatherServiceImpl.getWeatherByLocationKey(locationKey);
        WeatherInfo actualWeatherInfo = resultFuture.join();

        // Verify and assert results
        assertEquals(expectedWeatherInfo, actualWeatherInfo);
    }

    }
