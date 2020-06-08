package com.lijiajie.mapsystem.trajectory.producer;


import com.lijiajie.mapsystem.trajectory.pojo.SimulationLocation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class SimulationLocProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void syncSend(SimulationLocation locationMessage) {
//        StringBuffer sendMessage = new StringBuffer();
//        sendMessage.append(locationMessage.getLocName());
//        sendMessage.append("-");
//        sendMessage.append(locationMessage.getUserId());
//        sendMessage.append("-");
//        sendMessage.append(locationMessage.getLng());
//        sendMessage.append("-");
//        sendMessage.append(locationMessage.getLat());
        // 同步发送消息
        rabbitTemplate.convertAndSend(SimulationLocation.EXCHANGE, SimulationLocation.ROUTING_KEY, locationMessage);
    }

    @Async
    public ListenableFuture<Void> asyncSend(SimulationLocation locationMessage) {
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
