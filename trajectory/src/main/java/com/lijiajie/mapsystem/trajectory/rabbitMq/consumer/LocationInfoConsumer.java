package com.lijiajie.mapsystem.trajectory.rabbitMq.consumer;

import com.lijiajie.mapsystem.trajectory.mapper.LocationInfoMapper;
import com.lijiajie.mapsystem.trajectory.pojo.LocationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;


/**
 * @describe 获取rabbitmq对象的监听器接口。这个接口主要将完整的坐标类相关信息做数据持久化
 */
@Component
@RabbitListener(queues = LocationInfo.QUEUE)
public class LocationInfoConsumer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    LocationInfoMapper locationInfoMapper;

    @RabbitHandler
    public void onMessage(LocationInfo locationInfo) {
        logger.info("[onMessage][线程编号:{} id:{} lng：{}, lat:{}]", Thread.currentThread().getId(), locationInfo.getName(), locationInfo.getLongitude(), locationInfo.getLatitude());
        try {
            locationInfoMapper.createALocRecord(locationInfo);
        } catch (SQLException e) {
            logger.info("插入数据库异常,UserInfo：[{}] 发送异常]]", locationInfo.getName(), e);
            e.printStackTrace();
        }
    }


}
