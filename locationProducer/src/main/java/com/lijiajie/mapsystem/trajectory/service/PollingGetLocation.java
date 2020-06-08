package com.lijiajie.mapsystem.trajectory.service;

import com.lijiajie.mapsystem.trajectory.mapper.SimulationLocationMapper;
import com.lijiajie.mapsystem.trajectory.pojo.LocationInfo;
import com.lijiajie.mapsystem.trajectory.pojo.SimulationLocation;
import com.lijiajie.mapsystem.trajectory.producer.LngLatProducer;
import com.lijiajie.mapsystem.trajectory.producer.LocationProducer;
import com.lijiajie.mapsystem.trajectory.producer.SimulationLocProducer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * @describe 利用Scheduled轮询发布消息
 */
@Service
@Data
public class PollingGetLocation {

    @Autowired
    DecisionEngineService decisionEngineService;

    @Autowired
    LngLatProducer lngLatProducer;

    @Autowired
    LocationProducer locationProducer;

    @Autowired
    SimulationLocProducer simulationLocProducer;

    @Autowired
    SimulationLocationMapper simulationLocationMapper;

    @Value("true")
    public String scheduledEnable;

    @Value("21180231335")
    public String userId;

    @Value("21180231334")
    public String userIdJames;

    @Value("21180231336")
    public String userIdDulante;

    @Value("21180231337")
    public String userIdMeixi;

    public int id=1;

    public int idJames = 282;

    public int idDulante = 515;

    public int idMeixi = 738;

    private static final Logger logger = LoggerFactory.getLogger(PollingGetLocation.class);

//    @Scheduled(fixedRate = 5000,initialDelay = 3000)
//    @Async
//    public void executeFileDownLoadTaskLJJ() {
//        Object locationInfo = decisionEngineService.getEngineMesasgeLJJ();
//        LocationInfo locationInfoBean = analysisObject(locationInfo);
//        lngLatProducer.syncSend(locationInfoBean);
//        locationProducer.syncSend(locationInfoBean);
//        logger.info("[线程：{}][发送编号：[{}] 调用完成]", Thread.currentThread().getName(), locationInfoBean.getId());
//    }
//    @Scheduled(fixedRate = 5000,initialDelay = 3000)
//    @Async
//    public void executeFileDownLoadTaskLXY() {
//        Object locationInfo = decisionEngineService.getEngineMesasgeLXY();
//        LocationInfo locationInfoBean = analysisObject(locationInfo);
//        lngLatProducer.syncSend(locationInfoBean);
//        locationProducer.syncSend(locationInfoBean);
//        logger.info("[线程：{}][发送编号：[{}] 调用完成]", Thread.currentThread().getName(), locationInfoBean.getId());
//    }

    @Scheduled(fixedRate = 5000,initialDelay = 3000)
    @Async
    public void executePostLocLJJ() {
        if(!Boolean.parseBoolean(scheduledEnable)){
            logger.info("中断");
            return;
        }
        logger.info("执行");
        SimulationLocation simulationLocation = simulationLocationMapper.getUserLocations(userId,id);
        if(simulationLocation.getId()>0){
            logger.info("id:{},lng:{},lat:{}",simulationLocation.getId(),
                    simulationLocation.getLng(),simulationLocation.getLat());
            simulationLocProducer.syncSend(simulationLocation);
            id++;
        }else {
            logger.info("全表扫描完成");
        }

    }

    @Scheduled(fixedRate = 5000,initialDelay = 3000)
    @Async
    public void executePostLocJames() {
        if(!Boolean.parseBoolean(scheduledEnable)){
            logger.info("中断");
            return;
        }
        logger.info("执行模拟詹姆斯坐标");
        SimulationLocation simulationLocation = simulationLocationMapper.getUserLocations(userIdJames,idJames);
        if(simulationLocation.getId()>0){
            logger.info("线程:{}, 詹姆斯, id:{},lng:{},lat:{}",Thread.currentThread().getId(), simulationLocation.getId(),
                    simulationLocation.getLng(),simulationLocation.getLat());
            simulationLocProducer.syncSend(simulationLocation);
            idJames++;
        }else {
            logger.info("全表扫描完成");
        }

    }

    @Scheduled(fixedRate = 5000,initialDelay = 3000)
    @Async
    public void executePostLocDulante() {
        if(!Boolean.parseBoolean(scheduledEnable)){
            logger.info("中断");
            return;
        }
        logger.info("执行模拟杜兰特坐标");
        SimulationLocation simulationLocation = simulationLocationMapper.getUserLocations(userIdDulante,idDulante);
        if(simulationLocation.getId()>0){
            logger.info("线程:{}, 杜兰特, id:{},lng:{},lat:{}",Thread.currentThread().getId(), simulationLocation.getId(),
                    simulationLocation.getLng(),simulationLocation.getLat());
            simulationLocProducer.syncSend(simulationLocation);
            idDulante++;
        }else {
            logger.info("全表扫描完成");
        }

    }

    @Scheduled(fixedRate = 5000,initialDelay = 3000)
    @Async
    public void executePostLocMeixi() {
        if(!Boolean.parseBoolean(scheduledEnable)){
            logger.info("中断");
            return;
        }
        logger.info("执行模拟梅西坐标");
        SimulationLocation simulationLocation = simulationLocationMapper.getUserLocations(userIdMeixi,idMeixi);
        if(simulationLocation.getId()>0){
            logger.info("线程:{}, 梅西, id:{},lng:{},lat:{}",Thread.currentThread().getId(), simulationLocation.getId(),
                    simulationLocation.getLng(),simulationLocation.getLat());
            simulationLocProducer.syncSend(simulationLocation);
            idMeixi++;
        }else {
            logger.info("全表扫描完成");
        }

    }

    /**
     * @describe 解析feign出来的object。
     * @param object
     * @return
     */
    public static LocationInfo analysisObject(Object object){
        LocationInfo locationInfoBean = new LocationInfo();
        ArrayList locationList = (ArrayList) object;
        LinkedHashMap locationMap = (LinkedHashMap) locationList.get(0);
        String nameAndId = (String)locationMap.get("name");
        locationInfoBean.setId((Integer)locationMap.get("id"));
        locationInfoBean.setName(nameAndId.split("-")[0]);
        locationInfoBean.setUserId(nameAndId.split("-")[1]);
        locationInfoBean.setAltitude((Double)locationMap.get("altitude"));
        locationInfoBean.setDescription((String)locationMap.get("description"));
        locationInfoBean.setGcs((String)locationMap.get("gcs"));
        locationInfoBean.setLatitude((Double)locationMap.get("latitude"));
        locationInfoBean.setLongitude((Double)locationMap.get("longitude"));
        locationInfoBean.setTimeCreated((String)locationMap.get("timeCreated"));
        locationInfoBean.setTimestamp((String)locationMap.get("timestamp"));
        locationInfoBean.setLocValues(locationMap.get("values").toString());
        return locationInfoBean;
    }
}
