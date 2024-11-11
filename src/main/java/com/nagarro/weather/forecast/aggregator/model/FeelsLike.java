package com.nagarro.weather.forecast.aggregator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeelsLike {
    private double value;
    private String unit;
}
