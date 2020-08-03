package com.example.alarm_app.alarmserver.auth;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.alarm_app.alarmserver.ConnectionToAlarmServer.BASE_SERVER_URL;
import static com.example.alarm_app.alarmserver.ConnectionToAlarmServer.TOKEN_PATH;
import static com.example.alarm_app.alarmserver.ConnectionToAlarmServer.getBasicHeaders;

public class AuthTokenHolder {

    private static final AuthTokenHolder INSTANCE = new AuthTokenHolder();

    private static final int MIN_IN_MILLIS = 60000;
    private Calendar tokenValidation;

    private Credentials credentials;
    private Calendar tokenTakenTime;
    private String token;

    private AuthTokenHolder() {
        tokenValidation = Calendar.getInstance();
        tokenValidation.setTimeInMillis(MIN_IN_MILLIS * 30);
        tokenTakenTime = Calendar.getInstance();
        tokenTakenTime.setTimeInMillis(System.currentTimeMillis() - MIN_IN_MILLIS * 30);
    }

    public static AuthTokenHolder getINSTANCE() {
        return INSTANCE;
    }

    public Calendar getTokenValidation() {
        return tokenValidation;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public Calendar getTokenTakenTime() {
        return tokenTakenTime;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Map<String, String> getTokenAsAuthMap() {
        if (!isTokenReadyToUse()){
            throw new IllegalArgumentException("Token isn't ready to use");
        }
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);
        return header;
    }

    public void generateToken(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject jsonToSend = null;
        try {
            jsonToSend = new JSONObject(credentials.getJsonUsernamePassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final StringBuilder responseToReturn = new StringBuilder();

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_SERVER_URL + TOKEN_PATH,
                jsonToSend,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        saveGeneratedToken(response);
                        Log.i("Token generated", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest response", error.toString());

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicHeaders();
            }
        };

        requestQueue.add(objectRequest);
    }

    private void saveGeneratedToken(JSONObject tokenJson) {
        Gson gson = new Gson();
        token = gson.fromJson(tokenJson.toString(), Token.class).getJwt();
        tokenTakenTime.setTime(new Date());
    }

    public boolean isTokenReadyToUse() {
        return token != null && (tokenTakenTime.getTimeInMillis() + tokenValidation.getTimeInMillis() - MIN_IN_MILLIS >
                System.currentTimeMillis());
    }
}
