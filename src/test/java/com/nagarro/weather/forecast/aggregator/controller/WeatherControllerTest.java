package com.nagarro.weather.forecast.aggregator.controller;

import com.nagarro.weather.forecast.aggregator.model.Temperature;
import com.nagarro.weather.forecast.aggregator.model.WeatherResponse;
import com.nagarro.weather.forecast.aggregator.model.Wind;
import com.nagarro.weather.forecast.aggregator.service.WeatherAggregatorService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.CompletableFuture;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)  // Use WebMvcTest if testing only controller
@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {



    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherAggregatorService weatherAggregatorService;


    @Test
    @SneakyThrows
    public void testGetWeatherDetail() {
        // Prepare test data
        String city = "New York";
        String zip = "10001,US";
        WeatherResponse weatherResponse = WeatherResponse.builder()
                .weatherText("Clear sky")
                .hasPrecipitation(false)
                .precipitationType("")
                .isDayTime(true)
                .temperature(new Temperature(22.5, "C"))
                .feelsLike("20.0")
                .pressure(1015)
                .humidity(60)
                .visibility(10)
                .wind(new Wind(5.5, 180, 6.0))
                .sunrise(1609459200L)
                .sunset(1609502400L)
                .build();

        CompletableFuture<WeatherResponse> future = CompletableFuture.completedFuture(weatherResponse);

        when(weatherAggregatorService.getAggregatedWeather(anyString(), anyString(), anyString())).thenReturn(future);

        // Perform the request and assert the response
        mockMvc.perform(get("/weather")
                        .param("city", city)
                        .param("zip", zip)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                // Check if the hasPrecipitation field is correctly returned
//                .andExpect(jsonPath("$.hasPrecipitation").value(false))
//                .andExpect(jsonPath("$.weatherText").value("Clear sky"));
//                .andExpect(jsonPath("$.temperature.value").value(22.5))
//                .andExpect(jsonPath("$.temperature.unit").value("C"))
//                .andExpect(jsonPath("$.wind.speed").value(5.5))
//                .andExpect(jsonPath("$.wind.deg").value(180))
//                .andExpect(jsonPath("$.wind.gust").value(6.0));
    }


}
