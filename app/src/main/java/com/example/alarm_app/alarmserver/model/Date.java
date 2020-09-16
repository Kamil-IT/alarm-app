package com.example.alarm_app.alarmserver.model;

import java.util.Objects;

import androidx.annotation.NonNull;

public class Date {

    private Integer day;
    private Integer month;
    private Integer year;

    public Date() {
    }

    public Date(Integer day, Integer month, Integer year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return Objects.equals(day, date.day) &&
                Objects.equals(month, date.month) &&
                Objects.equals(year, date.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();

        if (day < 10) response.append("0").append(day);
        else response.append(day);
        response.append("-");

        if (month < 10) response.append("0").append(month);
        else response.append(month);
        response.append("-");

        if (year < 10) response.append("0").append(year);
        else response.append(year);

        return response.toString();
    }
}
