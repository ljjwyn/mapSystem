package com.lijiajie.mapsystem.trajectory.rabbitMq.producer;

import com.lijiajie.mapsystem.trajectory.pojo.SimulationLocation;
import com.lijiajie.mapsystem.trajectory.pojo.WebCurrentStomp;
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
public class WebCurrentProducer {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void syncSend(SimulationLocation lngLat,String exchange,String routing) {
        // 同步发送消息
        StringBuffer sendMessage = new StringBuffer();
        sendMessage.append(lngLat.getLocName());
        sendMessage.append("-");
        sendMessage.append(lngLat.getUserId());
        sendMessage.append("-");
        sendMessage.append(lngLat.getLng());
        sendMessage.append("-");
        sendMessage.append(lngLat.getLat());
        rabbitTemplate.convertAndSend(exchange, routing, sendMessage.toString());
    }

    @Async
    public ListenableFuture<Void> asyncSend(SimulationLocation lngLat, String exchange,String routing) {
        try {
            // 发送消息
            this.syncSend(lngLat, exchange, routing);
            // 返回成功的 Future
            return AsyncResult.forValue(null);
        } catch (Throwable ex) {
            // 返回异常的 Future
            return AsyncResult.forExecutionException(ex);
        }
    }
}
