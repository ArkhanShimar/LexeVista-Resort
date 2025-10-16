package com.example.luxevistaresort;

public class Offer {
    public String id;
    public String title;
    public String description;
    public String imageUrl;
    public String badge;

    // No-argument constructor for Firebase
    public Offer() {}

    public Offer(String id, String title, String description, String imageUrl, String badge) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.badge = badge;
    }
} 