package com.example.luxevistaresort;

public class User {
    public String name;
    public String email;
    public boolean isAdmin;
    public String address;
    public String dob;
    public String password;

    public User() {}

    public User(String name, String email, boolean isAdmin, String address, String dob, String password) {
        this.name = name;
        this.email = email;
        this.isAdmin = isAdmin;
        this.address = address;
        this.dob = dob;
        this.password = password;
    }
} 