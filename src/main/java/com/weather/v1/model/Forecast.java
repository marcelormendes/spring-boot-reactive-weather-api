package com.weather.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Forecast {
    @JsonProperty("day_name")
    private String dayName;

    @JsonProperty("temp_high_celsius")
    private int tempHighCelsius;

    @JsonProperty("forecast_blurp")
    private String forecastBlurp;
}
