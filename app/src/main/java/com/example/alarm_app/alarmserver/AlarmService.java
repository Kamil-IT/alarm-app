package com.example.alarm_app.alarmserver;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

    private List<OnDataSetChanged> listeners = new ArrayList<>();

    private AlarmService() {
    }

    public static AlarmService getInstance() {
        return INSTANCE;
    }

    private List<AlarmDto> alarmsDto;

    public List<AlarmDto> getAllAlarms() {
        return alarmsDto;
    }

    public void addListener(OnDataSetChanged listener){
        listeners.add(listener);
    }

    public void dataChanged(){
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
                                AlarmDto[] alarmListDto = gson.fromJson(response.toString(), AlarmDto[].class);
                                checkListsAndUpdate(context, Arrays.asList(alarmListDto));
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

            if (alarmsDto.size() >= alarmDtoNewList.size()) {
                for (int i = 0; i < alarmsDto.size(); i++) {
                    AlarmDto alarmFromNewList = findById(alarmDtoNewList, alarmsDto.get(i).getId());

                    if (alarmFromNewList != null) {
                        if (alarmsDto.get(i).getTimeCreateInMillis() > alarmFromNewList.getTimeCreateInMillis()) {
                            updateAlarm(context, alarmsDto.get(i));
                        }
                    } else {
                        creteAlarm(context, alarmDtoNewList.get(i));
                    }

                }
            } else {
                for (int i = 0; i < alarmDtoNewList.size(); i++) {
                    AlarmDto alarmFromServer;
                    try {
                        alarmFromServer = findById(alarmDtoNewList.get(i).getId());
                    } catch (Exception e) {
                        creteAlarm(context, alarmDtoNewList.get(i));
                        continue;
                    }

                    if (alarmDtoNewList.get(i).getTimeCreateInMillis() < alarmFromServer.getTimeCreateInMillis()) {
                        updateAlarm(context, alarmsDto.get(i));
                    }
                }
            }
        }

        alarmsDto = alarmDtoNewList;
        dataChanged();
    }

    private void updateAlarm(final Context context, final AlarmDto alarmDto) {
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
                                AlarmListDto alarmListDto = gson.fromJson(response.toString(), AlarmListDto.class);
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

    private void creteAlarm(final Context context, final AlarmDto alarmDto) {

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
                        Request.Method.POST,
                        BASE_SERVER_URL + ALARMS_PATH,
                        jsonToSend,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                AlarmListDto alarmListDto = gson.fromJson(response.toString(), AlarmListDto.class);
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

    private static class AlarmListDto {

        List<AlarmDto> alarmListDto;

        public AlarmListDto() {
        }

        public List<AlarmDto> getAlarmListDto() {
            return alarmListDto;
        }

        public void setAlarmListDto(List<AlarmDto> alarmListDto) {
            this.alarmListDto = alarmListDto;
        }
    }

    public interface OnDataSetChanged {
        void dataChanged();
    }
}
