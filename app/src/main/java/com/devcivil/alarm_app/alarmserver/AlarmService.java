package com.devcivil.alarm_app.alarmserver;

import android.content.Context;
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
import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.auth.AuthTokenHolder;
import com.devcivil.alarm_app.alarmserver.model.AlarmDto;
import com.devcivil.alarm_app.alarmserver.model.AlarmFor14Days;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import static com.devcivil.alarm_app.alarmserver.ConnectionToAlarmServer.ALARMS_PATH;
import static com.devcivil.alarm_app.alarmserver.ConnectionToAlarmServer.BASE_SERVER_URL;
import static com.devcivil.alarm_app.alarmserver.ConnectionToAlarmServer.getBasicHeaders;

public class AlarmService extends AlarmStaticService {

    List<String> idAlarmsToDelete = new ArrayList<>();

    public static final AlarmService INSTANCE = new AlarmService();

    public static AlarmService getInstance() {
        return INSTANCE;
    }

    private AlarmService() {
        super();
    }

    @NonNull
    public List<AlarmDto> getAllAlarms() {
        if (getAllStaticAlarms() != null) return getAllStaticAlarms();
        else return new ArrayList<>();
    }

    public void updateAlarmsFromServer(final Context context) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        for (String id :
                idAlarmsToDelete) {
            deleteById(context, id);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
                        Log.i("Alarm server all update", "Token isn't ready to use");
                        notifyNotGeneratedToken(context);
                        Thread.currentThread().interrupt();
                        return;
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
                                List<AlarmDto> alarmListDto = new LinkedList<>(Arrays.asList(gson.fromJson(response.toString(), AlarmDto[].class)));
                                checkListsAndUpdate(context, alarmListDto);
                                Log.i("Alarm server all update", response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Alarm server all update", error.toString());

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
        if (!alarmDtoNewList.equals(getAllStaticAlarms())) {
            for (AlarmDto alarmOld : getAllStaticAlarms()) {
//                Alarm not added to server
                if (alarmOld.getId() == null || alarmOld.getId().equals("")) {
                    creteAlarm(context, alarmOld);
                }

                AlarmDto alarmNew = null;
                for (AlarmDto alarm : alarmDtoNewList) {
                    if (alarm.getId().equals(alarmOld.getId())) alarmNew = alarm;
                }

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
        setAllStaticAlarms(alarmDtoNewList);
    }

    public void deleteById(final Context context, final String id) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
                        Log.e("Alarm service delete", "Token isn't ready to use");
                        deleteStaticAlarmById(id);
                        Toast.makeText(context, R.string.alarm_delete_local, Toast.LENGTH_SHORT).show();
                        idAlarmsToDelete.add(id);
                        notifyNotGeneratedToken(context);
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.DELETE,
                        BASE_SERVER_URL + ALARMS_PATH + "/" + id,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("Alarm service delete", response.toString());
                                deleteStaticAlarmById(id);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Alarm service delete", error.toString());
                                deleteStaticAlarmById(id);
                                Toast.makeText(context, R.string.alarm_delete_local, Toast.LENGTH_SHORT).show();
                                idAlarmsToDelete.add(id);
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

    public void deleteStaticByTimeCreate(long time) {
        super.deleteStaticAlarmByTimeAndIdNull(time);
    }

    public void updateAlarm(final Context context, final AlarmDto alarmDto) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonToSend = getJsonObject(alarmDto);
                final AlarmDto alarmOld = alarmDto;

                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.PUT,
                        BASE_SERVER_URL + ALARMS_PATH,
                        jsonToSend,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                AlarmDto alarmFromServer = gson.fromJson(response.toString(), AlarmDto.class);
                                Log.i("Alarm service update", response.toString());
                                if (alarmOld.getId() != null) {
                                    updateStaticAlarmById(alarmOld.getId(), alarmFromServer);
                                } else {
                                    updateStaticAlarmByTimeCreateAndIdNull(alarmOld.getTimeCreateInMillis(), alarmFromServer);
                                }
                                Toast.makeText(context, R.string.alarm_updated, Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Alarm service update", error.toString());
                                if (alarmOld.getId() != null) {
                                    updateStaticAlarmById(alarmOld.getId(), alarmOld);
                                } else {
                                    updateStaticAlarmByTimeCreateAndIdNull(alarmOld.getTimeCreateInMillis(), alarmOld);
                                }
                                Toast.makeText(context, R.string.alarm_update_local, Toast.LENGTH_SHORT).show();
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
        if (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
            Log.e("Alarm service update", "Token isn't ready to use");
            notifyNotGeneratedToken(context);
            if (alarmDto.getId() != null) {
                updateStaticAlarmById(alarmDto.getId(), alarmDto);
            } else {
                updateStaticAlarmByTimeCreateAndIdNull(alarmDto.getTimeCreateInMillis(), alarmDto);
            }
            Toast.makeText(context, R.string.alarm_update_local, Toast.LENGTH_SHORT).show();
        } else {
            thread.start();
        }
    }

    public void creteAlarm(final Context context, final AlarmDto alarmDto) {

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    if (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
                        Log.e("Alarm service create", "Token isn't ready to use");
                        notifyNotGeneratedToken(context);
                        addStaticAlarm(alarmDto);
                        Toast.makeText(context, R.string.alarm_created_local, Toast.LENGTH_SHORT).show();
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                JSONObject jsonToSend = getJsonObject(alarmDto);

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
                                    Log.i("Alarm service create", response.toString());
                                    addStaticAlarm(alarm);
                                    Toast.makeText(context, R.string.alarm_created, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(context, R.string.alarm_not_created, Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Alarm service create", error.toString());
                                addStaticAlarm(alarmDto);
                                Toast.makeText(context, R.string.alarm_created_local, Toast.LENGTH_SHORT).show();
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
        if (!AuthTokenHolder.getINSTANCE().isTokenReadyToUse()) {
            Log.e("Alarm service create", "Token isn't ready to use");
            addStaticAlarm(alarmDto);
            Toast.makeText(context, R.string.alarm_created_local, Toast.LENGTH_SHORT).show();
            notifyNotGeneratedToken(context);
        } else {
            thread.start();
        }
    }

    private JSONObject getJsonObject(AlarmDto alarmDto) {
        Gson gson = new Gson();
        JSONObject jsonToSend = null;
        try {
            jsonToSend = new JSONObject(gson.toJson(alarmDto));
        } catch (JSONException e) {
            Log.e("Convert error", "Cannot convert AlarmDto to json, Exception message: " + e.getMessage());
            e.printStackTrace();
        }
        return jsonToSend;
    }

    public List<AlarmDto> getSortedActiveAlarms() {
        List<AlarmDto> alarmsToSort = new LinkedList<>();
        for (AlarmDto alarm : getStaticAlarmsSortedByTime(getAllStaticAlarms())) {
            if (alarm.getActive() == true) alarmsToSort.add(alarm);
        }
        return alarmsToSort;
    }

    public List<AlarmFor14Days> getSortedActiveAlarmsFor14Days() {
        List<AlarmFor14Days> alarmsToSort = new LinkedList<>();
        for (AlarmFor14Days alarm : getStaticAlarmsSortedByTimeFor14Days(getAllStaticAlarms())) {
            if (alarm.getActive() == true) alarmsToSort.add(alarm);
        }
        return alarmsToSort;
    }

    public AlarmFor14Days getNextStaticAlarm10sAfterActivation() {
        return getNextStaticAlarm10sBefore(getAllStaticAlarms());
    }

    private void notifyNotGeneratedToken(Context context){
        AuthTokenHolder.getINSTANCE().generateToken(context);
    }


}
