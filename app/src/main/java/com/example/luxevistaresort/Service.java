package com.example.luxevistaresort;

import java.util.List;

public class Service {
    public String id;
    public String name;
    public String description;
    public double price;
    public String imageUrl;
    public List<String> tags;

    public Service() {}

    public Service(String id, String name, String description, double price, String imageUrl, List<String> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.tags = tags;
    }
} 