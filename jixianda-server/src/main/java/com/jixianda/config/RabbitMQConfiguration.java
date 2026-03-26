package com.jixianda.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/**
 * RabbitMQ 配置。
 * 这个类属于消息中间件配置层，负责定义订单创建队列、死信交换机和超时取消队列，
 * 让订单创建后能够自动进入延迟取消链路，支撑“先创建、后超时清理”的交易流程。
 */
public class RabbitMQConfiguration {

    // 订单延迟队列：订单创建成功后先把消息放在这里等待 TTL 到期。
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    // 订单死信交换机：延迟队列消息过期后会投递到这个交换机。
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";
    // 订单超时取消队列：死信交换机再把消息路由到这里，交给超时取消消费者处理。
    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout.queue";
    // 超时取消 routing key：用于把“过期订单消息”精准路由到超时取消队列。
    public static final String ORDER_TIMEOUT_ROUTING_KEY = "order.timeout";

    /**
     * 订单延迟队列。
     * 这个队列在拓扑中的位置是“延迟入口”：
     * 普通订单创建链路在其他配置类里把创建消息投到业务交换机后，消费者会再次投递到本队列，
     * 消息在本队列等待 TTL 到期后，才通过死信机制进入超时取消链路。
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(ORDER_DELAY_QUEUE)
                // TTL 60 秒：在业务上表示“支付超时时间窗口”。
                .withArgument("x-message-ttl", 60000)
                // 指定死信交换机：消息过期后不丢弃，而是继续路由到取消链路。
                .withArgument("x-dead-letter-exchange", ORDER_DLX_EXCHANGE)
                // 指定死信 routing key：控制过期消息进入哪条死信消费通道。
                .withArgument("x-dead-letter-routing-key", ORDER_TIMEOUT_ROUTING_KEY)
                .build();
    }

    /**
     * 订单死信交换机。
     * 这个交换机是“延迟取消中转站”：
     * 延迟队列过期消息统一先到这里，再按 routing key 分发到对应补偿队列。
     */
    @Bean
    public DirectExchange orderDlxExchange() {
        return new DirectExchange(ORDER_DLX_EXCHANGE, true, false);
    }

    /**
     * 订单超时取消队列。
     * 这个队列在拓扑中的位置是“补偿执行入口”：
     * 只接收延迟过期后的订单消息，由 `OrderTimeoutCancelConsumer` 做状态检查和取消补偿。
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(ORDER_TIMEOUT_QUEUE).build();
    }

    /**
     * 超时取消队列绑定。
     * 这条绑定定义了“order.timeout”这条路由规则：
     * 死信交换机收到过期订单消息后，只有匹配这个 routing key 才会进入超时取消队列。
     */
    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder.bind(orderTimeoutQueue()).to(orderDlxExchange()).with(ORDER_TIMEOUT_ROUTING_KEY);
    }
}
