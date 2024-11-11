package com.nagarro.weather.forecast.aggregator.exception;

public class WeatherServiceException extends RuntimeException{
    public WeatherServiceException(String message){
        super(message);
    }

}
