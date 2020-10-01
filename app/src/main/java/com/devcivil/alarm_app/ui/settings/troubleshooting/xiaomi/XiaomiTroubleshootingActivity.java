package com.devcivil.alarm_app.ui.settings.troubleshooting.xiaomi;

import android.os.Bundle;

import com.devcivil.alarm_app.R;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class XiaomiTroubleshootingActivity extends PreferenceFragmentCompat {
    private Preference btnNotShoOnLockScreen;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefrences_troubleshooting, rootKey);

        btnNotShoOnLockScreen = findPreference(getString(R.string.troubleshooting_xiaomi_lock_screen_key));
    }
}
