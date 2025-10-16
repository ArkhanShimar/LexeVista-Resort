package com.example.luxevistaresort;

public class Promotion {
    public String id;
    public String title;
    public String description;
    public String imageUrl;
    public String badge;
    public String roomId; // optional, can be null
    public String serviceId; // optional, can be null
    public String validUntil; // new field

    public Promotion() {}

    public Promotion(String id, String title, String description, String imageUrl, String badge, String roomId, String serviceId, String validUntil) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.badge = badge;
        this.roomId = roomId;
        this.serviceId = serviceId;
        this.validUntil = validUntil;
    }
} 