package com.example.ZhangDT.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter);
        
        // 开启强制消息投递，配合 ReturnCallback 使用
        template.setMandatory(true);

        // 消息发送到 Exchange 确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                // logger.info("消息成功发送到交换机");
            } else {
                System.err.println("消息发送到交换机失败: " + cause);
            }
        });

        // 消息从未命中 Queue 的返回回调
        template.setReturnsCallback(returned -> {
            System.err.println("消息从交换机退回: " + returned.getReplyText() + 
                               ", 交换机: " + returned.getExchange() + 
                               ", 路由键: " + returned.getRoutingKey());
        });

        return template;
    }
} 