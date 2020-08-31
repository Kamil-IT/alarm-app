package com.example.alarm_app.alarmserver.updator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.auth.AuthTokenHolder;
import com.example.alarm_app.alarmserver.auth.CredentialsHolder;

public class AlarmUpdateDataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//        Auth
        CredentialsHolder.getInstance().setShearPreferences(context);
        if (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
            AuthTokenHolder.getINSTANCE()
                    .generateToken(context);
        }

//        Alarm service
        AlarmService.getInstance()
                .setSharedPreferences(context);
        AlarmService.getInstance()
                .updateAlarmsFromServer(context);
    }

}
