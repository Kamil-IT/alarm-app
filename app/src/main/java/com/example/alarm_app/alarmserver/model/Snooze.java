package com.example.alarm_app.alarmserver.model;

public enum Snooze {
    MIN_1(0),
    MIN_2(1),
    MIN_3(2),
    MIN_4(3),
    MIN_5(4),
    MIN_10(5),
    MIN_15(6),
    MIN_30(7),
    MIN_60(8);

    private final long id;

    Snooze(long id) { this.id = id; };

    public long getId() { return id;}
}
