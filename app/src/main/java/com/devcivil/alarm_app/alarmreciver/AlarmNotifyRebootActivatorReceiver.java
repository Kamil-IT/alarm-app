package com.devcivil.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devcivil.alarm_app.alarmserver.updator.AlarmUpdateDataReceiver;

import androidx.core.content.ContextCompat;


public class AlarmNotifyRebootActivatorReceiver extends BroadcastReceiver {

    @SuppressLint("Receiver only for start service after reboot device")
    @Override
    public void onReceive(final Context context, Intent intent) {

        AlarmUpdateDataReceiver receiver = new AlarmUpdateDataReceiver();
        receiver.onReceive(context, new Intent(context, AlarmUpdateDataReceiver.class));

//        Create service and notification
        Intent intentService = new Intent(context, AlarmNotifyService.class);
        ContextCompat.startForegroundService(context, intentService);
    }
}
