package com.fzu.crowdsense.constant;

/**
 * <p>
 * RabbitMQ常量
 * <p>
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-03-21
 **/
public interface RabbitConstants {
    /**
     * user模块交换机名称
     */
    String CROWD_SENSE_EXCHANGE = "crowd.sense.topic.exchange";

    /**
     * Message 服务 Canal监听模块
     */
    String MESSAGE_CANAL_QUEUE = "message.canal.queue";


    /**
     * routingKey: Canal传输的数据
     */
    String ROUTING_CANAL_DATA = "canal.data";



}
