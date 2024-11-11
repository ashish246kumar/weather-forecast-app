package com.nagarro.weather.forecast.aggregator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenWeatherData {
    private Main main;
    private Sys sys;
    private Wind wind;
    private int visibility;
}
