package com.smlyk.simple;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author yekai
 */
public class MyConsumer2 {

    private final static String EXCHANGE_NAME = "TOPIC_EXCHANGE";

    private final static String QUEUE_NAME = "TOPIC_QUEUE";

    private final static String ROUTING_KEY = "TOPIC_ROUTING_KEY.#";

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


        /**
         * 声明交换机
         * String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete,Map<String, Object> arguments
         * String type：交换机的类型，direct, topic, fanout 中的一种。
         * boolean durable：是否持久化，代表交换机在服务器重启后是否还存在。
         */
        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true, false, null);

        /**
         * 声明队列
         * String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments
         * boolean durable：是否持久化，代表队列在服务器重启后是否还存在。
         * boolean exclusive：是否排他性队列。排他性队列只能在声明它的 Connection中使用（可以在同一个 Connection 的不同的 channel 中使用），连接断开时自动删除。
         * boolean autoDelete：是否自动删除。如果为 true，至少有一个消费者连接到这个队列，之后所有与这个队列连接的消费者都断开时，队列会自动删除。
         * Map<String, Object> arguments：队列的其他属性，例如：
                 x-message-ttl 队列中消息的存活时间，单位毫秒
                 x-expires 队列在多久没有消费者访问以后会被删除
                 x-max-length 队列的最大消息数
                 x-max-length-bytes 队列的最大容量，单位 Byte
                 x-dead-letter-exchange 队列的死信交换机
                 x-dead-letter-routing-key 死信交换机的路由键
                x-max-priority 队列中消息的最大优先级，消息的优先级不能超过它
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        //绑定队列和交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        System.out.println("Waiting for message ...");

        //创建消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String msg = new String(body, "utf-8");
                System.out.println("Received message : '" + msg + "'");
                System.out.println("consumerTag : " + consumerTag );
                System.out.println("deliveryTag : " + envelope.getDeliveryTag() );
            }
        };

        //开始获取消息
        //String queue, boolean autoAck, Consumer callback
        channel.basicConsume(QUEUE_NAME, true, consumer);

    }

}
