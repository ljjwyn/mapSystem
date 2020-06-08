package com.lijiajie.mapsystem.trajectory.pojo;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LocationInfo implements Serializable {
    private static final long serialVersionUID = 15964969802L;

    public static final String QUEUE = "QUEUE_LOC_01";

    public static final String EXCHANGE = "EXCHANGE_LOC_01";

    public static final String ROUTING_KEY = "ROUTING_LOC_01";
    private int id;
    private String name;
    private String userId;
    private String description;
    private double latitude;
    private double longitude;
    private double altitude;
    private String gcs;
    private String timestamp;
    private String timeCreated;
    private String locValues;
}
