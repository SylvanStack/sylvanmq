package com.yuanstack.sylvanmq.demo;

import com.alibaba.fastjson.JSON;
import com.yuanstack.sylvanmq.client.Broker;
import com.yuanstack.sylvanmq.client.Consumer;
import com.yuanstack.sylvanmq.client.Producer;
import com.yuanstack.sylvanmq.model.Message;

/**
 * @author Sylvan
 * @date 2024/07/14  17:20
 */
public class MessageQueueDemo {
    private static final String TEST_TOPIC = "com.yuanstack.test";

    public static void main(String[] args) {
        long ids = 0;
        Broker broker = Broker.Default;
        Producer producer = broker.createProducer();
        Consumer<?> consumer = broker.createConsumer(TEST_TOPIC);
        consumer.listen(TEST_TOPIC, message -> {
            System.out.println("消费数据 => " + message); // 这里处理消息
        });

        for (int i = 0; i < 10; i++) {
            Order order = new Order(String.valueOf(i), "apple" + i, 1.0 + i);
            System.out.println("生产消息：" + order);
            Message<?> message = Message.create(JSON.toJSONString(order), null);
            producer.send(TEST_TOPIC, message);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("=====================================");
        //
        //for (int i = 0; i < 10; i++) {
        //    try {
        //        Message<?> orderSylvanMessage = consumer.receive(TEST_TOPIC);
        //        if (orderSylvanMessage != null) {
        //            System.out.println("消费消息：" + JSON.parseObject(String.valueOf(orderSylvanMessage.getBody()), Order.class));
        //            consumer.ack(TEST_TOPIC, orderSylvanMessage);
        //        }
        //        Thread.sleep(1000);
        //    } catch (Exception ex) {
        //        ex.printStackTrace();
        //    }
        //}

        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                Order order = new Order(String.valueOf(i), "apple" + i, 1.0 + i);
                System.out.println("生产消息：" + order);
                Message<?> message = Message.create(JSON.toJSONString(order), null);
                producer.send(TEST_TOPIC, message);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //new Thread(() -> {
        //    while (true) {
        //        try {
        //            Message<?> orderSylvanMessage = consumer.receive(TEST_TOPIC);
        //            if (orderSylvanMessage != null) {
        //                System.out.println("消费消息：" + JSON.parseObject(String.valueOf(orderSylvanMessage.getBody()), Order.class));
        //                consumer.ack(TEST_TOPIC, orderSylvanMessage);
        //            }
        //            Thread.sleep(1000);
        //        } catch (Exception ex) {
        //            ex.printStackTrace();
        //        }
        //    }
        //}).start();
    }
}
