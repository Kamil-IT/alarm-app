package com.example.alarm_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.example.alarm_app.alarmreciver.AlarmNotifyService;
import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.updator.AlarmUpdateDataReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();


        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_next_alarm, R.id.navigation_timer,
                R.id.navigation_stopwatch, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        mContext = this;

        createConnectionWitchAlarmService();
        initAlarmNotificationAndService();
    }

    private void createConnectionWitchAlarmService() {
        AlarmUpdateDataReceiver receiver = new AlarmUpdateDataReceiver();
        receiver.onReceive(this, new Intent(this, AlarmUpdateDataReceiver.class));
    }

    private void initAlarmNotificationAndService() {
//        Create service and notification
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (AlarmService.getInstance().getAllAlarms().size() == 0) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intentService = new Intent(mContext, AlarmNotifyService.class);
                ContextCompat.startForegroundService(mContext, intentService);

            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}