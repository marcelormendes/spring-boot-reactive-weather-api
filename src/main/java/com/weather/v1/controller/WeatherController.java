package com.weather.v1.controller;

import com.weather.v1.model.Daily;
import com.weather.v1.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping(path = "/today", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Mono<Daily> getToday() throws Exception {

        return weatherService.getTodayForecast();
    }

}
