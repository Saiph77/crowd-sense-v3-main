package com.fzu.crowdsense.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fzu.crowdsense.constant.RabbitConstants.*;


/**
 * <p>
 * RabbitMQ配置类-TopicExchange模式
 * <p>
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-03-21
 **/
@Configuration
public class RabbitTopicConfigurer {


    /**
     * 配置交换机-TopicExchange
     *
     * @return crowdSenseTopicExchange
     */
    @Bean("crowdSenseTopicExchange")
    public TopicExchange ppengTopicExchange() {

        return ExchangeBuilder.topicExchange(CROWD_SENSE_EXCHANGE).durable(true).build();
    }


    /**
     * Canal监听队列
     *
     * @return UserCanalQueue
     */
    @Bean("messageCanalQueue")
    public Queue userCanalQueue() {

        return QueueBuilder.durable(MESSAGE_CANAL_QUEUE).build();
    }


    /**
     * 队列和交换机绑定关系
     *
     * @param queue    队列
     * @param exchange 交换机
     * @return getBinding
     */
    @Bean
    public Binding userCacheUpdateQueueBinding(@Qualifier("messageCanalQueue") Queue queue, @Qualifier("crowdSenseTopicExchange") TopicExchange exchange) {

        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_CANAL_DATA);
    }


}
