package com.devcivil.alarm_app.ui.settings.troubleshooting.xiaomi;

import android.os.Bundle;
import android.view.Window;

import com.devcivil.alarm_app.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for issue when alarm ringing not show on lock screen
 */
public class XiaomiAlarmRingingNotShowOnLockScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.fragment_ringing_not_show_on_lock_screen);
    }
}
