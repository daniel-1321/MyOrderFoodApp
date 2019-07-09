package com.example.msiseri.orderapp.Model;

/**
 * Created by MSI SERI on 03-Dec-17.
 */

public class User {
    private String Name;
    private String Password;
    private String Phone;
    private String Staff;
    private String SecureCode;

    public User() {
    }

    public User(String name, String password, String secureCode) {
        Name = name;
        Password = password;
        Staff = "false";
        this.SecureCode = secureCode;
    }

    public String getSecureCode() {
        return SecureCode;
    }

    public void setSecureCode(String secureCode) {
        SecureCode = secureCode;
    }

    public String getStaff() {
        return Staff;
    }

    public void setStaff(String staff) {
        Staff = staff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
