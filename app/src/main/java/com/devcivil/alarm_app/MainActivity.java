package com.devcivil.alarm_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;

import com.devcivil.alarm_app.alarmreciver.AlarmNotifyService;
import com.devcivil.alarm_app.alarmserver.AlarmService;
import com.devcivil.alarm_app.alarmserver.updator.AlarmUpdateDataReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 25;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) getSupportActionBar().hide();



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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            // TODO: Add warning when user don't permit and how can do it
            if (!Settings.canDrawOverlays(this)) {


                new MaterialAlertDialogBuilder(this)
                        .setTitle("Alarm working")
                        .setMessage("Set in settings display over other apps to Allowed (this app named Alarm)")
                        .setPositiveButton("Do it now!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                                //  It have to work like this
                                //  ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},1);
                            }
                        })
                        .setNegativeButton("Do it later by your self", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                .show();
            }
        }
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