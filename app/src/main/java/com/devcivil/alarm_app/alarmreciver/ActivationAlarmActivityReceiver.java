package com.devcivil.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.AlarmService;
import com.devcivil.alarm_app.alarmserver.model.RingType;
import com.devcivil.alarm_app.alarmserver.model.Snooze;
import com.devcivil.alarm_app.ui.ringing.AlarmRingingActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.preference.PreferenceManager;

import static com.devcivil.alarm_app.ui.ringing.AlarmRingingActivity.ALARM_NAME_CODE;
import static com.devcivil.alarm_app.ui.ringing.AlarmRingingActivity.ALARM_RING_TYPE_CODE;
import static com.devcivil.alarm_app.ui.ringing.AlarmRingingActivity.ALARM_SNOOZE_NAME_CODE;
import static com.devcivil.alarm_app.ui.ringing.AlarmRingingActivity.SNOOZE_ACTIVE_CODE;

public class ActivationAlarmActivityReceiver extends BroadcastReceiver {

    public static final String EXTRA_CURRENT_ALARM_RINGING = "alarm_current_ringing";

    @SuppressLint("Receiver for start alarm on time set")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent dialogIntent = new Intent(context, AlarmRingingActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(EXTRA_CURRENT_ALARM_RINGING, intent.getStringExtra(EXTRA_CURRENT_ALARM_RINGING));

//        context.startActivity(dialogIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!Settings.canDrawOverlays(context)) {
                Toast.makeText(context, "No permission to write on the screen", Toast.LENGTH_LONG).show();
                Log.e("Permit", "write on the screen: " + Settings.canDrawOverlays(context));
            }

            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    PixelFormat.TRANSLUCENT);
            mLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;

            viewRinging = getAlarmRingingView(context);

            mWindowManager.addView(viewRinging, mLayoutParams);

        } else {
            context.startActivity(dialogIntent);
        }
    }

    private View viewRinging;
    private WindowManager mWindowManager;
    private MediaPlayer player;
    private Boolean snoozeActive = false;
    private boolean isSnoozeClicked = false;

    private View getAlarmRingingView(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_alarm_ringing, null, false);


        //        Init button, labels text
        Button btnSnooze = view.findViewById(R.id.button_snooze);
        Button btnStopAlarm = view.findViewById(R.id.button_stop_alarm);
        TextView textAlarmName = view.findViewById(R.id.text_alarm_name);
        TextView textAlarmTime = view.findViewById(R.id.text_alarm_time);


        btnSnooze.setText(btnSnooze.getText() + ": " + context.getResources().getStringArray(R.array.snooze_duration)[(int) getSnoozeOfCurrentAlarmRinging(context).getId()]);
        if (getNameOfCurrentAlarmRinging(context) != null && !"".equals(getNameOfCurrentAlarmRinging(context))) {
            textAlarmName.setText(getNameOfCurrentAlarmRinging(context));
        }
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        textAlarmTime.setText(formatter.format(new Date()));

//        Init Media player
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_ALARM);
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(getRingTypeOfCurrentAlarmRinging(context).getMusicRes());
        try {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException ignored) { }

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
                final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent receiverIntent = new Intent(context, ActivationAlarmActivityReceiver.class);
                final PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, 0);
                Date currentDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis() + getSnoozeOfCurrentAlarmRinging(context).getTimeInMillisecond());

                alarmMgr.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmIntent
                );
                if (player != null) {
                    player.release();
                }
                notifySnoozeActive(context, getNameOfCurrentAlarmRinging(context), getSnoozeOfCurrentAlarmRinging(context), getRingTypeOfCurrentAlarmRinging(context));
                isSnoozeClicked = true;
                mWindowManager.removeViewImmediate(viewRinging);
            }
        });

        btnStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    player.release();
                }
                notifyAlarmEnd(context);
                mWindowManager.removeViewImmediate(viewRinging);
            }
        });

//        Notify to service one alarm is after time
        AlarmService.getInstance().dataChanged();

        return view;
    }


    private void notifyAlarmEnd(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(SNOOZE_ACTIVE_CODE);
        editor.remove(ALARM_NAME_CODE);
        editor.remove(ALARM_SNOOZE_NAME_CODE);
        editor.remove(ALARM_RING_TYPE_CODE);
        editor.apply();
    }

    private void notifySnoozeActive(Context context, String name, Snooze snooze, RingType ringType) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putBoolean(SNOOZE_ACTIVE_CODE, true);

        editor.putString(ALARM_NAME_CODE, name);
        editor.putString(ALARM_SNOOZE_NAME_CODE, snooze.name());
        editor.putString(ALARM_RING_TYPE_CODE, ringType.toString());
        editor.apply();
    }

    private String getNameOfCurrentAlarmRinging(Context context) {
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        snoozeActive = defSharedPref.getBoolean(SNOOZE_ACTIVE_CODE, false);

        if (!snoozeActive){
            return AlarmService.getInstance().getNextStaticAlarm10sAfterActivation().getName();
        } else {
            return defSharedPref.getString(ALARM_NAME_CODE, "");
        }
    }

    private Snooze getSnoozeOfCurrentAlarmRinging(Context context) {
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        snoozeActive = defSharedPref.getBoolean(SNOOZE_ACTIVE_CODE, false);

        if (!snoozeActive){
            return AlarmService.getInstance().getNextStaticAlarm10sAfterActivation().getSnooze();
        } else {
            return Snooze.valueOf(defSharedPref.getString(ALARM_SNOOZE_NAME_CODE, "MIN_1"));
        }
    }

    private RingType getRingTypeOfCurrentAlarmRinging(Context context){
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        snoozeActive = defSharedPref.getBoolean(SNOOZE_ACTIVE_CODE, false);
        if (!snoozeActive){
            return AlarmService.getInstance().getNextStaticAlarm10sAfterActivation().getRingType();
        } else {
            return RingType.valueOf(defSharedPref.getString(ALARM_RING_TYPE_CODE, RingType.ALARM_CLASSIC.toString()));
        }
    }
}
