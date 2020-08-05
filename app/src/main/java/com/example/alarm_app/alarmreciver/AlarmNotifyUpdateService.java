package com.example.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.alarm_app.MainActivity;
import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.AlarmStaticService;
import com.example.alarm_app.alarmserver.model.AlarmFor14Days;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

/**
 * !!!
 * Before create service you must create channel
 * for notification because if you don't do this
 * Android closed you notification and service
 * 10sec after turn off app.
 * !!!
 */
public class AlarmNotifyUpdateService extends Service {

    public static final String CHANEL_ID = "alarm_main_chanel";

    public static final String ID_EXTRA_NEXT_ALARM = "next_alarm";
    public static final int ID_FOREGROUND = 1;

//    TODO: Add receiver to open after reboot
//    TODO: Add listener to data changed and change text on notification is correct


    @Override
    public void onCreate() {
        //        Create chanel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANEL_ID,
                    String.valueOf(R.string.notify_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(String.valueOf(R.string.notify_desc_alarm_starter));

            // Register the channel with the system
            NotificationManager notificationManager = getBaseContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String contentTitle = getString(R.string.notify_next_alarm_will_be);
        String contentText = getString(R.string.notify_no_upcoming_alarms);

        String alarm14Json = null;
        try {
            alarm14Json = intent.getStringExtra(ID_EXTRA_NEXT_ALARM);
        } catch (Exception ignored) {
        }

        AlarmFor14Days alarm = null;
        if (alarm14Json != null) {
            Gson gson = new Gson();
            alarm = gson.fromJson(alarm14Json, AlarmFor14Days.class);
            contentText = getTextWhenNextAlarmWillBe(alarm);
        }

//        Create intent to notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                0
        );
//        Create notification
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setNotificationSilent()
                .setShowWhen(false)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);
//        Start notification
        startForeground(ID_FOREGROUND, notification.build());


//        TODO: check working it when app is closed and after change date in server
        final AlarmFor14Days alarmOld = alarm;
        AlarmService.getInstance().addListener(new AlarmStaticService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                if (AlarmService.getInstance().getSortedActiveAlarmsFor14Days().size() != 0) {
                    AlarmFor14Days alarmNew = AlarmService.getInstance().getSortedActiveAlarmsFor14Days().get(0);
                    if (alarmNew != alarmOld) {
                        notification.setContentText(getTextWhenNextAlarmWillBe(alarmNew));
                        startForeground(ID_FOREGROUND, notification.build());
                    }
                }
            }
        });

        return START_STICKY;
    }

//    TODO: add listener when day is and and change text to bee correct to next day
    private String getTextWhenNextAlarmWillBe(AlarmFor14Days alarm){
        StringBuilder strTimeNextAlarm = new StringBuilder();

//        TODO: change it as every if have own calendar
        Calendar endOfTime = Calendar.getInstance();
        endOfTime.setTime(new Date());
        endOfTime.set(HOUR, 23);
        endOfTime.set(MINUTE, 59);
        endOfTime.set(SECOND, 59);

        if (alarm.getAlarmBe().before(endOfTime.getTime())){
                strTimeNextAlarm.append(getString(R.string.today));
        }
        endOfTime.setTimeInMillis(endOfTime.getTimeInMillis() + 86400000L);
        if (alarm.getAlarmBe().before(endOfTime.getTime())){
            strTimeNextAlarm.append(getString(R.string.tomorrow));
        }
        if (alarm.getAlarmBe().after(endOfTime.getTime())){
            endOfTime.setTime(alarm.getAlarmBe());
            strTimeNextAlarm.append(
                    getResources()
                            .getStringArray(
                                    R.array.week_days)[endOfTime.get(DAY_OF_WEEK)]
            );
        }
//        TODO: If time is longer then week add costume data
        strTimeNextAlarm.append(", ");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm:ss");
        strTimeNextAlarm.append(df.format(alarm.getAlarmBe()));

        return strTimeNextAlarm.toString();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
