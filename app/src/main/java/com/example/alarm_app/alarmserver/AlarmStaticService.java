package com.example.alarm_app.alarmserver;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.alarm_app.alarmserver.model.AlarmDto;
import com.example.alarm_app.alarmserver.model.AlarmFrequencyType;
import com.example.alarm_app.alarmserver.model.Date;
import com.example.alarm_app.alarmserver.model.Time;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.CUSTOM;
import static com.example.alarm_app.alarmserver.model.AlarmFrequencyType.SINGLE;

public class AlarmStaticService {

    public static final String ALARMS_DB = "alarms_database_main_context";
    public static final String ALL_ALARM_CODE = "alarms";

    protected SharedPreferences sharedPreferences;
    private List<AlarmDto> alarmsDto = new ArrayList<>();
    private List<AlarmService.OnDataSetChanged> listeners = new ArrayList<>();


    protected AlarmStaticService() {
    }

    /**
     * Have to be initial, because it keep all data after reboot app
     *
     * @param context context with alarm db
     */
    public void setSharedPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(ALARMS_DB, Context.MODE_PRIVATE);
        getAllStaticAlarms();
    }

//    Basic operations on list

    protected List<AlarmDto> getAllStaticAlarms() {
        if (alarmsDto.size() == 0 && sharedPreferences != null) {
            Gson gson = new Gson();
            String jsonAlarms = sharedPreferences.getString(ALL_ALARM_CODE, null);
            if (jsonAlarms != null) {
                return new ArrayList<>(Arrays.asList(gson.fromJson(jsonAlarms, AlarmDto[].class)));
            }

        }
        return alarmsDto;
    }

    protected List<AlarmDto> setAllStaticAlarms(List<AlarmDto> newAlarms) {
        alarmsDto.clear();
        for (AlarmDto alarm :
                newAlarms) {
            alarmsDto.add(alarm);
        }
        dataChanged();
        return alarmsDto;
    }

    protected AlarmDto findByIdStaticAlarm(String id) {
        for (AlarmDto alarmDto : alarmsDto) {
            if (alarmDto.getId().equals(id)) return alarmDto;
        }
        throw new IllegalArgumentException("Not found static alarm with id: " + id);
    }

    protected void addStaticAlarm(AlarmDto alarmDto) {
        this.alarmsDto.add(alarmDto);
        dataChanged();
    }

    protected AlarmDto updateStaticAlarmById(String id, AlarmDto alarmDto) {
        for (int i = 0; i < alarmsDto.size(); i++) {
            if (id.equals(alarmsDto.get(i).getId())) {
                alarmsDto.set(i, alarmDto);
                break;
            }
        }
        dataChanged();
        return alarmDto;
    }

    protected AlarmDto updateStaticAlarmByTimeCreateAndIdNull(long timeCreate, AlarmDto alarmDto) {
        for (int i = 0; i < alarmsDto.size(); i++) {
            if (timeCreate == alarmsDto.get(i).getTimeCreateInMillis() &&
                    alarmsDto.get(i).getId() == null) {
                alarmsDto.set(i, alarmDto);
                break;
            }
        }
        dataChanged();
        return alarmDto;
    }

    protected void deleteStaticAlarmById(String id) {
        for (int i = 0; i < alarmsDto.size(); i++) {
            if (id.equals(alarmsDto.get(i).getId())) {
                alarmsDto.remove(i);
                break;
            }
        }
        dataChanged();
    }

    protected void deleteStaticAlarmByTimeAndIdNull(long timeCreate) {
        for (int i = 0; i < alarmsDto.size(); i++) {
            if (timeCreate == alarmsDto.get(i).getTimeCreateInMillis() &&
                    alarmsDto.get(i).getId() == null) {
                alarmsDto.remove(i);
                break;
            }
        }
        dataChanged();
    }


//  Basic item with id from list finder
//    @Nullable
//    protected AlarmDto findById(String id, List<AlarmDto> alarmsDto) {
//        for (AlarmDto alarm : alarmsDto) {
//            if (alarm.getId().equals(id)) return alarm;
//        }
//        return null;
//    }

    //    Notify data change
    public void dataChanged() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            editor.putString(ALL_ALARM_CODE, gson.toJson(alarmsDto));
            editor.apply();
        }

        for (AlarmService.OnDataSetChanged listener :
                listeners) {
            listener.dataChanged();
        }
    }

    //    Data change listener
    public void addListener(AlarmService.OnDataSetChanged listener) {
        listeners.add(listener);
    }

    public interface OnDataSetChanged {
        void dataChanged();
    }

    //    Sorting

    protected List<AlarmDto> getStaticAlarmsSortedByTime() {
        List<AlarmDto> alarmsToSort = alarmsDto;
        if (alarmsToSort.isEmpty() || alarmsToSort.size() == 1)
            return alarmsToSort;
        Collections.sort(alarmsToSort, new AlarmDtoComparator());
        return alarmsToSort;
    }

    //    TODO: test it
    public static class AlarmDtoComparator implements Comparator<AlarmDto> {
        //        1 -> to o1 mniejsze
        @Override
        public int compare(AlarmDto o1, AlarmDto o2) {
            List<java.util.Date> o1DatesAlarmPlayed = getDatesAlarmPlayed(o1);
            List<java.util.Date> o2DatesAlarmPlayed = getDatesAlarmPlayed(o2);

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

        private List<java.util.Date> getDatesAlarmPlayed(AlarmDto alarm) {
            Set<AlarmFrequencyType> frequencyTypes = alarm.getAlarmFrequencyType();
            List<Date> frequencyCostume = alarm.getAlarmFrequencyCostume();

            List<java.util.Date> dates = new ArrayList<>();

            if (frequencyCostume != null) {
                for (Date date :
                        frequencyCostume) {
                    dates.add(new java.util.Date(date.getYear(), date.getMonth(), date.getDay()));
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int dayOfCurrentWeek = calendar.get(Calendar.DAY_OF_WEEK);

            if (frequencyTypes != null) {
                frequencyTypes.remove(SINGLE);
                frequencyTypes.remove(CUSTOM);

                for (AlarmFrequencyType type :
                        frequencyTypes) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
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
                if (o1.before(o2)) return 1;
                else return 0;
            }
        }
    }

//         TODO:
//    AlarmDto updateStaticAlarmByTimeCreateAndIdNull(String id);
//
//    List<AlarmDto> getStaticAlarmsByTime();
}
