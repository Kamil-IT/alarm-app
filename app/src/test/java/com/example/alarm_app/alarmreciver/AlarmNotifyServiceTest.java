package com.example.alarm_app.alarmreciver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import com.example.alarm_app.alarmserver.model.AlarmFor14Days;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.HOUR_OF_DAY;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AlarmNotifyServiceTest {

    public static final long DAY_IN_MILLIS = 86400000L;

    @Mock
    private Resources mockContextResources;
    @Mock
    private Context mockApplicationContext;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getTextWhenNextAlarmWillBeNoUpcomingAlarms() {
//        Set calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(HOUR_OF_DAY, calendar.get(HOUR_OF_DAY) - 1);

//        Set alarm
        AlarmFor14Days alarm = new AlarmFor14Days();
        alarm.setAlarmBe(calendar.getTime());

        AlarmNotifyService alarmNotifyService = new AlarmNotifyService();


//        TODO: Use Mock
//        assertEquals("No upcoming alarms", alarmNotifyService.getTextWhenNextAlarmWillBe(alarm));
    }

    @Test
    public void getTextWhenNextAlarmWillBeTomorrow() {
        AlarmFor14Days alarm = new AlarmFor14Days();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(DAY_OF_YEAR, calendar.get(DAY_OF_YEAR) + 1);
        alarm.setAlarmBe(calendar.getTime());
        AlarmNotifyService alarmNotifyService = new AlarmNotifyService();

//        TODO: Use Mock
//        assertEquals("No upcoming alarms", alarmNotifyService.getTextWhenNextAlarmWillBe(alarm));
    }

    @Test
    public void getTextWhenNextAlarmWillBeFor7Days() {
        AlarmFor14Days alarm = new AlarmFor14Days();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(DAY_OF_YEAR, calendar.get(DAY_OF_YEAR) + 2);
        alarm.setAlarmBe(calendar.getTime());
        AlarmNotifyService alarmNotifyService = new AlarmNotifyService();

//        TODO: Use Mock
//        assertEquals("No upcoming alarms", alarmNotifyService.getTextWhenNextAlarmWillBe(alarm));
    }

    @Test
    public void getTextWhenNextAlarmWillBeAfter7Days() {
        AlarmFor14Days alarm = new AlarmFor14Days();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(DAY_OF_YEAR, calendar.get(DAY_OF_YEAR) + 8);
        alarm.setAlarmBe(calendar.getTime());
        AlarmNotifyService alarmNotifyService = new AlarmNotifyService();

        StringBuilder info = new StringBuilder();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        info.append(df.format(alarm.getAlarmBe()));
        info.append(", ");
        df = new SimpleDateFormat("HH:mm:ss");
        info.append(df.format(alarm.getAlarmBe()));

        assertEquals(info.toString(), alarmNotifyService.getTextWhenNextAlarmWillBe(alarm));
    }
}