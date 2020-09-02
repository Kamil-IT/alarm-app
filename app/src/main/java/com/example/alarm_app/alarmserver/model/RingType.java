package com.example.alarm_app.alarmserver.model;

import com.example.alarm_app.R;

import androidx.annotation.RawRes;

public enum RingType {
    BIRDS(0, R.raw.birds),
    COSTUME(1, R.raw.alarm_clock),
    OCEAN(2, R.raw.ocean),
    ROOSTER(3, R.raw.roster),
    ALARM_CLASSIC(4, R.raw.alarm_clock),
    SIREN(5, R.raw.alarm_clock);

    private final long id;
    private final int musicRes;

    RingType(long id, @RawRes int musicRes) {
        this.id = id;
        this.musicRes = musicRes;
    };

    public long getId() { return id;}

    public int getMusicRes() { return musicRes; }
}
