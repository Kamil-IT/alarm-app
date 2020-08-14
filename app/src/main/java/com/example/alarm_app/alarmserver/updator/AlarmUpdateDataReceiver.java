package com.example.alarm_app.alarmserver.updator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.auth.AuthTokenHolder;
import com.example.alarm_app.alarmserver.auth.Credentials;

public class AlarmUpdateDataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//        TODO: if credentials changed, generate new token
        //        Auth
        AuthTokenHolder.getINSTANCE()
                //                TODO: Make it static
                .setCredentials(new Credentials("admin", "admin"));

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
