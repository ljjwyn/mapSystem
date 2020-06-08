package com.lijiajie.mapsystem.trajectory.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LngLat implements Serializable {

    public static final String QUEUE = "QUEUE_LNGLAT_01";

    public static final String EXCHANGE = "EXCHANGE_LNGLAT_01";

    public static final String ROUTING_KEY = "ROUTING_LNGLAT_01";

    private String Lng;

    private String Lat;
}
