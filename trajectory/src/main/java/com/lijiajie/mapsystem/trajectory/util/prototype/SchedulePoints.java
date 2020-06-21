package com.lijiajie.mapsystem.trajectory.util.prototype;


import com.lijiajie.mapsystem.trajectory.pojo.SimulationLocation;
import com.lijiajie.mapsystem.trajectory.rabbitMq.producer.WebCurrentProducer;
import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@Data
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SchedulePoints {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String KEY_NAME="currentPoint";

    private final String QUEUE = "QUEUE_WCS_01";

    private final String EXCHANGE = "EXCHANGE_WCS_01";

    private final String ROUTING_KEY = "ROUTING_WCS_01";

    @Value("false")
    private String isStart;

    private int taskId;

    private String userId;

    private String queneName;

    private String exchangeName;

    private String routingName;

    @Autowired
    WebCurrentProducer webCurrentProducer;

    @Autowired
    JedisUtil jedisUtil;

    @Scheduled(fixedRate = 5000,initialDelay = 2000)
    //@Async
    public void translatePoints() {
        if(!Boolean.parseBoolean(isStart)){
            logger.info("[translatePoints],线程：{},中断,taskId:{}",Thread.currentThread().getId(),taskId);
            return;
        }else {
            Map<String, Object> targetLoc = jedisUtil.getMap(KEY_NAME + "_" + userId);
            SimulationLocation simulationLocation=new SimulationLocation();
            simulationLocation.setUserId((String) targetLoc.get("userId"));
            simulationLocation.setLocName((String) targetLoc.get("locName"));
            simulationLocation.setLng((Double) targetLoc.get("lng"));
            simulationLocation.setLat((Double) targetLoc.get("lat"));
            webCurrentProducer.syncSend(simulationLocation, EXCHANGE+"_"+taskId+"_"+simulationLocation.getUserId(), ROUTING_KEY+"_"+taskId+"_"+simulationLocation.getUserId());
            logger.info("[web stomp][线程编号:{}, taskId:{}, userId:{}]", Thread.currentThread().getId(), taskId, targetLoc.get("userId"));
        }
    }
}
