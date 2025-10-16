package com.example.luxevistaresort;

public class Notification {
    public String id;
    public String title;
    public String message;
    public long timestamp;

    public Notification() {}

    public Notification(String id, String title, String message, long timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }
} 