package com.devcivil.alarm_app.alarmserver.model;

import com.devcivil.alarm_app.R;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

public enum RingType {
    BIRDS(0, R.raw.birds),
    COSTUME(1, R.raw.alarm_clock),
    OCEAN(2, R.raw.ocean),
    ROOSTER(3, R.raw.roster),
    ALARM_CLASSIC(4, R.raw.alarm_clock),
    SIREN(5, R.raw.siren);

    private final long id;
    private final int musicRes;

    RingType(long id, @RawRes int musicRes) {
        this.id = id;
        this.musicRes = musicRes;
    }

    public long getId() {
        return id;
    }

    public int getMusicRes() {
        return musicRes;
    }

    @Nullable
    public static RingType getById(long id) {
        for (RingType ringType :
                RingType.values()) {
            if (ringType.id == id) {
                return ringType;
            }
        }
        return null;
    }

    @Nullable
    public static RingType getById(int id){
        return getById(Long.valueOf(id));
    }
}
