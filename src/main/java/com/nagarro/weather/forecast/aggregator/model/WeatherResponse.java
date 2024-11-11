package com.nagarro.weather.forecast.aggregator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherResponse {
   private String weatherText;
   private boolean hasPrecipitation;
    private String  precipitationType;
    private boolean isDayTime;
    private Temperature temperature;
    private String feelsLike;
    private int pressure;
    private int humidity;
    private int visibility;
    private Wind wind;
    private Long sunrise;
    private Long sunset;


}
