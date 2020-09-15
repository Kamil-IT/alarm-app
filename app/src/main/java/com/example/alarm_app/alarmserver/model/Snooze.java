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

    Snooze(long id) { this.id = id; }

    public long getId() { return id;}

    public long getTimeInMillisecond() {
        if (id == 0) return 60000;
        else if (id == 1) return 60000 * 2;
        else if (id == 2) return 60000 * 3;
        else if (id == 3) return 60000 * 4;
        else if (id == 4) return 60000 * 5;
        else if (id == 5) return 60000 * 10;
        else if (id == 6) return 60000 * 15;
        else if (id == 7) return 60000 * 30;
        else return 60000 * 60;
    }
}
