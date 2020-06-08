package com.lijiajie.mapsystem.trajectory.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SimulationLocation implements Serializable {
    private static final long serialVersionUID = 13375361116L;

    public static final String QUEUE = "QUEUE_SL_01";

    public static final String EXCHANGE = "EXCHANGE_SL_01";

    public static final String ROUTING_KEY = "ROUTING_SL_01";
    private int id;
    private String userId;
    private Double lng;
    private Double lat;
    private String timeStemp;
    private String locName;

}
