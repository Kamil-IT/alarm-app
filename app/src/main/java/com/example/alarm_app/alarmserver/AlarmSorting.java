package com.example.alarm_app.alarmserver;

import com.example.alarm_app.alarmserver.model.AlarmDto;
import com.example.alarm_app.alarmserver.model.AlarmFor14Days;
import com.example.alarm_app.alarmserver.model.AlarmFrequencyType;
import com.example.alarm_app.alarmserver.model.Date;
import com.example.alarm_app.alarmserver.model.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.Nullable;

import static com.example.alarm_app.alarmserver.AlarmSorting.AlarmDtoComparator.getDatesWhenAlarmPlayWeek;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.CUSTOM;

public class AlarmSorting {

    public static final long WEEK_IN_MILLIS = 604800000L;

    public AlarmSorting() {
    }

    //    Sorting
    protected List<AlarmDto> getStaticAlarmsSortedByTime(List<AlarmDto> alarmsDto) {
        List<AlarmDto> alarmsToSort = alarmsDto;
        if (alarmsToSort.isEmpty() || alarmsToSort.size() == 1) return alarmsToSort;

        Collections.sort(alarmsToSort, new AlarmDtoComparator());
        ArrayList<AlarmDto> alarmSorted = new ArrayList<>();
        for (AlarmDto alarm : alarmsToSort) alarmSorted.add(alarm);
        return onlyAlarmsInFuture(alarmSorted);
    }

    protected List<AlarmFor14Days> getStaticAlarmsSortedByTimeFor14Days(List<AlarmDto> alarmsDto) {
        List<AlarmFor14Days> alarmsSorting = new ArrayList<>();
        long weekInMillis = WEEK_IN_MILLIS;

        for (AlarmDto alarm : alarmsDto) {
//            Get alarm dates when play
            List<java.util.Date> alarmPlayNext = getDatesWhenAlarmPlayWeek(alarm, System.currentTimeMillis());
            alarmPlayNext.addAll(getDatesWhenAlarmPlayWeek(alarm, System.currentTimeMillis() + weekInMillis));
//            Sort dates
            Collections.sort(alarmPlayNext, AlarmDtoComparator.DateUtilSort.getInstance());
//            Add all variables
            for (java.util.Date date : alarmPlayNext) {
                if (date.after(new java.util.Date())) {
                    alarmsSorting.add(new AlarmFor14Days(
                            alarm.getId(),
                            alarm.getName(),
                            alarm.getAlarmTurnOffType(),
                            alarm.getSnooze(),
                            alarm.getActive(),
                            date,
                            alarm.getRingType()
                    ));
                }
            }
        }
        Collections.sort(alarmsSorting, new AlarmsWithTimeFor14DaysSort());
        return alarmsSorting;
    }

    //    Comparators for sorting
    public static class AlarmDtoComparator implements Comparator<AlarmDto> {
        //        1 -> to o1 smaller
        @Override
        public int compare(AlarmDto o1, AlarmDto o2) {
            List<java.util.Date> o1DatesAlarmPlayed = getDatesWhenAlarmPlayWeek(o1, System.currentTimeMillis());
            List<java.util.Date> o2DatesAlarmPlayed = getDatesWhenAlarmPlayWeek(o2, System.currentTimeMillis());

            Collections.sort(o1DatesAlarmPlayed, DateUtilSort.getInstance());
            Collections.sort(o2DatesAlarmPlayed, DateUtilSort.getInstance());

            if (o1DatesAlarmPlayed.size() == 0) return 1;
            else if (o2DatesAlarmPlayed.size() == 0) return -1;

            DateUtilSort dateUtilSort = DateUtilSort.getInstance();
            return dateUtilSort.compare(o1DatesAlarmPlayed.get(0), o2DatesAlarmPlayed.get(0));
        }

        public static List<java.util.Date> getDatesWhenAlarmPlayWeek(AlarmDto alarm, long currentTime) {
            if (alarm.getAlarmFrequencyType() == null) return new ArrayList<>();
            else if (alarm.getAlarmFrequencyType().isEmpty()) return new ArrayList<>();

            List<AlarmFrequencyType> frequencyTypes = new ArrayList<>();
            for (AlarmFrequencyType type :
                    alarm.getAlarmFrequencyType()) {
                if (type != CUSTOM)
                    frequencyTypes.add(type);
            }

            List<java.util.Date> dates = new ArrayList<>();

            for (Date date :
                    alarm.getAlarmFrequencyCostume()) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, date.getYear());
                calendar.set(Calendar.MONTH, date.getMonth() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
                dates.add(calendar.getTime());
            }

            Calendar calendarCurrentTime = Calendar.getInstance();
            calendarCurrentTime.setTimeInMillis(currentTime);
            int dayOfCurrentWeek = calendarCurrentTime.get(Calendar.DAY_OF_WEEK);

