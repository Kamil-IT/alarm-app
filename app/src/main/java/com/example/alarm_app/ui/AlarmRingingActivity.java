package com.example.alarm_app.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.alarm_app.alarmreciver.ActivationAlarmActivityReceiver;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.model.Snooze;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class AlarmRingingActivity extends AppCompatActivity {

    public static final String SNOOZE_ACTIVE_CODE = "snooze_counter";
    public static final String ALARM_NAME_CODE = "alarm_name";
    public static final String ALARM_SNOOZE_NAME_CODE = "alarm_snooze_name";
    private MediaPlayer player;
    private Boolean snoozeActive = false;
    private boolean isSnoozeClicked = false;

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
//        Init variables
        btnSnooze = findViewById(R.id.button_snooze);
        btnStopAlarm = findViewById(R.id.button_stop_alarm);
        textAlarmName = findViewById(R.id.text_alarm_name);
        textAlarmTime = findViewById(R.id.text_alarm_time);

//        Init button, labels text
        btnSnooze.setText(btnSnooze.getText() + ": " + getResources().getStringArray(R.array.snooze_duration)[(int) getSnoozeOfCurrentAlarmRinging().getId()]);
        if (getNameOfCurrentAlarmRinging() != null && !"".equals(getNameOfCurrentAlarmRinging())) {
            textAlarmName.setText(getNameOfCurrentAlarmRinging());
        }
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm");
        textAlarmTime.setText(formatter.format(new Date()));

//        Init Media player
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



        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlarmManager alarmMgr = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
                Intent receiverIntent = new Intent(getBaseContext(), ActivationAlarmActivityReceiver.class);
                final PendingIntent alarmIntent = PendingIntent.getBroadcast(getBaseContext(), 0, receiverIntent, 0);
                Date currentDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis() + getSnoozeOfCurrentAlarmRinging().getTimeInMillisecond());

                alarmMgr.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmIntent
                );
                if (player != null) {
                    player.release();
                }
                notifySnoozeActive(getNameOfCurrentAlarmRinging(), getSnoozeOfCurrentAlarmRinging());
                isSnoozeClicked = true;
                finish();
            }
        });

        btnStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    player.release();
                }
                notifyAlarmEnd();
                finish();
            }
        });

//        Notify to service one alarm is after time
        AlarmService.getInstance().dataChanged();
    }

    private void notifyAlarmEnd() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        editor.remove(SNOOZE_ACTIVE_CODE);
        editor.remove(ALARM_NAME_CODE);
        editor.remove(ALARM_SNOOZE_NAME_CODE);
        editor.apply();
    }

    private void notifySnoozeActive(String name, Snooze snooze) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();

        editor.putBoolean(SNOOZE_ACTIVE_CODE, true);

        editor.putString(ALARM_NAME_CODE, name);
        editor.putString(ALARM_SNOOZE_NAME_CODE, snooze.name());
        editor.apply();
    }

    private String getNameOfCurrentAlarmRinging() {
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        snoozeActive = defSharedPref.getBoolean(SNOOZE_ACTIVE_CODE, false);

        if (!snoozeActive){
            return AlarmService.getInstance().getNextStaticAlarm10sAfterActivation().getName();
        } else {
            return defSharedPref.getString(ALARM_NAME_CODE, "");
        }
    }

    private Snooze getSnoozeOfCurrentAlarmRinging() {
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        snoozeActive = defSharedPref.getBoolean(SNOOZE_ACTIVE_CODE, false);

        if (!snoozeActive){
            return AlarmService.getInstance().getNextStaticAlarm10sAfterActivation().getSnooze();
        } else {
            return Snooze.valueOf(defSharedPref.getString(ALARM_SNOOZE_NAME_CODE, "MIN_1"));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isSnoozeClicked){
            notifyAlarmEnd();
        }
    }
}