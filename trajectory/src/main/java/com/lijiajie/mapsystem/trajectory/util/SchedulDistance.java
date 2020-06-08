package com.lijiajie.mapsystem.trajectory.util;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Data
//@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SchedulDistance {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String KEY_NAME="currentPoint";

    private final String POINT_DISTANCE="pointDistance";

    private String INSIDE_USER_SET="insideUserSet";

    @Autowired
    JedisUtil jedisUtil;

    private Integer taskId;


    @Scheduled(fixedRate = 30000,initialDelay = 5000)
    //@Async
    public void calculateDistance() {
        if(taskId!=null){
            System.out.println(taskId);
            Set<String> userSet = jedisUtil.smembers(INSIDE_USER_SET+"_"+taskId);
            List<String> userIdList = new ArrayList<>(userSet);
            if(!userIdList.isEmpty()){
                List<Map<String,Double>> recordList = new ArrayList<>();
                for (int i = 0; i < userIdList.size(); i++) {
                    Map<String,Object> targetLoc = jedisUtil.getMap(KEY_NAME+"_"+userIdList.get(i));
                    for (int j = i+1; j < userIdList.size(); j++) {
                        Map<String,Object> tempLoc = jedisUtil.getMap(KEY_NAME+"_"+userIdList.get(j));
                        MapHelper mapHelper = new MapHelper();
                        Double distance = mapHelper.getDistance((Double) targetLoc.get("lat")
                                , (Double) targetLoc.get("lng"), (Double) tempLoc.get("lat")
                                , (Double) tempLoc.get("lng"));
                        Map<String,Double> recordMap = new HashMap<>();
                        recordMap.put(userIdList.get(i)+"_"+userIdList.get(j),distance);
                        recordList.add(recordMap);
                        jedisUtil.set(userIdList.get(i)+"_"+userIdList.get(j),distance.toString());
                        logger.info("[calculateDistance],线程:{},目标1:{},目标2:{},距离:{}",Thread.currentThread().getName()
                                , userIdList.get(i), userIdList.get(j), distance);
                    }
                }
                jedisUtil.setList(POINT_DISTANCE+"_"+taskId,recordList);
            }else {
                jedisUtil.del(POINT_DISTANCE+"_"+taskId);
                logger.info("[calculateDistance],线程:{},set集合为空这个围栏区域没用目标",Thread.currentThread().getName());
            }
        }else {
            logger.info("[calculateDistance],线程:{},未定义任务id不执行距离计算",Thread.currentThread().getName());
        }

    }
}
