package com.devcivil.alarm_app.ui.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmreciver.AlarmSyncService;
import com.devcivil.alarm_app.ui.account.LoginActivity;
import com.devcivil.alarm_app.ui.settings.troubleshooting.autostart.AutoStartEnabler;
import com.devcivil.alarm_app.ui.settings.troubleshooting.xiaomi.XiaomiAlarmRingingNotShowOnLockScreenActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference btnSyncInterval, btnAccount, btnSendFeedback, btnTroubleSXiaomi, btnTroubleSAutoStart;
    private SwitchPreferenceCompat switchAutoSync;

    private SettingsViewModel settingsViewModel;

    private Observer<String> observerSummaryAccount = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            btnAccount.setSummary(s);
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        settingsViewModel = new ViewModelProvider(this)
                .get(SettingsViewModel.class);
        setPreferencesFromResource(R.xml.prefrences, rootKey);

        initVariables();

        addListeners();
        accountWorking();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsViewModel.getAccountSummaryLiveData().observe(getViewLifecycleOwner(), observerSummaryAccount);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void addListeners() {
        btnSyncInterval.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog alertDialog = new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.sync_interval_title)
                        .setNegativeButton(R.string.cancel, null)
                        .setItems(R.array.sync_interval_time, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                                        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                                        int interval;
                                        if (which == 0) interval = 15;
                                        else if (which == 1) interval = 30;
                                        else if (which == 2) interval = 60;
                                        else if (which == 3) interval = 60 * 2;
                                        else if (which == 4) interval = 60 * 6;
                                        else if (which == 5) interval = 30 * 12;
                                        else throw new IllegalArgumentException();


                                        editor.putInt(getString(R.string.sync_interval_key), interval);
                                        editor.commit();
                                        AlarmSyncService.syncTimeUpdated(getContext(), true);
                                    }
                                }
                        )
                        .create();
                alertDialog.show();
                return false;
            }
        });

        btnSendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Android app Alarm");
                intent.putExtra(Intent.EXTRA_TEXT, "Hello Dev civil, \n"
                        + "Product: " + Build.PRODUCT + "\n"
                        + "Device: " + Build.DEVICE + "\n"
                        + "Model: " + Build.MODEL + "\n"
                        + "SDK: " + Build.VERSION.SDK_INT + "\n"
                );
                intent.setData(Uri.parse("mailto:kkwolny@vp.pl"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }
        });

        switchAutoSync.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                AlarmSyncService.syncTimeUpdated(getContext(), (Boolean) newValue);
                return true;
            }
        });

        btnTroubleSXiaomi.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), XiaomiAlarmRingingNotShowOnLockScreenActivity.class));
                return false;
            }
        });

        btnTroubleSAutoStart.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AutoStartEnabler.enable(requireContext());
                return false;
            }
        });
    }

    private void accountWorking() {
        btnAccount.setSummary(settingsViewModel.getAccountSummaryLiveData().getValue());
        btnAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    private void initVariables() {
        btnSyncInterval = findPreference(getString(R.string.sync_interval_key));
        btnAccount = findPreference(getString(R.string.account_key));
        btnSendFeedback = findPreference(getString(R.string.feedback_key));
        switchAutoSync = findPreference(getString(R.string.auto_sync_key));
        btnTroubleSXiaomi = findPreference(getString(R.string.troubleshooting_xiaomi_key));
        btnTroubleSAutoStart = findPreference(getString(R.string.troubleshooting_autostart_key));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        settingsViewModel.getAccountSummaryLiveData().removeObserver(observerSummaryAccount);
    }
}