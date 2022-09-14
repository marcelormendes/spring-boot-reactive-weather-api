package com.weather.v1.test.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.text.DateFormatSymbols;
import java.util.Calendar;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@AutoConfigureWebTestClient
public class WeatherControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void getTodayForecast() {

        String dayNames[] = new DateFormatSymbols().getWeekdays();
        Calendar calendarDate = Calendar.getInstance();
        String todayName = dayNames[calendarDate.get(Calendar.DAY_OF_WEEK)];

        webTestClient.get()
                .uri("/weather/today")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.day_name", todayName);
    }

}
