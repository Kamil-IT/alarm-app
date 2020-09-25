package com.devcivil.alarm_app.ui.home;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.AlarmService;
import com.devcivil.alarm_app.alarmserver.AlarmStaticService;
import com.devcivil.alarm_app.alarmserver.model.AlarmDto;
import com.devcivil.alarm_app.alarmserver.model.AlarmFor14Days;
import com.devcivil.alarm_app.alarmserver.model.Date;
import com.devcivil.alarm_app.alarmserver.model.RingType;
import com.devcivil.alarm_app.alarmserver.model.Snooze;
import com.devcivil.alarm_app.alarmserver.model.Time;
import com.devcivil.alarm_app.alarmserver.model.TurnOffType;
import com.devcivil.alarm_app.ui.account.LoginActivity;
import com.devcivil.alarm_app.ui.account.SingUpActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import static com.devcivil.alarm_app.alarmserver.ConnectionToAlarmServer.isNetworkConnected;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.FRIDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.MONDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.SATURDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.SUNDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.THURSDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.TUESDAY;
import static com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType.WEDNESDAY;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<String> timeOfNextAlarmLiveData;
    private MutableLiveData<Boolean> isDataRefreshLiveData;
    private MutableLiveData<Boolean> dataChanged;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        final Context context = application.getApplicationContext();
        timeOfNextAlarmLiveData = new MutableLiveData<>();
        timeOfNextAlarmLiveData.setValue(nextAlarmTime(context));
        isDataRefreshLiveData = new MutableLiveData<>();
        dataChanged = new MutableLiveData<>();
        dataChanged.setValue(true);
        AlarmService.getInstance().addListener(new AlarmStaticService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                timeOfNextAlarmLiveData.setValue(nextAlarmTime(context));
            }
        });
        AlarmService.getInstance().addListener(new AlarmStaticService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                dataChanged.setValue(!dataChanged.getValue());
            }
        });
    }

    public MutableLiveData<Boolean> getIsDataRefreshLiveData() {
        return isDataRefreshLiveData;
    }

    public MutableLiveData<Boolean> getDataChanged() {
        return dataChanged;
    }

    public void OnSwipeRefresh(@NotNull Context context){
        AlarmService.getInstance().addListener(new AlarmStaticService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                isDataRefreshLiveData.setValue(true);
            }
        });

        if (isNetworkConnected(context)) {
            AlarmService.getInstance().updateAlarmsFromServer(context);
        } else {
            isDataRefreshLiveData.setValue(false);
        }
    }

    public MutableLiveData<String> getTimeOfNextAlarmLiveData() {
        return timeOfNextAlarmLiveData;
    }

    private String nextAlarmTime(@NotNull Context context) {
        String strNextAlar;
        List<AlarmFor14Days> sortedActiveAlarms = AlarmService.getInstance().getSortedActiveAlarmsFor14Days();

        if (sortedActiveAlarms.size() == 0) {
            strNextAlar = context.getString(R.string.text_view_next_alarm_be);
        } else {
            strNextAlar = getTextWhenNextAlarmWillBe(context, sortedActiveAlarms.get(0));
        }
        return strNextAlar;
    }

    private String getTextWhenNextAlarmWillBe(@NotNull Context context, AlarmFor14Days alarm) {
        if (alarm == null){
            return context.getString(R.string.notify_no_upcoming_alarms);
        }
        else if (alarm.getAlarmBe().before(new java.util.Date())){
            return context.getString(R.string.notify_no_upcoming_alarms);
        }

        StringBuilder strTimeNextAlarm = new StringBuilder();
        long dayInMillis = 86400000L;

//        End today
        Calendar endOfCurrentDay = Calendar.getInstance();
        endOfCurrentDay.setTime(new java.util.Date());
        endOfCurrentDay.set(HOUR_OF_DAY, 23);
        endOfCurrentDay.set(MINUTE, 59);
        endOfCurrentDay.set(SECOND, 59);
//        End tomorrow
        Calendar endOfTomorrowDay = Calendar.getInstance();
        endOfTomorrowDay.setTime(endOfCurrentDay.getTime());
        endOfTomorrowDay.setTimeInMillis(endOfCurrentDay.getTimeInMillis() + dayInMillis);
//        End Week
        Calendar endOfWeek = Calendar.getInstance();
        endOfWeek.setTime(endOfTomorrowDay.getTime());
        endOfWeek.setTimeInMillis(endOfTomorrowDay.getTimeInMillis() + dayInMillis * 5);


        if (alarm.getAlarmBe().before(endOfCurrentDay.getTime())) {
            strTimeNextAlarm.append(context.getString(R.string.today));
        }
        else if (alarm.getAlarmBe().before(endOfTomorrowDay.getTime())) {
            strTimeNextAlarm.append(context.getString(R.string.tomorrow));
        }
        else if (alarm.getAlarmBe().before(endOfWeek.getTime())) {
            endOfCurrentDay.setTime(alarm.getAlarmBe());
            strTimeNextAlarm.append(
                    context.getResources().getStringArray(
                            R.array.week_days)[endOfCurrentDay.get(DAY_OF_WEEK)]
            );
        }
        else {
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            strTimeNextAlarm.append(df.format(alarm.getAlarmBe()));
        }

        strTimeNextAlarm.append(", ");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm:ss");
        strTimeNextAlarm.append(df.format(alarm.getAlarmBe()));

        return strTimeNextAlarm.toString();
    }

//    First run
    public boolean isPreviousStart(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_previously_started), false);
    }

    public void previousStart(Context context){
        AlarmService.getInstance().creteAlarm(context,
                new AlarmDto(
                        null,
                        "Time to school",
                        "Wake up to school",
                        new Time(7,0,0),
                        System.currentTimeMillis(),
                        RingType.ALARM_CLASSIC,
                        Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY),
                        false,
                        new ArrayList<Date>(),
                        TurnOffType.NORMAL,
                        Snooze.MIN_5
                ));

        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean(context.getString(R.string.pref_previously_started), Boolean.TRUE);
        edit.apply();
    }

    public AlertDialog dialogForFirstUsage(@NotNull final Context context) {
        return new MaterialAlertDialogBuilder(context)
                .setTitle("Sync all alarms")
                .setMessage("Create or log to your account will keep all of yours alarms in cloud. It's highly recommended to create or log in now.")
                .setPositiveButton("Create account now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        context.startActivity(new Intent(context, SingUpActivity.class));
                    }
                })
                .setNeutralButton("Log to your account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        context.startActivity(new Intent(context, LoginActivity.class));

                    }
                })
                .setNegativeButton("Do it later via settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
    }


}