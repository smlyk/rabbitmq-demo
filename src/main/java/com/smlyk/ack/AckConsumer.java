package com.smlyk.ack;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author yekai
 */
public class AckConsumer {

    private final static String QUEUE_NAME = "ACK_QUEUE";
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

        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);

        //声明队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        //绑定队列和交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME,ROUTING_KEY);
        System.out.println(" Waiting for message....");

        //创建消费者，接收消息
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String msg = new String(body, "UTF-8");
                System.out.println("Received message : '" + msg + "'");

                if (msg.contains("拒收")){
                    // 拒绝消息
                    // requeue：是否重新入队列，true：是；false：直接丢弃，相当于告诉队列可以直接删除掉
                    //如果只有这一个消费者，requeue 为true 的时候会造成消息重复消费
                    channel.basicReject(envelope.getDeliveryTag(), false);
                } else if (msg.contains("异常")){
                    // 批量拒绝
                    // requeue：是否重新入队列
                    // 如果只有这一个消费者，requeue 为true 的时候会造成消息重复消费
//                    channel.basicNack(envelope.getDeliveryTag(), true, false);
                }else {
                    // 手工应答
                    // 如果不应答，队列中的消息会一直存在，重新连接的时候会重复消费
                    channel.basicAck(envelope.getDeliveryTag(), true);
                }

            }
        };


        // 开始获取消息，注意这里开启了手工应答
        // String queue, boolean autoAck, Consumer callback
        channel.basicConsume(QUEUE_NAME, false, consumer);

    }


}
