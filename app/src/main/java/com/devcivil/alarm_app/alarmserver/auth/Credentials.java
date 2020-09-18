package com.devcivil.alarm_app.alarmserver.auth;

import java.util.Objects;

import androidx.annotation.Nullable;

public class Credentials {

    private String username;
    private String password;

    @Nullable
    private String email;

    public Credentials() {
    }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Credentials(String username, String password, @Nullable String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getJsonUsernamePassword() {
        return "{" +
                "\"password\": \"" + password + "\"," +
                "\"username\": \"" + username + "\"" +
                "}";
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }
}
