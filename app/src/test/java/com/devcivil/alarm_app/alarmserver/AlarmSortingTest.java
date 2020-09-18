package com.devcivil.alarm_app.alarmserver;

import com.devcivil.alarm_app.alarmserver.model.AlarmDto;
import com.devcivil.alarm_app.alarmserver.model.AlarmFor14Days;
import com.devcivil.alarm_app.alarmserver.model.AlarmFrequencyType;
import com.devcivil.alarm_app.alarmserver.model.Date;
import com.devcivil.alarm_app.alarmserver.model.RingType;
import com.devcivil.alarm_app.alarmserver.model.Snooze;
import com.devcivil.alarm_app.alarmserver.model.Time;
import com.devcivil.alarm_app.alarmserver.model.TurnOffType;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AlarmSortingTest {

    List<AlarmDto> alarms = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 5);
        alarms.add(new AlarmDto(
                "minutes+5",
                "",
                "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L,
                RingType.BIRDS,
                Collections.singletonList(AlarmFrequencyType.CUSTOM),
                true,
                Collections.singletonList(new Date(
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.YEAR))),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        ));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 5);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 5);
        alarms.add(new AlarmDto(
                "hour+5",
                "",
                "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L,
                RingType.BIRDS,
                Collections.singletonList(AlarmFrequencyType.CUSTOM),
                true,
                Collections.singletonList(new Date(
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.YEAR))),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        ));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 5);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 5);
        alarms.add(new AlarmDto(
                "minutes-5",
                "",
                "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L,
                RingType.BIRDS,
                Collections.singletonList(AlarmFrequencyType.CUSTOM),
                true,
                Collections.singletonList(new Date(
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.YEAR))),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        ));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 10);
        alarms.add(new AlarmDto(
                "minutes-5",
                "",
                "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L,
                RingType.BIRDS,
                Collections.singletonList(AlarmFrequencyType.MONDAY),
                true,
                Collections.singletonList(new Date(
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.YEAR))),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        ));


