package com.example.alarm_app.alarmreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class ActivationAlarmServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Im receiver", Toast.LENGTH_SHORT).show();

        Intent intentService = new Intent(context, ActivationAlarmActivityService.class);

        ContextCompat.startForegroundService(context, intentService);
    }
}
