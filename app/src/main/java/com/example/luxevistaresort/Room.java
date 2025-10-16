package com.example.luxevistaresort;

import java.util.List;

public class Room {
    public String id;
    public String name;
    public String description;
    public double price;
    public String imageUrl;
    public List<String> tags;
    public boolean isAC;
    public int capacity;

    public Room() {}

    public Room(String id, String name, String description, double price, String imageUrl, List<String> tags, boolean isAC, int capacity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.isAC = isAC;
        this.capacity = capacity;
    }
} 