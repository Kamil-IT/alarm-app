package com.example.alarm_app.alarmserver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.alarm_app.alarmserver.auth.Credentials;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class ConnectionToAlarmServer {

    public static final String BASE_SERVER_URL = "http://alarmrestapi-env-1.eba-zvyzw6aw.eu-central-1.elasticbeanstalk.com/";

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

    public static void createNewAccount(Context context, final Credentials credentials, @Nullable final OnAccountCreate onAccountCreate,
                                        @Nullable final OnAccountNotCreate onAccountNotCreate){
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Gson gson = new Gson();
                JSONObject jsonToSend = null;
                try {
                    jsonToSend = new JSONObject(gson.toJson(credentials));
                } catch (JSONException e) {
                    Log.e("Convert error", "Cannot convert AlarmDto to json, Exception message: " + e.getMessage());
                    e.printStackTrace();
                }

                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        BASE_SERVER_URL + NEW_ACCOUNT_PATH,
                        jsonToSend,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("Account create", "Account created");
                                if (onAccountCreate != null){
                                    Credentials newCredentials = gson.fromJson(response.toString(), Credentials.class);
                                    newCredentials.setPassword(credentials.getPassword());
                                    onAccountCreate.OnCreate(newCredentials);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Account create", "Account not created");
                                if (onAccountNotCreate != null){
                                    onAccountNotCreate.OnNotCreate(error.getMessage());
                                }
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = getBasicHeaders();
                        headers.putAll(getBasicHeaders());
                        return headers;
                    }

                };
                requestQueue.add(objectRequest);
            }
        });
        thread.start();

    }

    public static void createNewAccount(Context context, Credentials credentials){
        createNewAccount(context, credentials, null, null);
    }

    public interface OnAccountCreate{
        void OnCreate(Credentials credentials);
    }

    public interface OnAccountNotCreate{
        void OnNotCreate(String message);
    }
}
