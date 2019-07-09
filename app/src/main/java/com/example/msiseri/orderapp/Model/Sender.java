package com.example.msiseri.orderapp.Model;

/**
 * Created by MSI SERI on 04-Jan-18.
 */

public class Sender {
    public String to;
    public Notification notification;

    public Sender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }
}
