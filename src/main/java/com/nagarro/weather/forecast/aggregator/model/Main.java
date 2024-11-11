package com.nagarro.weather.forecast.aggregator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Main {
    private String feels_like;
    private int pressure;
    private int humidity;

}
