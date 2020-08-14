package com.example.alarm_app.ui.alarmringing;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.model.AlarmFor14Days;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.alarm_app.alarmreciver.ActivationAlarmActivityReceiver.EXTRA_CURRENT_ALARM_RINGING;

public class AlarmRingingActivity extends AppCompatActivity {

    private MediaPlayer player;

    private Button btnSnooze, btnStopAlarm;
    private TextView textAlarmName, textAlarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alarm_ringing);

//        Set working on screen off
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (keyguardManager != null)
                keyguardManager.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        String alarm14Json = getIntent().getStringExtra(EXTRA_CURRENT_ALARM_RINGING);
        Gson gson = new Gson();
        AlarmFor14Days alarm = gson.fromJson(alarm14Json, AlarmFor14Days.class);

        btnSnooze = findViewById(R.id.button_snooze);
        btnStopAlarm = findViewById(R.id.button_stop_alarm);
        textAlarmName = findViewById(R.id.text_alarm_name);
        textAlarmTime = findViewById(R.id.text_alarm_time);

        btnSnooze.setText(btnSnooze.getText() + ": " + getResources().getStringArray(R.array.snooze_duration)[(int) alarm.getSnooze().getId()]);
        if (alarm.getName() != null && "".equals(alarm.getName())) {
            textAlarmName.setText(alarm.getName());
        }
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm");
        textAlarmTime.setText(formatter.format(alarm.getAlarmBe()));

//        TODO: add service for snooze after user click on it

        btnStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (player != null) {
                    player.release();
                }
            }
        });

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
        
        AlarmService.getInstance().dataChanged();

    }
}