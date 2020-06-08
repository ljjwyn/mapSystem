package com.lijiajie.mapsystem.trajectory.rabbitMq.config;


import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Create by hadoop
 */
@Component
public class ConnectionUtil {

    /**
     * 创建 RabbitMQ 连接工厂
     * @return
     */
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        // rabbitmq 服务地址
        connectionFactory.setAddresses("127.0.0.1:5672");
        // 用户名
        connectionFactory.setUsername("root");
        // 密码
        connectionFactory.setPassword("root");
        // 虚拟机路径
        connectionFactory.setVirtualHost("/");

        return connectionFactory;
    }

    /**
     * 创建 RabbitAdmin 类，这个类封装了对 RabbitMQ 管理端的操作！
     *
     *    比如：Exchange 操作，Queue 操作，Binding 绑定 等
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 只有设置为 true，spring 才会加载 RabbitAdmin 这个类
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }



}