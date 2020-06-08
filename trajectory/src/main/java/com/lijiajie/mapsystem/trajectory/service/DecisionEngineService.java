package com.lijiajie.mapsystem.trajectory.service;

import feign.Headers;
import feign.RequestLine;

import java.util.Map;


/**
 * @describe 模拟获取坐标接口，用feign控制传输
 */
public interface DecisionEngineService {
    @Headers(value = {"Content-Type: application/json"})
    @RequestLine(value="POST /locationProducer/control")
    Object controlLocPost(Map<String,String> requestParam);
}
