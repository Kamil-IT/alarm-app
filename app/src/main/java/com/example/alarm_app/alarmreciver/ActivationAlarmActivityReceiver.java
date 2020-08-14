package com.example.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.alarm_app.ui.alarmringing.AlarmRingingActivity;

public class ActivationAlarmActivityReceiver extends BroadcastReceiver {

    public static final String EXTRA_CURRENT_ALARM_RINGING = "alarm_current_ringing";

    @SuppressLint("Receiver for start alarm on time set")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent dialogIntent = new Intent(context, AlarmRingingActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(EXTRA_CURRENT_ALARM_RINGING, intent.getStringExtra(EXTRA_CURRENT_ALARM_RINGING));

        context.startActivity(dialogIntent);
    }
}
