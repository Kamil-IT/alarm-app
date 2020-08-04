package com.example.alarm_app.alarmserver.model;

public enum RingType {
    COSTUME(0),
    BIRDS(1);

    private final long id;

    RingType(long id) { this.id = id; };

    public long getId() { return id;}
}
