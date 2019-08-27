package com.smlyk.simple;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author yekai
 */
public class MyProducer3 {

    private final static String EXCHANGE_NAME = "FANOUT_EXCHANGE";



    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        //连接IP
        factory.setHost("47.101.129.30");
        //连接端口
        factory.setPort(5672);
        //虚拟机
        factory.setVirtualHost("/");
        //用户
        factory.setUsername("admin");
        factory.setPassword("admin");

        //建立连接
        Connection connection = factory.newConnection();
        //创建消息信道
        Channel channel = connection.createChannel();

        //发送消息
        String msg = "Hello World, RabbitMQ";
        /**
         * 消息属性 BasicProperties
              Map<String,Object> headers 消息的其他自定义参数
             Integer deliveryMode 2 持久化，其他：瞬态
             Integer priority 消息的优先级
             String correlationId 关联 ID，方便 RPC 相应与请求关联
             String replyTo 回调队列
             String expiration TTL，消息过期时间，单位毫秒
         */
        AMQP.BasicProperties properties = new AMQP.BasicProperties
                .Builder()
                .contentType("application/json")
                .expiration("100")
                .build();
        channel.basicPublish(EXCHANGE_NAME, "", properties, msg.getBytes());

        channel.close();
        connection.close();
    }


}
