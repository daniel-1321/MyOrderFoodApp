package com.example.msiseri.orderapp.Model;

import com.example.msiseri.orderapp.Model.Order;

import java.util.List;

/**
 * Created by MSI SERI on 09-Dec-17.
 */

public class Request {
    private String phone;
    private String name;
    private String time;
    private String total;
    private String status;
    private String people;
    private List<Order> foods;

    public Request() {
    }

    public Request(String phone, String name, String time, String total, String status, String people, List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.time = time;
        this.total = total;
        this.status = status;
        this.people = people;
        this.foods = foods;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
