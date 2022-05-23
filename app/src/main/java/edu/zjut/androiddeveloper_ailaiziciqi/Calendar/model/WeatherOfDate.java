package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model;

import java.time.LocalDate;

public class WeatherOfDate {

    private LocalDate date;
    private String weather;
    private String weatherDetails;

    public WeatherOfDate(LocalDate date, String weather, String weatherDetails) {
        this.date = date;
        this.weather = weather;
        this.weatherDetails = weatherDetails;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeatherDetails() {
        return weatherDetails;
    }

    public void setWeatherDetails(String weatherDetails) {
        this.weatherDetails = weatherDetails;
    }
}
