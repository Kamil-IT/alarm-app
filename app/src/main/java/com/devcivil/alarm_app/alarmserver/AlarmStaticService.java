package com.devcivil.alarm_app.alarmserver;

import android.content.Context;
import android.content.SharedPreferences;

import com.devcivil.alarm_app.alarmserver.model.AlarmDto;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlarmStaticService extends AlarmSorting {

    public static final String ALARMS_DB = "alarms_database_main_context";
    public static final String ALL_ALARM_CODE = "alarms";

    protected SharedPreferences sharedPreferences;
    private List<AlarmDto> alarmsDto = new ArrayList<>();
    private List<AlarmService.OnDataSetChanged> listeners = new ArrayList<>();


    protected AlarmStaticService() {
        super();
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
        for (AlarmDto alarm : newAlarms) {
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
                    (alarmsDto.get(i).getId() == null || "".equals(alarmsDto.get(i).getId()))) {
                alarmsDto.remove(i);
                break;
            }
        }
        dataChanged();
    }

    //    Notify data change
    public void dataChanged() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            editor.putString(ALL_ALARM_CODE, gson.toJson(alarmsDto));
            editor.apply();
        }

        for (AlarmService.OnDataSetChanged listener : listeners) {
            listener.dataChanged();
        }
    }

    //    Data change listener

    public void addListener(AlarmService.OnDataSetChanged listener) {
        listeners.add(listener);
    }

    public void removeListener(AlarmService.OnDataSetChanged listener) {
        listeners.remove(listener);
    }

    public interface OnDataSetChanged {
        void dataChanged();
    }
}
