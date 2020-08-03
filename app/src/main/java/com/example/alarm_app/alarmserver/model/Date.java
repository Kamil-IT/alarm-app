package com.example.alarm_app.alarmserver.model;

import java.util.Objects;

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
}