            for (AlarmFrequencyType type :
                    frequencyTypes) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(currentTime);
                if (type.getId() == dayOfCurrentWeek) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new java.util.Date(currentTime));
                    calendar.set(Calendar.HOUR_OF_DAY, alarm.getTime().getHours());
                    calendar.set(Calendar.MINUTE, alarm.getTime().getMinutes());
                    calendar.set(Calendar.SECOND, alarm.getTime().getSeconds());
                    if (calendar.getTime().before(new java.util.Date(currentTime))){
                        dates.add(new java.util.Date(currentTime + 86400000 * 7));
                    }
                    else {
                        dates.add(new java.util.Date(currentTime));
                    }
                } else if (type.getId() > dayOfCurrentWeek) {
                    cal.add(Calendar.DAY_OF_MONTH, (int) (type.getId() - dayOfCurrentWeek));
                    dates.add(cal.getTime());
                } else {
                    cal.add(Calendar.DAY_OF_MONTH, (int) (7 + type.getId() - dayOfCurrentWeek));
                    dates.add(cal.getTime());
                }
            }

            Time alarmTime = alarm.getTime();
            List<java.util.Date> datesToReturn = new ArrayList<>();
            for (java.util.Date date :
                    dates) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, alarmTime.getHours());
                calendar.set(Calendar.MINUTE, alarmTime.getMinutes());
                calendar.set(Calendar.SECOND, alarmTime.getSeconds());

                datesToReturn.add(calendar.getTime());
            }


            for (Iterator<java.util.Date> it = datesToReturn.iterator(); it.hasNext(); ) {
                java.util.Date date = it.next();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                if ((calendar.getTimeInMillis() > (calendarCurrentTime.getTimeInMillis() + WEEK_IN_MILLIS)) ||
                        (calendar.getTimeInMillis() < calendarCurrentTime.getTimeInMillis())) {
                    it.remove();
                }
            }
            return datesToReturn;
        }

        public static class DateUtilSort implements Comparator<java.util.Date> {

            private static final DateUtilSort INSTANCE = new DateUtilSort();

            private DateUtilSort() {
            }

            public static DateUtilSort getInstance() {
                return INSTANCE;
            }

            @Override
            public int compare(java.util.Date o1, java.util.Date o2) {
                if (o1.after(o2)) return 1;
                else if (o1 == o2) return 0;
                else return -1;
            }
        }
    }

    public static class AlarmsWithTimeFor14DaysSort implements Comparator<AlarmFor14Days> {

        @Override
        public int compare(AlarmFor14Days o1, AlarmFor14Days o2) {
            AlarmDtoComparator.DateUtilSort dateUtilSort = AlarmDtoComparator.DateUtilSort.getInstance();
            return dateUtilSort.compare(o1.getAlarmBe(), o2.getAlarmBe());
        }
    }

    private List<AlarmDto> onlyAlarmsInFuture(List<AlarmDto> alarms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());

        for (Iterator<AlarmDto> it = alarms.iterator(); it.hasNext(); ) {
            AlarmDto alarm = it.next();
            List<java.util.Date> datesWhenAlarmPlayWeek = getDatesWhenAlarmPlayWeek(alarm, calendar.getTimeInMillis());
            for (Iterator<java.util.Date> iter = datesWhenAlarmPlayWeek.iterator(); iter.hasNext(); ) {
                java.util.Date date = iter.next();
                if (date.before(calendar.getTime())) {
                    iter.remove();
                }

            }
            if (datesWhenAlarmPlayWeek.size() == 0) {
                it.remove();
            }
        }
        return alarms;
    }

    @Nullable
    protected AlarmFor14Days getNextStaticAlarm10sBefore(List<AlarmDto> alarmsDto) {
        List<AlarmFor14Days> alarmsSorting = new ArrayList<>();
        long weekInMillis = WEEK_IN_MILLIS;

        for (AlarmDto alarm : alarmsDto) {
//            Get alarm dates when play
            List<java.util.Date> alarmPlayNext = getDatesWhenAlarmPlayWeek(alarm, System.currentTimeMillis() - 10000);
//            Sort dates
            Collections.sort(alarmPlayNext, AlarmDtoComparator.DateUtilSort.getInstance());
//            Add all variables
            for (java.util.Date date : alarmPlayNext) {
                if (date.after(new java.util.Date(System.currentTimeMillis() - 10000))) {
                    alarmsSorting.add(new AlarmFor14Days(
                            alarm.getId(),
                            alarm.getName(),
                            alarm.getAlarmTurnOffType(),
                            alarm.getSnooze(),
                            alarm.getActive(),
                            date,
                            alarm.getRingType()
                    ));
                }
            }
        }
        Collections.sort(alarmsSorting, new AlarmsWithTimeFor14DaysSort());
        if (alarmsSorting.size() == 0) return null;
        else return alarmsSorting.get(0);
    }
}
