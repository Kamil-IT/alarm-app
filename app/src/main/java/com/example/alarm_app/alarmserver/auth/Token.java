package com.example.alarm_app.alarmserver.auth;

import java.util.Objects;

public class Token {

    private String jwt;

    public Token(String jwt) {
        this.jwt = jwt;
    }

    public Token() {
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token1 = (Token) o;
        return Objects.equals(jwt, token1.jwt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jwt);
    }
}
