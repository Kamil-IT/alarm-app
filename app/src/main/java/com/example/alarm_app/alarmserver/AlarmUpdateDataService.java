package com.example.alarm_app.alarmserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AlarmUpdateDataService extends Service {

//    TODO: This service have to actualize token, and services
//    Time to actualize token must by static and have to depends on service time actualize
//    Time to actualize alarms give user

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
