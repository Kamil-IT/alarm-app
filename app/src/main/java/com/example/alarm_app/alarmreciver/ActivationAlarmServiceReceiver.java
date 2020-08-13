package com.example.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class ActivationAlarmServiceReceiver extends BroadcastReceiver {

    @SuppressLint("Receiver for start alarm on time set")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, ActivationAlarmActivityService.class);
        ContextCompat.startForegroundService(context, intentService);
    }
}
