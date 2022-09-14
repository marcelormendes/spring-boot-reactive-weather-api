package com.weather.v1.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.weather.v1.model.Daily;
import com.weather.v1.model.Forecast;

import reactor.core.publisher.Mono;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class WeatherService {

    private final WebClient client = WebClient.builder()
            .baseUrl("https://api.weather.gov")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public Mono<Daily> getTodayForecast() {
        return client.get()
                .uri("/gridpoints/MLB/33,70/forecast")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::buildDailyObject);

    }

    private Daily buildDailyObject(String forecastJson) {
        Daily daily = new Daily();

        try {
            ObjectNode node = new ObjectMapper().readValue(forecastJson, ObjectNode.class);

            if (node.has("properties")) {

                Integer highTemp = 0;
                String forecastBlurp = null;

                JsonNode periodsArray = node.get("properties").get("periods");

                for (JsonNode objNode : periodsArray) {

                    // Check if Date coming from Api.Weather it matches with today
                    String startDate = objNode.get("startTime").toString().substring(1, 11);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateFromApi = sdf.parse(startDate);

                    if (!this.isCurrentDay(dateFromApi)) {
                        continue;
                    }

                    // Get highest temperature of current day + short forecast
                    Integer currentTemp = Integer.valueOf(objNode.get("temperature").toString());
                    String currentForecastBlurp = objNode.get("shortForecast").toString();

                    if (currentTemp > highTemp) {
                        highTemp = currentTemp;
                        forecastBlurp = currentForecastBlurp;
                    }
                }

                // Get the name of Current Day
                String dayNames[] = new DateFormatSymbols().getWeekdays();
                Calendar calendarDate = Calendar.getInstance();
                String todayName = dayNames[calendarDate.get(Calendar.DAY_OF_WEEK)];

                // Convert temperature to Celsius
                int highTempInCelsius = ((highTemp - 32) * 5) / 9;

                // Remove double quote from forecast blurp
                String forecastBlurpNoDq = forecastBlurp.replaceAll("^\"|\"$", "");

                Forecast fc = new Forecast(todayName, highTempInCelsius, forecastBlurpNoDq);

                List<Forecast> fcList = new ArrayList<>();
                fcList.add(fc);

                daily.setForecastList(fcList);

            }
        } catch (Exception e) {
            throw new RuntimeException("error during parsing of api.weather.gov");
        }

        if (daily.getForecastList() == null) {
            throw new RuntimeException("not found forecast");
        }

        return daily;

    }

    private Boolean isCurrentDay(Date dateToVerify) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(new Date());

        Calendar apiCalendar = Calendar.getInstance();
        apiCalendar.setTime(dateToVerify);

        int todayCalendarYear = todayCalendar.get(Calendar.YEAR);
        int todayCalendarMonth = todayCalendar.get(Calendar.MONTH);
        int todayCalendarDay = todayCalendar.get(Calendar.DAY_OF_MONTH);

        int apiCalendarYear = apiCalendar.get(Calendar.YEAR);
        int apiCalendarMonth = apiCalendar.get(Calendar.MONTH);
        int apiCalendarDay = apiCalendar.get(Calendar.DAY_OF_MONTH);

        if (todayCalendarDay == apiCalendarDay
                && todayCalendarMonth == apiCalendarMonth
                && todayCalendarYear == apiCalendarYear) {
            return true;
        } else {
            return false;
        }
    }

}
