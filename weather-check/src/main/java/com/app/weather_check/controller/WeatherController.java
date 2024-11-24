package com.app.weather_check.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class WeatherController {

    @Value("${apiKey}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/index")
    public String home() {
        return "index"; // Refers to 'index.html' in the 'templates' directory
    }

    // http://localhost:8080/index

    @PostMapping("/getweatherwithlocation")
    public String getWeatherWithLocation(@RequestParam String latitude, String longitude, Model model) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey;

        try {
            JsonNode apiResponse = restTemplate.getForObject(url, JsonNode.class);
            if (apiResponse != null) {
                String cityName = apiResponse.path("name").asText();
                double tempKelvin = apiResponse.path("main").path("temp").asDouble();
                double tempCelcius = tempKelvin - 273.15;

                int humidity = apiResponse.path("main").path("humidity").asInt();
                double windSpeed = apiResponse.path("wind").path("speed").asDouble();
                String cloudCondition = apiResponse.path("weather").get(0).path("main").asText();

                String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

                model.addAttribute("cityName", cityName);
                model.addAttribute("humidity", humidity);
                model.addAttribute("cloudCondition", cloudCondition);
                model.addAttribute("windSpeed", windSpeed);
                model.addAttribute("date", currentDate);
                model.addAttribute("temp", String.format("%.2f °C", tempCelcius));
            } else {
                model.addAttribute("error", "No response from the weather API.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Unable to fetch weather details. Please try again later.");
        }
        return "dashboard";
    }

    @PostMapping("/getweather")
    public String getCurrentTempString(@RequestParam String cityName, Model model) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey;

        try {
            JsonNode apiResponse = restTemplate.getForObject(url, JsonNode.class);
            if (apiResponse != null) {
                double tempKelvin = apiResponse.path("main").path("temp").asDouble();
                double tempCelcius = tempKelvin - 273.15;

                int humidity = apiResponse.path("main").path("humidity").asInt();
                double windSpeed = apiResponse.path("wind").path("speed").asDouble();
                String cloudCondition = apiResponse.path("weather").get(0).path("main").asText();

                String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

                model.addAttribute("cityName", cityName);
                model.addAttribute("humidity", humidity);
                model.addAttribute("cloudCondition", cloudCondition);
                model.addAttribute("windSpeed", windSpeed);
                model.addAttribute("date", currentDate);
                model.addAttribute("temp", String.format("%.2f °C", tempCelcius));
            } else {
                model.addAttribute("error", "No response from the weather API.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Unable to fetch weather details. Please try again later.");
        }
        return "dashboard";
    }
}
