package com.smlyk.ack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author yekai
 */
public class AckProducer {

    private final static String EXCHANGE_NAME = "ACK_EXCHANGE";
    private final static String ROUTING_KEY = "ACK_ROUTING_KEY";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.101.129.30");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        String msg = "test ack message异常";
        // 发送消息
        // String exchange, String routingKey, BasicProperties props, byte[] body
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null,msg.getBytes());

        channel.close();
        connection.close();

    }

}
