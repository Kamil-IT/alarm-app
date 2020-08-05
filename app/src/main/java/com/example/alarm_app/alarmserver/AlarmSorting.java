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
import java.util.List;

import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.CUSTOM;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.SINGLE;

public class AlarmSorting {

    public AlarmSorting() {
    }

    //    Sorting
//      TODO check is time added to entities
    protected List<AlarmDto> getStaticAlarmsSortedByTime(List<AlarmDto> alarmsDto) {
        List<AlarmDto> alarmsToSort = alarmsDto;
        if (alarmsToSort.isEmpty() || alarmsToSort.size() == 1)
            return alarmsToSort;
        Collections.sort(alarmsToSort, new AlarmDtoComparator());
        return alarmsToSort;
    }

    protected List<AlarmFor14Days> getStaticAlarmsSortedByTimeFor14Days(List<AlarmDto> alarmsDto) {
        List<AlarmFor14Days> alarmsSorting = new ArrayList<>();
        long weekInMillis = 604800000L;

        for (AlarmDto alarm : alarmsDto) {
//            Get alarm dates when play
            List<java.util.Date> alarmPlayNext = AlarmDtoComparator.getDatesAlarmPlay(alarm, System.currentTimeMillis());
            alarmPlayNext.addAll(AlarmDtoComparator.getDatesAlarmPlay(alarm, System.currentTimeMillis() + weekInMillis));
//            Sort dates
            Collections.sort(alarmPlayNext, new AlarmDtoComparator.DateUtilSort());
//            Add all variables
            for (java.util.Date date : alarmPlayNext) {
                if (date.after(new java.util.Date())){
                    alarmsSorting.add(new AlarmFor14Days(
                            alarm.getId(),
                            alarm.getName(),
                            alarm.getAlarmTurnOffType(),
                            alarm.getSnooze(),
                            alarm.getActive(),
                            date
                    ));
                }
            }
        }
        Collections.sort(alarmsSorting, new AlarmsWithTimeFor14DaysSort());
        return alarmsSorting;
    }

    //    Comparators for sorting
    //    TODO: test it
    public static class AlarmDtoComparator implements Comparator<AlarmDto> {
        //        1 -> to o1 mniejsze
        @Override
        public int compare(AlarmDto o1, AlarmDto o2) {
            List<java.util.Date> o1DatesAlarmPlayed = getDatesAlarmPlay(o1, System.currentTimeMillis());
            List<java.util.Date> o2DatesAlarmPlayed = getDatesAlarmPlay(o2, System.currentTimeMillis());

            Collections.sort(o1DatesAlarmPlayed, new DateUtilSort());
            Collections.sort(o2DatesAlarmPlayed, new DateUtilSort());
            try {
                if (o1DatesAlarmPlayed.get(0).after(o2DatesAlarmPlayed.get(0))) {
                    return 1;
                } else if (o1DatesAlarmPlayed.get(0).equals(o2DatesAlarmPlayed.get(0))) {
                    return 0;
                }
            } catch (Exception e) {
                if (o1DatesAlarmPlayed.size() == 0) {
                    return 1;
                }
                return -1;
            }
            return -1;
        }
//TODO check if alarm is not before current time
        public static List<java.util.Date> getDatesAlarmPlay(AlarmDto alarm, long currentTime) {
            List<AlarmFrequencyType> frequencyTypes = new ArrayList<>();
            for (AlarmFrequencyType type :
                    alarm.getAlarmFrequencyType()) {
                if (type != SINGLE || type != CUSTOM)
                    frequencyTypes.add(type);
            }
            List<Date> frequencyCostume = alarm.getAlarmFrequencyCostume();

            List<java.util.Date> dates = new ArrayList<>();

            if (frequencyCostume != null) {
                for (Date date :
                        frequencyCostume) {
                    dates.add(new java.util.Date(date.getYear(), date.getMonth(), date.getDay()));
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentTime);
            int dayOfCurrentWeek = calendar.get(Calendar.DAY_OF_WEEK);


            for (AlarmFrequencyType type :
                    frequencyTypes) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(currentTime);
                if (type.getId() == dayOfCurrentWeek) {
                    dates.add(new java.util.Date());
                } else if (type.getId() > dayOfCurrentWeek) {
                    cal.add(Calendar.DAY_OF_MONTH, (int) (type.getId() - dayOfCurrentWeek));
                    dates.add(cal.getTime());
                } else {
                    cal.add(Calendar.DAY_OF_MONTH, (int) (7 + dayOfCurrentWeek - type.getId()));
                    dates.add(cal.getTime());
                }
            }
            Time o1Time = alarm.getTime();

            for (java.util.Date date :
                    dates) {
                date.setSeconds(o1Time.getSeconds());
                date.setMinutes(o1Time.getMinutes());
                date.setHours(o1Time.getHours());
            }
            return dates;
        }

        public static class DateUtilSort implements Comparator<java.util.Date> {

            @Override
            public int compare(java.util.Date o1, java.util.Date o2) {
                if (o1.after(o2)) return 1;
                else if (o1 == o2) return 0;
                else return 0;
            }
        }
    }

    public static class AlarmsWithTimeFor14DaysSort implements Comparator<AlarmFor14Days> {

        @Override
        public int compare(AlarmFor14Days o1, AlarmFor14Days o2) {
            if (o1.getAlarmBe().after(o2.getAlarmBe())) return 1;
            else if (o1.getAlarmBe() == o2.getAlarmBe()) return 0;
            else return -1;
        }
    }
}
