package com.example.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.auth.AuthTokenHolder;
import com.example.alarm_app.alarmserver.auth.Credentials;
import com.example.alarm_app.alarmserver.model.AlarmFor14Days;
import com.google.gson.Gson;

import java.util.List;

import androidx.core.content.ContextCompat;

import static com.example.alarm_app.alarmreciver.AlarmNotifyUpdateService.ID_EXTRA_NEXT_ALARM;

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
        Intent intentService = new Intent(context, AlarmNotifyUpdateService.class);
        List<AlarmFor14Days> sortedAlarms = AlarmService.getInstance().getSortedActiveAlarmsFor14Days();
        if (sortedAlarms.size() != 0) {
            AlarmFor14Days alarm = sortedAlarms.get(0);
            Gson gson = new Gson();
            String alarm14Json = gson.toJson(alarm);
            intentService.putExtra(ID_EXTRA_NEXT_ALARM, alarm14Json);
        }
        ContextCompat.startForegroundService(context, intentService);
    }
}
