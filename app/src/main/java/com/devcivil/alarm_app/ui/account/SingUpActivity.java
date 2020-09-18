package com.devcivil.alarm_app.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devcivil.alarm_app.MainActivity;
import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.ConnectionToAlarmServer;
import com.devcivil.alarm_app.alarmserver.auth.Credentials;
import com.devcivil.alarm_app.alarmserver.auth.CredentialsHolder;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SingUpActivity extends AppCompatActivity {

    private Button btnSingUp;
    private EditText textLogin, textEmail, textPassword;
    private ProgressBar progressBarCreateAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_sing_up);


        btnSingUp = findViewById(R.id.btn_sing_up);
        textLogin = findViewById(R.id.txt_user_name);
        textEmail = findViewById(R.id.txt_email);
        textPassword = findViewById(R.id.txt_password);
        progressBarCreateAccount = findViewById(R.id.progress_bar_creating_account);

        final Context context = this;
        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Validator
                String username = textLogin.getText().toString();
                String email = textEmail.getText().toString();
                String password = textPassword.getText().toString();
                if (username.length() == 0){
                    Toast.makeText(context, "Login cannot be empty", Toast.LENGTH_LONG).show();
                } else if (email.length() == 0){
                    Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_LONG).show();
                } else if (!email.contains("@")){
                    Toast.makeText(context, "Email have to have @", Toast.LENGTH_LONG).show();
                } else if (password.length() == 0){
                    Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_LONG).show();
                } else if (password.length() < 4){
                    Toast.makeText(context, "Password have to have min 4 characters", Toast.LENGTH_LONG).show();
                } else{
                    //                Add account
                    progressBarCreateAccount.setVisibility(View.VISIBLE);
                    ConnectionToAlarmServer.createNewAccount(
                            context,
                            new Credentials(username, password, email),
                            new ConnectionToAlarmServer.OnAccountCreate() {
                                @Override
                                public void OnCreate(Credentials credentials) {
                                    Toast.makeText(context, "Account created successful", Toast.LENGTH_SHORT).show();
                                    CredentialsHolder.getInstance().setCredentials(credentials, context);
                                    startActivity(new Intent(context, MainActivity.class));
                                }
                            },
                            new ConnectionToAlarmServer.OnAccountNotCreate() {
                                @Override
                                public void OnNotCreate(String message) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                }
        });
    }
}
