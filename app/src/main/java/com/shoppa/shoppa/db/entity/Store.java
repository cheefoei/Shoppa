package com.shoppa.shoppa.db.entity;

public class Store {

    private String id;
    private String name;
    private double longitude;
    private double latitude;

    public Store() {
    }

    public Store(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Store(String name, double longitude, double latitude) {

        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
