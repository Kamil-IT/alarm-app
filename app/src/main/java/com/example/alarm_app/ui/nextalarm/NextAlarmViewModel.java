package com.example.alarm_app.ui.nextalarm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NextAlarmViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NextAlarmViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}