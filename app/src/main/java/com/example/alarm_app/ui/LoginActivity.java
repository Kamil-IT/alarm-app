package com.example.alarm_app.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.alarm_app.R;
import com.example.alarm_app.alarmserver.auth.CredentialsHolder;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText textPassword, textUsername;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        textUsername = findViewById(R.id.txtUserName);
        textPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        if (CredentialsHolder.getInstance().getUsername() != null){
            textUsername.setText(CredentialsHolder.getInstance().getUsername());
        }

        final Context context = this;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = textUsername.getText().toString();
                String password = textPassword.getText().toString();

                CredentialsHolder.getInstance().setCredentials(username, password, context);
                finish();
            }
        });
    }
}
