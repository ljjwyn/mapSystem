package com.lijiajie.mapsystem.trajectory.service;

import feign.Headers;
import feign.RequestLine;


/**
 * @describe 模拟获取坐标接口，用feign拉数据
 */
public interface DecisionEngineService {
    @Headers(value = {"Content-Type: application/json","IOT-ApiKey: e279b3b7303d29831f0a7393d8d2d64a"})
    @RequestLine(value="GET /api/v1.0/device/28/sensor/72/points/latest/1")
    Object getEngineMesasgeLJJ();

    @Headers(value = {"Content-Type: application/json","IOT-ApiKey: 60a099ff493b6b1321eb4214e30cc4cf"})
    @RequestLine(value="GET /api/v1.0/device/27/sensor/67/points/latest/1")
    Object getEngineMesasgeLXY();
}
