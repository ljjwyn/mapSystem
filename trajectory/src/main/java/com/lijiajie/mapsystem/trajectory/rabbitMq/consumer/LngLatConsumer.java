package com.lijiajie.mapsystem.trajectory.rabbitMq.consumer;



import com.lijiajie.mapsystem.trajectory.pojo.LngLat;
import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @describe 获取rabbitmq String类型的监听接口，主要是测试前端web socket订阅消息队列不能
 * 拿到对象类型，因此使用了拼接字符串。
 */
@Component
@RabbitListener(queues = LngLat.QUEUE)
public class LngLatConsumer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String KEY_NAME="currentPoint";

    @Autowired
    JedisUtil jedisUtil;

    @RabbitHandler
    public void onMessage(String message) {
        Map<String,Object> recordRedisLoc = new HashMap<>();
        String[] messageList = message.split("-");
        recordRedisLoc.put("userName",messageList[0]);
        recordRedisLoc.put("userId",messageList[1]);
        recordRedisLoc.put("lng",Double.valueOf(messageList[2]));
        recordRedisLoc.put("lat",Double.valueOf(messageList[3]));
        jedisUtil.setMap(KEY_NAME+"_"+messageList[1], recordRedisLoc);
        logger.info("[onMessage][LngAndLat][线程编号:{} message:{}]", Thread.currentThread().getId(), message);
    }
}
