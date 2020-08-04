package com.example.alarm_app.alarmserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.auth.AuthTokenHolder;
import com.example.alarm_app.alarmserver.model.AlarmDto;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

import static com.example.alarm_app.alarmserver.ConnectionToAlarmServer.ALARMS_PATH;
import static com.example.alarm_app.alarmserver.ConnectionToAlarmServer.BASE_SERVER_URL;
import static com.example.alarm_app.alarmserver.ConnectionToAlarmServer.getBasicHeaders;

public class AlarmService {

    public static final AlarmService INSTANCE = new AlarmService();

    public static final String ALARMS_DB = "alarms_database_main_context";
    public static final String ALL_ALARM_CODE = "alarms";

    private List<AlarmDto> alarmsDto;
    private SharedPreferences sharedPreferences;

    private List<OnDataSetChanged> listeners = new ArrayList<>();

    private AlarmService() {
    }

    public static AlarmService getInstance() {
        return INSTANCE;
    }

    public void addListener(OnDataSetChanged listener) {
        listeners.add(listener);
    }

    @Nullable
    public List<AlarmDto> getAllAlarms() {
        if (alarmsDto == null && sharedPreferences != null) {
            Gson gson = new Gson();
            String jsonAlarms = sharedPreferences.getString(ALL_ALARM_CODE, null);
            if (jsonAlarms != null) {
                return Arrays.asList(gson.fromJson(jsonAlarms, AlarmDto[].class));
            }

        }
        return alarmsDto;
    }

    public void dataChanged() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            editor.putString(ALL_ALARM_CODE, gson.toJson(alarmsDto));
            editor.apply();
        }

        for (OnDataSetChanged listener :
                listeners) {
            listener.dataChanged();
        }
    }

    public void updateAlarmsFromServer(final Context context) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                JsonArrayRequest objectRequest = new JsonArrayRequest(
                        Request.Method.GET,
                        BASE_SERVER_URL + ALARMS_PATH,
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Gson gson = new Gson();
                                List<AlarmDto> alarmListDto = Arrays.asList(gson.fromJson(response.toString(), AlarmDto[].class));
                                checkListsAndUpdate(context, alarmListDto);
                                Log.i("Alarms", response.toString());
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
                        Map<String, String> headers = getBasicHeaders();
                        headers.putAll(AuthTokenHolder.getINSTANCE().getTokenAsAuthMap());
                        return headers;
                    }
                };


                requestQueue.add(objectRequest);
            }
        });
        thread.start();
    }

    //    TODO: create test for it
    private void checkListsAndUpdate(Context context, List<AlarmDto> alarmDtoNewList) {
        if (!alarmDtoNewList.equals(alarmsDto) && this.alarmsDto != null) {
            for (AlarmDto alarmOld : this.alarmsDto) {
//                Alarm not added to server
                if (alarmOld.getId() == null || alarmOld.getId().equals("")) {
                    creteAlarm(context, alarmOld);
                }

                AlarmDto alarmNew = findById(alarmDtoNewList, alarmOld.getId());

//                Alarm deleted by user directly through server
                if (alarmNew == null) {
                    continue;
                }

//                Different alarms check which was created first
                if (!alarmOld.equals(alarmNew) &&
                        alarmNew.getTimeCreateInMillis() < alarmOld.getTimeCreateInMillis()) {
                    updateAlarm(context, alarmOld);
                }
            }
        }

        alarmsDto = alarmDtoNewList;

        dataChanged();
    }

    public void deleteById(final Context context, final String id) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.DELETE,
                        BASE_SERVER_URL + ALARMS_PATH + "/" + id,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("Alarm deleted", response.toString());
                                updateAlarmsFromServer(context);
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
                        Map<String, String> headers = getBasicHeaders();
                        headers.putAll(AuthTokenHolder.getINSTANCE().getTokenAsAuthMap());
                        return headers;
                    }
                };


                requestQueue.add(objectRequest);
            }
        });
        thread.start();
    }

    public void updateAlarm(final Context context, final AlarmDto alarmDto) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                JSONObject jsonToSend = null;
                try {
                    Gson gson = new Gson();
                    jsonToSend = new JSONObject(gson.toJson(alarmDto));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.PUT,
                        BASE_SERVER_URL + ALARMS_PATH,
                        jsonToSend,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                updateAlarmsFromServer(context);
                                Log.i("Alarm updated", response.toString());
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
                        Map<String, String> headers = getBasicHeaders();
                        headers.putAll(AuthTokenHolder.getINSTANCE().getTokenAsAuthMap());
                        return headers;
                    }
                };


                requestQueue.add(objectRequest);
            }
        });
        thread.start();
    }

    public void creteAlarm(final Context context, final AlarmDto alarmDto) {

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                JSONObject jsonToSend = null;
                try {
                    Gson gson = new Gson();
                    jsonToSend = new JSONObject(gson.toJson(alarmDto));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        BASE_SERVER_URL + ALARMS_PATH,
                        jsonToSend,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Gson gson = new Gson();
                                    AlarmDto alarm = gson.fromJson(response.toString(), AlarmDto.class);
                                    Log.i("Alarm created", response.toString());
                                    addStaticAlarm(alarm);
                                    Toast.makeText(context, R.string.alarm_created, Toast.LENGTH_SHORT).show();
                                } catch (Exception ignored){

//                               TODO:Add static alarm and wait for connection to server or internet
                                    Toast.makeText(context, R.string.alarm_not_created, Toast.LENGTH_SHORT).show();
                                }
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
                        Map<String, String> headers = getBasicHeaders();
                        headers.putAll(AuthTokenHolder.getINSTANCE().getTokenAsAuthMap());
                        return headers;
                    }
                };


                requestQueue.add(objectRequest);
            }
        });
        thread.start();
    }

    /**
     * Find Alarm by id from alarmsDto list, given in function
     *
     * @param alarmsDto Alarms list
     * @param id        alarm id
     * @return null or find alarm
     */
    @Nullable
    private AlarmDto findById(List<AlarmDto> alarmsDto, String id) {
        for (AlarmDto alarm :
                alarmsDto) {
            if (alarm.getId().equals(id)) {
                return alarm;
            }
        }
        return null;
    }

    private void addStaticAlarm(AlarmDto alarmDto){
        this.alarmsDto.add(alarmDto);
        dataChanged();
    }

    /**
     * Find alarm by id in local alarms
     *
     * @param id Alarm id to find
     * @return found alarm
     * @throws IllegalArgumentException if not found
     */
    public AlarmDto findById(String id) {
        for (AlarmDto alarmDto :
                alarmsDto) {
            if (alarmDto.getId().equals(id)) {
                return alarmDto;
            }
        }
        throw new IllegalArgumentException("Id not found");
    }

    public void setSharedPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(ALARMS_DB, Context.MODE_PRIVATE);
    }

    public interface OnDataSetChanged {
        void dataChanged();
    }
}
