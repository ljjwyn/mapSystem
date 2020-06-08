package com.lijiajie.mapsystem.trajectory;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class RabbitTest {
    /****    RabbitAdmin 操作    ****/

    @Autowired
    private RabbitAdmin rabbitAdmin;

    /**
     * 交换机操作
     */
    @Test
    public void testAdminExchange() {
        // 创建交换机, 类型为 direct
        // durable 参数表示是否持久化
        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));

    }


    /**
     * 队列操作
     */
    @Test
    public void testAdminQueue() {
        // 创建队列
        // durable 参数表示是否持久化
        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
        // 创建队列
        // durable 参数表示是否持久化
        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
        // 创建队列
        // durable 参数表示是否持久化
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));

    }

    /**
     * 绑定操作
     */
    @Test
    public void testAdminBinding() {
        /**
         * 两种写法都可以，都选择绑定 队列 或者 交换机
         */


        /**
         * destination 需要绑定队列的名字
         * DestinationType 绑定类型，
         *          Binding.DestinationType.QUEUE 表示是队列绑定
         *          Binding.DestinationType.EXCHANGE 表示交换机绑定
         *
         * exchange 交换机名称
         * routingKey 路由key
         * arguments 额外参数（比如绑定队列，可以设置 死信队列的参数）
         */
        rabbitAdmin.declareBinding(new Binding("test.direct.queue",
                Binding.DestinationType.QUEUE, "test.direct", "routing_direct", new HashMap<>()));



    }


    /**
     * 其他操作
     */
    @Test
    public void testAdminOther() {

        // 清空队列
        // noWait 参数是否需要等待: true 表示需要，false 表示不需要
        //      也就是需要清空的时候，我需要等待一下，在清空(会自动等待几秒钟)
        rabbitAdmin.purgeQueue("test.fanout.queue", false);

    }

}
