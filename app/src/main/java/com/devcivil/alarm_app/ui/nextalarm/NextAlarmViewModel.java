package com.devcivil.alarm_app.ui.nextalarm;

import android.content.Context;

import com.devcivil.alarm_app.alarmserver.AlarmService;
import com.devcivil.alarm_app.alarmserver.AlarmStaticService;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.devcivil.alarm_app.alarmserver.ConnectionToAlarmServer.isNetworkConnected;

public class NextAlarmViewModel extends ViewModel {

    private MutableLiveData<Boolean> isDataRefreshLiveData;
    private MutableLiveData<Boolean> dataChanged;

    public NextAlarmViewModel() {
        isDataRefreshLiveData = new MutableLiveData<>();
        dataChanged = new MutableLiveData<>();
        dataChanged.setValue(true);
        AlarmService.getInstance().addListener(new AlarmStaticService.OnDataSetChanged() {
            @Override
            public void dataChanged() {
                dataChanged.setValue(!dataChanged.getValue());
            }
        });
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

    public MutableLiveData<Boolean> getIsDataRefreshLiveData() {
        return isDataRefreshLiveData;
    }
}