package com.devcivil.alarm_app.alarmserver.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import static com.devcivil.alarm_app.alarmserver.ConnectionToAlarmServer.BASE_SERVER_URL;
import static com.devcivil.alarm_app.alarmserver.ConnectionToAlarmServer.TOKEN_PATH;
import static com.devcivil.alarm_app.alarmserver.ConnectionToAlarmServer.getBasicHeaders;

public class CredentialsHolder {

    private static final CredentialsHolder INSTANCE = new CredentialsHolder();
    public static final String IS_CONNECT_CODE = "is_connect_successful";

    private List<CredentialsChangedListener> credentialsChangedListeners = new ArrayList<>();

    public static final String CREDENTIALS_DB = "credentials_db";
    public static final String USERNAME_CODE = "username";
    public static final String PASSWORD_CODE = "password";

    private Context context;

    private Credentials credentials;

    private CredentialsHolder() {
        credentials = new Credentials();
    }

    public static CredentialsHolder getInstance() {
        return INSTANCE;
    }

    /**
     * This method have to be triggered to get username and password
     * @param context whit credentials db
     */
    public void setShearPreferences(Context context){
        this.context = context;
        try {
            getCredentials();
        }
        catch (Exception ignored){
        }
    }

    public Credentials getCredentials() {
        if (context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(CREDENTIALS_DB, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                String username = sharedPreferences.getString(USERNAME_CODE, null);
                String password = sharedPreferences.getString(PASSWORD_CODE, null);
                if (password == null || username == null){
                    throw new IllegalArgumentException("Password or username not specified");
                }
                this.credentials.setUsername(username);
                this.credentials.setPassword(password);
            }
        }
        return credentials;
    }

    public void setCredentials(String username, String password, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDENTIALS_DB, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USERNAME_CODE, username);
            editor.putString(PASSWORD_CODE, password);
            editor.apply();
        }
        credentials.setUsername(username);
        credentials.setPassword(password);

        checkIsCredentialsCorrect();
    }

    public void setCredentials(Credentials credentials, Context context){
        setCredentials(credentials.getUsername(), credentials.getPassword(), context);
    }

    private void checkIsCredentialsCorrect() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject jsonToSend = null;
        try {
            jsonToSend = new JSONObject(CredentialsHolder.getInstance().getJsonUsernamePassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_SERVER_URL + TOKEN_PATH,
                jsonToSend,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Log on successful", Toast.LENGTH_SHORT).show();
                        PreferenceManager
                                .getDefaultSharedPreferences(context)
                                .edit()
                                .putBoolean(IS_CONNECT_CODE, true)
                                .apply();
                        credentialChanged(context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Log on failed", Toast.LENGTH_SHORT).show();
                        PreferenceManager
                                .getDefaultSharedPreferences(context)
                                .edit()
                                .putBoolean(IS_CONNECT_CODE, false)
                                .apply();
                        credentialChanged(context);
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

    @Nullable
    public String getUsername(){
        return credentials.getUsername();
    }

    @Nullable
    public String getPassword(){
        return credentials.getPassword();
    }

    public String getJsonUsernamePassword(){
        return credentials.getJsonUsernamePassword();
    }

    private void credentialChanged(Context context){
        for (CredentialsChangedListener listener :
                credentialsChangedListeners) {
            listener.OnCredentialChanged(context);
        }
    }

    public void addCredentialsChangedListener(CredentialsChangedListener listener){
        credentialsChangedListeners.add(listener);
    }


    public interface CredentialsChangedListener{
        void OnCredentialChanged(Context context);
    }
}
