package com.lijiajie.mapsystem.trajectory.pojo;

public class recordLoc {
    private int id;
    private int userId;
    private Double lng;
    private Double lat;
    private String timeStemp;
    private String locName;

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getTimeStemp() {
        return timeStemp;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setTimeStemp(String timeStemp) {
        this.timeStemp = timeStemp;
    }
}
