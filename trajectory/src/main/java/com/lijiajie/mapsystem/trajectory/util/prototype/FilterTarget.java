package com.lijiajie.mapsystem.trajectory.util.prototype;

import com.lijiajie.mapsystem.trajectory.config.RabbitConfig;
import com.lijiajie.mapsystem.trajectory.rabbitMq.producer.WebCurrentProducer;
import com.lijiajie.mapsystem.trajectory.service.FencingServiceImpl;
import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FilterTarget {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String INSIDE_USER_SET="insideUserSet";

    private final String KEY_NAME="currentPoint";

    private final String QUEUE = "QUEUE_WCS_01";

    private final String EXCHANGE = "EXCHANGE_WCS_01";

    private final String ROUTING_KEY = "ROUTING_WCS_01";

    @Autowired
    FencingServiceImpl fencingService;

    @Autowired
    JedisUtil jedisUtil;

    @Autowired
    WebCurrentProducer webCurrentProducer;

    @Autowired
    RabbitConfig rabbitConfig;

    @Autowired
    RabbitAdmin rabbitAdmin;

    @Autowired
    SchedulePoints schedulePoints;

    public void filterByFencing(Integer taskId, int fencingId){
        List<Map<String,Double>> fencingList = jedisUtil.getList("fencing_"+fencingId);
        List<String> userIdList = new ArrayList<>(jedisUtil.smembers(INSIDE_USER_SET));
        if(fencingList==null){
            logger.info("[filterByFencing][线程编号:{},未得到区划围栏，不做处理]", Thread.currentThread().getId());
        }else {
            List<Point2D.Double> fencingTransList = new ArrayList<>();
            for (Map<String,Double> fencingPoint:fencingList){
                fencingTransList.add(new Point2D.Double(fencingPoint.get("lng"),fencingPoint.get("lat")));
            }
            for(String userId:userIdList) {
                Map<String, Object> targetLoc = jedisUtil.getMap(KEY_NAME + "_" + userId);
                Point2D.Double targetPoint = new Point2D.Double((Double) targetLoc.get("lng"), (Double) targetLoc.get("lat"));
                boolean isFencing = fencingService.isInPolygon(targetPoint, fencingTransList);
                if (isFencing) {
                    if (!jedisUtil.sismember(INSIDE_USER_SET + "_" + taskId, (String) targetLoc.get("userId"))) {
                        jedisUtil.sadd(INSIDE_USER_SET + "_" + taskId, (String) targetLoc.get("userId"));
                    }
                } else {
                    if (jedisUtil.sismember(INSIDE_USER_SET + "_" + taskId, (String) targetLoc.get("userId"))) {
                        jedisUtil.srem(INSIDE_USER_SET + "_" + taskId, (String) targetLoc.get("userId"));
                        logger.info("[filterByFencing][线程编号:{},离开区划围栏]", Thread.currentThread().getId());
                    } else {
                        logger.info("[filterByFencing][线程编号:{},不在区划围栏中，不做处理]", Thread.currentThread().getId());
                    }
                }
            }
        }
    }

    public void sendTargetPoint(String targetId,int taskId){
        List<String> userIdList = new ArrayList<>(jedisUtil.smembers(INSIDE_USER_SET));
        for(String userId:userIdList) {
            if(targetId.equals(userId)){
                createMQ(QUEUE+"_"+taskId+"_"+targetId,EXCHANGE+"_"+taskId+"_"+targetId,ROUTING_KEY+"_"+taskId+"_"+targetId);
                schedulePoints.setTaskId(taskId);
                schedulePoints.setUserId(targetId);
                schedulePoints.setIsStart("true");
            }
        }
    }

    public void createMQ(String queueName,String exchangeName,String routingName){
        rabbitAdmin.declareQueue(new Queue(queueName, false));
        rabbitAdmin.declareExchange(new DirectExchange(exchangeName, false, false));
        rabbitAdmin.declareBinding(new Binding(queueName,
                Binding.DestinationType.QUEUE, exchangeName, routingName, new HashMap<>()));
    }

}
