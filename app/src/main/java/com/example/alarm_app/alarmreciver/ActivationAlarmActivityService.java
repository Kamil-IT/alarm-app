package com.example.alarm_app.alarmreciver;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.alarm_app.R;
import com.example.alarm_app.ui.alarmringing.AlarmRingingActivity;

import java.io.IOException;

import androidx.annotation.Nullable;

public class ActivationAlarmActivityService extends Service {

    private MediaPlayer player;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent dialogIntent = new Intent(this, AlarmRingingActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_ALARM);

        AssetFileDescriptor afd = this.getResources().openRawResourceFd(R.raw.paluch_sund);
        try {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
//            TODO: set default music after add it
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        player.prepareAsync();

//        To stop
//        player.release();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
