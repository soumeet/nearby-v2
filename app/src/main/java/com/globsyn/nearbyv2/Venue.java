package com.globsyn.nearbyv2;

public class Venue {
    private String id, name, vicinity;
    private double lat, lng;
    public Venue(String id, String name, String vicinity, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getVicinity() {
        return vicinity;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}