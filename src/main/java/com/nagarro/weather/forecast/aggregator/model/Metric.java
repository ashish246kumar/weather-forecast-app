package com.nagarro.weather.forecast.aggregator.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Metric {
    @JsonAlias("Value")
    private double Value;
    @JsonAlias("Unit")
    private String unit;
}
