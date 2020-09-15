package com.example.alarm_app.ui.settings;

import android.app.Application;
import android.content.Context;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.auth.CredentialsHolder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

public class SettingsViewModel extends AndroidViewModel {

    private MutableLiveData<String> accountSummaryLiveData;

    public SettingsViewModel(@NonNull final Application application) {
        super(application);
        accountSummaryLiveData = new MutableLiveData<>();
        CredentialsHolder.getInstance().addCredentialsChangedListener(new CredentialsHolder.CredentialsChangedListener() {
            @Override
            public void OnCredentialChanged(Context context) {
                if (PreferenceManager
                        .getDefaultSharedPreferences(context)
                        .getBoolean(CredentialsHolder.IS_CONNECT_CODE, false)) {
                    accountSummaryLiveData.setValue("Connected to account with username: " + CredentialsHolder.getInstance().getUsername());
                } else {
                    accountSummaryLiveData.setValue(application.getString(R.string.account_summary_not_connected));
                }
            }
        });
    }

    public MutableLiveData<String> getAccountSummaryLiveData() {
        return accountSummaryLiveData;
    }
}
