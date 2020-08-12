package com.example.alarm_app.alarmreciver;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.alarm_app.R;
import com.example.alarm_app.ui.alarmringing.AlarmRingingActivity;

import androidx.annotation.Nullable;

public class ActivationAlarmActivityService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent dialogIntent = new Intent(this, AlarmRingingActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);

//        Play music
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.paluch_sund);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
