package com.example.alarm_app.alarmserver.model;

import java.util.Objects;

public class Time {

    private Integer hours;
    private Integer minutes;
    private Integer seconds;

    public Time() {
    }

    public Time(Integer hours, Integer minutes, Integer seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return Objects.equals(hours, time.hours) &&
                Objects.equals(minutes, time.minutes) &&
                Objects.equals(seconds, time.seconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hours, minutes, seconds);
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();

        if (hours < 10) response.append("0").append(hours);
        else response.append(hours);
        response.append(":");

        if (minutes < 10) response.append("0").append(minutes);
        else response.append(minutes);
        response.append(":");

        if (seconds < 10) response.append("0").append(seconds);
        else response.append(seconds);

        return response.toString();
    }
}
