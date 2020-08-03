package com.example.alarm_app;

import android.os.Bundle;

import com.example.alarm_app.alarmserver.AlarmService;
import com.example.alarm_app.alarmserver.auth.AuthTokenHolder;
import com.example.alarm_app.alarmserver.auth.Credentials;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        createConnectionWitchAlarmService();

//        ConnectionToAlarmServer.getJsonDataFromServer(this, "v2/api-docs");


//        ConnectionToAlarmServer.getJsonDataFromServer(this, "/api/v1/alarm", AuthTokenHolder.getINSTANCE().getTokenAsAuthMap(this));

    }

    private void createConnectionWitchAlarmService(){
        AuthTokenHolder.getINSTANCE()
                .setCredentials(new Credentials("admin", "admin"));
        AuthTokenHolder.getINSTANCE()
                .generateToken(this);
        AlarmService.getInstance()
                .setSharedPreferences(this);
        AlarmService.getInstance()
                .updateAlarmsFromServer(this);
        }

        }