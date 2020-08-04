package com.example.alarm_app.alarmserver.model;

public enum TurnOffType {
    NORMAL(0),
    MATH_TASK(1),
    VIBES(2);

    private final long id;

    TurnOffType(long id) { this.id = id; };

    public long getId() { return id;}
}