//        alarmFor14Days.add(new AlarmFor14Days("ID", "Alarm1", TurnOffType.NORMAL, Snooze.MIN_5, true));

    }

    @Test
    public void getStaticAlarmsSortedByTime() {
//        TODO: Create tests for it
    }

    @Test
    public void getStaticAlarmsSortedByTimeFor14Days() {
//        TODO: Create tests for it
    }


    @Test
    public void alarmDtoComparatorCompare() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 5);


        AlarmDto alarmFirst = new AlarmDto(
                "1", "", "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L, RingType.BIRDS,
                Collections.singletonList(AlarmFrequencyType.CUSTOM),
                true,
                Collections.singletonList(new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        );

        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 2);
        AlarmDto alarmSecond = new AlarmDto(
                "2", "", "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L, RingType.BIRDS,
                Collections.singletonList(AlarmFrequencyType.CUSTOM),
                true,
                Collections.singletonList(new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        );

        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 2);
        AlarmDto alarm3 = new AlarmDto(
                "3", "", "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L, RingType.BIRDS,
                Collections.singletonList(AlarmFrequencyType.CUSTOM),
                true,
                Collections.singletonList(new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        );


        List<AlarmDto> alarms = new ArrayList<>();
        alarms.add(alarmSecond);
        alarms.add(alarmFirst);
        alarms.add(alarm3);
        Collections.sort(alarms, new AlarmSorting.AlarmDtoComparator());

        assertEquals(3, alarms.size());
        assertEquals(alarmFirst.getId(), alarms.get(0).getId());
        assertEquals(alarmSecond.getId(), alarms.get(1).getId());
        assertEquals(alarm3.getId(), alarms.get(2).getId());
    }


    @Test
    public void alarmDtoComparatorGetDatesWhenAlarmPlayWeekCustom() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 5);

        List<java.util.Date> datesWhenAlarmPlayWeek = AlarmSorting.AlarmDtoComparator.getDatesWhenAlarmPlayWeek(new AlarmDto(
                "", "", "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L, RingType.BIRDS,
                Collections.singletonList(AlarmFrequencyType.CUSTOM),
                true,
                Collections.singletonList(new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        ), System.currentTimeMillis());

        assertEquals(1, datesWhenAlarmPlayWeek.size());
        assertEquals(calendar.getTime().toString(), datesWhenAlarmPlayWeek.get(0).toString());
    }


    @Test
    public void alarmDtoComparatorGetDatesWhenAlarmPlayWeekDayOfWeek() {
        long currentTime = 1598353847092L;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 5);

        List<AlarmFrequencyType> alarmFrequencyType = new ArrayList<>();
        alarmFrequencyType.add(AlarmFrequencyType.MONDAY);
        alarmFrequencyType.add(AlarmFrequencyType.FRIDAY);
        alarmFrequencyType.add(AlarmFrequencyType.SUNDAY);

        List<java.util.Date> datesWhenAlarmPlayWeek = AlarmSorting.AlarmDtoComparator.getDatesWhenAlarmPlayWeek(new AlarmDto(
                "", "", "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L, RingType.BIRDS,
                alarmFrequencyType,
                true,
                Collections.<Date>emptyList(),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        ), currentTime);
        int sumOfDaysInWeek = 0;
        for (java.util.Date date :
                datesWhenAlarmPlayWeek) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            sumOfDaysInWeek += cal.get(Calendar.DAY_OF_WEEK);
        }

        assertEquals(3, datesWhenAlarmPlayWeek.size());
        assertEquals(AlarmFrequencyType.MONDAY.getId() + AlarmFrequencyType.FRIDAY.getId() +
                AlarmFrequencyType.SUNDAY.getId(), sumOfDaysInWeek);
    }


    @Test
    public void alarmDtoComparatorGetDatesWhenAlarmPlayWeekDayOfWeekBeforeCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 5);

        List<java.util.Date> datesWhenAlarmPlayWeek = AlarmSorting.AlarmDtoComparator.getDatesWhenAlarmPlayWeek(new AlarmDto(
                "", "", "",
                new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                0L, RingType.BIRDS,
                new ArrayList<AlarmFrequencyType>(),
                true,
                Collections.<Date>emptyList(),
                TurnOffType.NORMAL,
                Snooze.MIN_5
        ), System.currentTimeMillis());

        assertEquals(0, datesWhenAlarmPlayWeek.size());
    }


    @Test
    public void AlarmDtoComparatorDateUtilSortCompare() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        java.util.Date dateCurrent = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        java.util.Date dateBefore = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 2);
        java.util.Date dateAfter = calendar.getTime();

        List<java.util.Date> dates = Arrays.asList(dateCurrent, dateBefore, dateAfter);

        Collections.sort(dates, AlarmSorting.AlarmDtoComparator.DateUtilSort.getInstance());

        assertEquals(3, dates.size());
        assertEquals(dateBefore.toString(), dates.get(0).toString());
        assertEquals(dateCurrent.toString(), dates.get(1).toString());
        assertEquals(dateAfter.toString(), dates.get(2).toString());
    }


    @Test
    public void AlarmsWithTimeFor14DaysSortCompare() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        java.util.Date dateCurrent = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        java.util.Date dateBefore = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 2);
        java.util.Date dateAfter = calendar.getTime();
        AlarmFor14Days alarmCurrent = new AlarmFor14Days();
        alarmCurrent.setAlarmBe(dateCurrent);
        AlarmFor14Days alarmAfter = new AlarmFor14Days();
        alarmAfter.setAlarmBe(dateAfter);
        AlarmFor14Days alarmBefore = new AlarmFor14Days();
        alarmBefore.setAlarmBe(dateBefore);
        List<AlarmFor14Days> dates = Arrays.asList(alarmCurrent, alarmAfter, alarmBefore);

        Collections.sort(dates, new AlarmSorting.AlarmsWithTimeFor14DaysSort());

        assertEquals(3, dates.size());
        assertEquals(dateBefore.toString(), dates.get(0).getAlarmBe().toString());
        assertEquals(dateCurrent.toString(), dates.get(1).getAlarmBe().toString());
        assertEquals(dateAfter.toString(), dates.get(2).getAlarmBe().toString());
    }

    @Test
    public void onlyAlarmsInFuture() {
//        TODO: Create tests for it
    }
}