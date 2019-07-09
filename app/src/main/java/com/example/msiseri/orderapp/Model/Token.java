package com.example.msiseri.orderapp.Model;

/**
 * Created by MSI SERI on 04-Jan-18.
 */

public class Token {
    private String token;
    private boolean isServerToken;

    public Token() {
    }

    public Token(String token, boolean isServerToken) {
        this.token = token;
        this.isServerToken= isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isService() {
        return isServerToken;
    }

    public void setService(boolean service) {
        isServerToken= service;
    }
}

