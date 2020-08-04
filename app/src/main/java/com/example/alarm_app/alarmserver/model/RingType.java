package com.example.alarm_app.alarmserver.model;

public enum RingType {
    BIRDS(0),
    COSTUME(1),
    OCEAN(2),
    ROOSTER(3);

    private final long id;

    RingType(long id) { this.id = id; };

    public long getId() { return id;}
}
