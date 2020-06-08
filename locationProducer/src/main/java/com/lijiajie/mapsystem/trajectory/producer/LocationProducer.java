package com.lijiajie.mapsystem.trajectory.producer;

import com.lijiajie.mapsystem.trajectory.pojo.LocationInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;


/**
 * @describe 推送对象类型
 */
@Component
public class LocationProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void syncSend(LocationInfo locationMessage) {
        // 同步发送消息
        rabbitTemplate.convertAndSend(LocationInfo.EXCHANGE, LocationInfo.ROUTING_KEY, locationMessage);
    }

    @Async
    public ListenableFuture<Void> asyncSend(LocationInfo locationMessage) {
        try {
            // 发送消息
            this.syncSend(locationMessage);
            // 返回成功的 Future
            return AsyncResult.forValue(null);
        } catch (Throwable ex) {
            // 返回异常的 Future
            return AsyncResult.forExecutionException(ex);
        }
    }
}
