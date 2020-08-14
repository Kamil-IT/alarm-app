package com.example.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.auth.AuthTokenHolder;
import com.example.alarm_app.alarmserver.auth.Credentials;

import androidx.core.content.ContextCompat;


public class AlarmNotifyRebootActivatorReceiver extends BroadcastReceiver {

    @SuppressLint("Receiver only for start service after reboot device")
    @Override
    public void onReceive(final Context context, Intent intent) {
//        Auth
        AuthTokenHolder.getINSTANCE()
//                TODO: Make it static
                .setCredentials(new Credentials("admin", "admin"));
        AuthTokenHolder.getINSTANCE()
                .generateToken(context);

//        Alarm service
        AlarmService.getInstance()
                .setSharedPreferences(context);
        AlarmService.getInstance()
                .updateAlarmsFromServer(context);

//        Create service and notification
        Intent intentService = new Intent(context, AlarmNotifyService.class);
        ContextCompat.startForegroundService(context, intentService);
    }
}
