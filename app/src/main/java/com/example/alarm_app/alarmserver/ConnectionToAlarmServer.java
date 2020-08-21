package com.example.alarm_app.alarmserver;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.HashMap;
import java.util.Map;

public class ConnectionToAlarmServer {

    public static final String BASE_SERVER_URL = "http://192.168.1.13:8080/";

    //     Authentication
    public static final String TOKEN_PATH = "api/auth";
    public static final String DELETE_ACCOUNT_PATH = "api/deleteaccount";
    public static final String NEW_ACCOUNT_PATH = "api/newaccount";

    //    Alarms
    public static final String ALARMS_PATH = "api/v1/alarm";


    public static Map<String, String> getBasicHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("accept", "*/*");
        return headers;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
