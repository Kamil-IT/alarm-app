package com.example.alarm_app.alarmserver.model;

public enum AlarmFrequencyType {

    //    Custom
    CUSTOM(10),

    //    Week days
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),

    //    Weekend
    SATURDAY(7),
    SUNDAY(1);

    private final long id;

    AlarmFrequencyType(long id) { this.id = id; };

    public long getId() { return id;}
}
