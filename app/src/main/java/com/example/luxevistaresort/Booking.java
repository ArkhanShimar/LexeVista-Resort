package com.example.luxevistaresort;

public class Booking {
    public String id;
    public String userId;
    public String userEmail;
    public String roomName;
    public String checkInDate;
    public String checkOutDate;
    public int adults;
    public int children;
    public String specialRequests;
    public long timestamp;

    public Booking() {}

    public Booking(String id, String userId, String userEmail, String roomName, String checkInDate, String checkOutDate, int adults, int children, String specialRequests) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.roomName = roomName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.adults = adults;
        this.children = children;
        this.specialRequests = specialRequests;
        this.timestamp = System.currentTimeMillis();
    }
} 