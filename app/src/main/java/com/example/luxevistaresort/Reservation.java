package com.example.luxevistaresort;

public class Reservation {
    public String id;
    public String userId;
    public String userEmail;
    public String serviceName;
    public String date;
    public String time;
    public long timestamp;

    public Reservation() {}

    public Reservation(String id, String userId, String userEmail, String serviceName, String date, String time) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.serviceName = serviceName;
        this.date = date;
        this.time = time;
        this.timestamp = System.currentTimeMillis();
    }
} 