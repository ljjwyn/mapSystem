package com.lijiajie.mapsystem.trajectory.rabbitMq.consumer;

import com.lijiajie.mapsystem.trajectory.pojo.SimulationLocation;
import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @describe 获取rabbitmq对象的监听器接口。这个接口主要将完整的坐标类相关信息做数据持久化
 * 目前用来接受模拟的坐标，由于模拟坐标并发，前端单目标实时展示以及地理围栏系统需要重新筛选
 * 目标id并转发。
 */
@Component
@RabbitListener(queues = SimulationLocation.QUEUE)
@Data
//@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SimulationLocConsumer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String KEY_NAME="currentPoint";

    private String INSIDE_USER_SET="insideUserSet";

    @Autowired
    JedisUtil jedisUtil;

    @RabbitHandler
    public void onMessage(SimulationLocation locationInfo) {
        Map<String,Object> recordRedisLoc = new HashMap<>();
        recordRedisLoc.put("locationName",locationInfo.getLocName());
        recordRedisLoc.put("userId",locationInfo.getUserId());
        recordRedisLoc.put("lng",locationInfo.getLng());
        recordRedisLoc.put("lat",locationInfo.getLat());
        if(!jedisUtil.sismember(INSIDE_USER_SET,locationInfo.getUserId())){
            jedisUtil.sadd(INSIDE_USER_SET,locationInfo.getUserId());
        }
        jedisUtil.setMap(KEY_NAME+"_"+locationInfo.getUserId(), recordRedisLoc);
        logger.info("[onMessage][线程编号:{},收到目标：{}的坐标]", Thread.currentThread().getId(),locationInfo.getUserId());
    }
}
