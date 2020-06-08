package com.lijiajie.mapsystem.trajectory.producer;

import com.lijiajie.mapsystem.trajectory.pojo.LngLat;
import com.lijiajie.mapsystem.trajectory.pojo.LocationInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * @describe 为前端web推送构造字符串推送
 */
@Component
public class LngLatProducer {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void syncSend(LocationInfo lngLat) {
        // 同步发送消息
        StringBuffer sendMessage = new StringBuffer();
        sendMessage.append(lngLat.getName());
        sendMessage.append("-");
        sendMessage.append(lngLat.getUserId());
        sendMessage.append("-");
        sendMessage.append(lngLat.getLongitude());
        sendMessage.append("-");
        sendMessage.append(lngLat.getLatitude());
        rabbitTemplate.convertAndSend(LngLat.EXCHANGE, LngLat.ROUTING_KEY, sendMessage.toString());
    }

    @Async
    public ListenableFuture<Void> asyncSend(LocationInfo lngLat) {
        try {
            // 发送消息
            this.syncSend(lngLat);
            // 返回成功的 Future
            return AsyncResult.forValue(null);
        } catch (Throwable ex) {
            // 返回异常的 Future
            return AsyncResult.forExecutionException(ex);
        }
    }
}

