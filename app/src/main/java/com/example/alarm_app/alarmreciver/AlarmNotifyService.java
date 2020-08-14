package com.example.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.alarm_app.MainActivity;
import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.AlarmStaticService;
import com.example.alarm_app.alarmserver.model.AlarmFor14Days;
import com.example.alarm_app.alarmserver.updator.AlarmUpdateDataReceiver;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

public class AlarmNotifyService extends Service {

    /**
     * Notification chanel for Android Version 26
     */
    public static final String CHANEL_ID = "alarm_main_chanel";
    /**
     * Foreground service ID
     */
    public static final int ID_FOREGROUND = 1;
    /**
     * ID for info about next alarm, which will be send to receiver intent
     */
    public static final String EXTRA_CURRENT_ALARM_RINGING = "alarm_current_ringing";

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
        final AlarmFor14Days alarm = getNextUpcomingAlarm();

        startNotification(alarm);

//        Create and set alarms ringing
        final AlarmManager alarmMgr = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        if (alarm != null) {
            setAlarmMgrToNextAlarm(alarm, alarmMgr);
        }
        AlarmService.getInstance().addListener(new AlarmStaticService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                AlarmFor14Days nextUpcomingAlarm = getNextUpcomingAlarm();
                if (nextUpcomingAlarm != null) {
                    setAlarmMgrToNextAlarm(nextUpcomingAlarm, alarmMgr);
                }
            }
        });

//        Create updater for data from server
        alarmMgr.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                System.currentTimeMillis() +
//                            TODO: this have to be set by user after add setting
                        AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HALF_HOUR,
                PendingIntent.getBroadcast(this, 0, new Intent(getBaseContext(), AlarmUpdateDataReceiver.class), 0)
        );

        return START_STICKY;
    }

    private void setAlarmMgrToNextAlarm(AlarmFor14Days alarm, AlarmManager alarmMgr) {
        Intent receiverIntent = new Intent(this, ActivationAlarmActivityReceiver.class);
        Gson gson = new Gson();
        receiverIntent.putExtra(EXTRA_CURRENT_ALARM_RINGING, gson.toJson(alarm));
        final PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + (alarm.getAlarmBe().getTime() - currentDate.getTime()));

        alarmMgr.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmIntent
        );
    }

    private void startNotification(AlarmFor14Days alarm) {
        String contentTitle = getString(R.string.notify_next_alarm_will_be);
        String contentText = getString(R.string.notify_no_upcoming_alarms);

        if (alarm != null) {
            contentText = getTextWhenNextAlarmWillBe(alarm);
        }

        final NotificationCompat.Builder notification = createNotification(contentTitle, contentText);
        startForeground(ID_FOREGROUND, notification.build());

//        TODO: check working it when app is closed and after change date in server
        final AlarmFor14Days alarmOld = alarm;
        AlarmService.getInstance().addListener(new AlarmStaticService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                AlarmFor14Days alarmNew = getNextUpcomingAlarm();
                if (alarmNew != alarmOld) {
                    notification.setContentText(getTextWhenNextAlarmWillBe(alarmNew));
                    startForeground(ID_FOREGROUND, notification.build());
                }
            }
        });
    }

    private NotificationCompat.Builder createNotification(String contentTitle, String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                0
        );
        return new NotificationCompat.Builder(this, CHANEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setNotificationSilent()
                .setShowWhen(false)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);
    }

    private String getTextWhenNextAlarmWillBe(AlarmFor14Days alarm) {
        if (alarm == null){
            return getString(R.string.notify_no_upcoming_alarms);
        }

        StringBuilder strTimeNextAlarm = new StringBuilder();

        Calendar endOfCurrentDay = Calendar.getInstance();
        endOfCurrentDay.setTime(new Date());
        endOfCurrentDay.set(HOUR, 23);
        endOfCurrentDay.set(MINUTE, 59);
        endOfCurrentDay.set(SECOND, 59);
        if (alarm.getAlarmBe().before(endOfCurrentDay.getTime())) {
            strTimeNextAlarm.append(getString(R.string.today));
        }

        Calendar endOfSecondDay = endOfCurrentDay;
        long dayInMillis = 86400000L;
        endOfSecondDay.setTimeInMillis(endOfCurrentDay.getTimeInMillis() + dayInMillis);
        if (alarm.getAlarmBe().before(endOfCurrentDay.getTime())) {
            strTimeNextAlarm.append(getString(R.string.tomorrow));
        }

        if (alarm.getAlarmBe().after(endOfCurrentDay.getTime())) {
            Calendar endOfTheWeek = endOfSecondDay;
            endOfTheWeek.setTimeInMillis(endOfCurrentDay.getTimeInMillis() + dayInMillis * 5);
            if (alarm.getAlarmBe().before(endOfTheWeek.getTime())) {
                endOfCurrentDay.setTime(alarm.getAlarmBe());
                strTimeNextAlarm.append(
                        getResources()
                                .getStringArray(
                                        R.array.week_days)[endOfCurrentDay.get(DAY_OF_WEEK)]
                );
            }
        }

        Calendar endOfTheWeek = endOfSecondDay;
        endOfTheWeek.setTimeInMillis(endOfCurrentDay.getTimeInMillis() + dayInMillis * 5);
        if (alarm.getAlarmBe().after(endOfTheWeek.getTime())) {
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            strTimeNextAlarm.append(df.format(alarm.getAlarmBe()));

        }
        strTimeNextAlarm.append(", ");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm:ss");
        strTimeNextAlarm.append(df.format(alarm.getAlarmBe()));

        return strTimeNextAlarm.toString();
    }

    @Nullable
    private AlarmFor14Days getNextUpcomingAlarm() {
        List<AlarmFor14Days> sortedActiveAlarmsFor14Days = AlarmService.getInstance().getSortedActiveAlarmsFor14Days();
        if (sortedActiveAlarmsFor14Days.size() != 0) {
            return sortedActiveAlarmsFor14Days.get(0);
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
