package com.lijiajie.mapsystem.trajectory.config;

import com.lijiajie.mapsystem.trajectory.pojo.LngLat;
import com.lijiajie.mapsystem.trajectory.pojo.LocationInfo;
import com.lijiajie.mapsystem.trajectory.pojo.SimulationLocation;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LngLatRabbitConfig {
    /**
     * Direct Exchange 示例的配置类
     * 配置交换机路由为LngLat类中的相关配置
     * 这个队列为前端stomp推送
     */
    public static class DirectExchangeDemoConfiguration {

        // 创建 Queue
        @Bean
        public Queue demo01Queue() {
            return new Queue(LngLat.QUEUE, // Queue 名字
                    true, // durable: 是否持久化
                    false, // exclusive: 是否排它
                    false); // autoDelete: 是否自动删除
        }

        // 创建 Direct Exchange
        @Bean
        public DirectExchange demo01Exchange() {
            return new DirectExchange(LngLat.EXCHANGE,
                    true,  // durable: 是否持久化
                    false);  // exclusive: 是否排它
        }

        // 创建 Binding
        // Exchange：Demo01Message.EXCHANGE
        // Routing key：Demo01Message.ROUTING_KEY
        // Queue：Demo01Message.QUEUE
        @Bean
        public Binding demo01Binding() {
            return BindingBuilder.bind(demo01Queue()).to(demo01Exchange()).with(LngLat.ROUTING_KEY);
        }

    }
    /**
     * Direct Exchange 示例的配置类
     * 这个队列为对象推送。
     */
    public static class DirectExchangeDemoConfiguration2 {

        // 创建 Queue
        @Bean
        public Queue demo02Queue() {
            return new Queue(LocationInfo.QUEUE, // Queue 名字
                    true, // durable: 是否持久化
                    false, // exclusive: 是否排它
                    false); // autoDelete: 是否自动删除
        }

        // 创建 Direct Exchange
        @Bean
        public DirectExchange demo02Exchange() {
            return new DirectExchange(LocationInfo.EXCHANGE,
                    true,  // durable: 是否持久化
                    false);  // exclusive: 是否排它
        }

        // 创建 Binding
        // Exchange：Demo01Message.EXCHANGE
        // Routing key：Demo01Message.ROUTING_KEY
        // Queue：Demo01Message.QUEUE
        @Bean
        public Binding demo02Binding() {
            return BindingBuilder.bind(demo02Queue()).to(demo02Exchange()).with(LocationInfo.ROUTING_KEY);
        }

    }

    /**
     * Direct Exchange 示例的配置类
     * 这个队列为对象推送。
     */
    public static class DirectExchangeDemoConfiguration3 {

        // 创建 Queue
        @Bean
        public Queue demo03Queue() {
            return new Queue(SimulationLocation.QUEUE, // Queue 名字
                    true, // durable: 是否持久化
                    false, // exclusive: 是否排它
                    false); // autoDelete: 是否自动删除
        }

        // 创建 Direct Exchange
        @Bean
        public DirectExchange demo03Exchange() {
            return new DirectExchange(SimulationLocation.EXCHANGE,
                    true,  // durable: 是否持久化
                    false);  // exclusive: 是否排它
        }

        // 创建 Binding
        // Exchange：Demo01Message.EXCHANGE
        // Routing key：Demo01Message.ROUTING_KEY
        // Queue：Demo01Message.QUEUE
        @Bean
        public Binding demo03Binding() {
            return BindingBuilder.bind(demo03Queue()).to(demo03Exchange()).with(SimulationLocation.ROUTING_KEY);
        }

    }
}
