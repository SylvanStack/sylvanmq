package com.yuanstack.sylvanmq.demo;

import com.yuanstack.sylvanmq.core.SylvanBroker;
import com.yuanstack.sylvanmq.core.SylvanConsumer;
import com.yuanstack.sylvanmq.core.SylvanMessage;
import com.yuanstack.sylvanmq.core.SylvanProducer;

/**
 * @author Sylvan
 * @date 2024/07/14  17:20
 */
public class SylvanMqDemo {
    public static void main(String[] args) {
        String topic = "sylvan.order";
        SylvanBroker broker = new SylvanBroker();
        broker.createTopic(topic);

        SylvanProducer producer = new SylvanProducer(broker);
        SylvanConsumer<Order> consumer = new SylvanConsumer<>(broker);

        consumer.subscribe(topic);

        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                Order order = new Order(String.valueOf(i), "apple", 1.0);
                System.out.println("生产消息：" + order);
                producer.send(topic, new SylvanMessage<>((long) i, order, null));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                SylvanMessage<Order> orderSylvanMessage = consumer.poll(1000);
                if (orderSylvanMessage != null) {
                    System.out.println("消费消息：" + orderSylvanMessage.getBody());
                }
            }
        }).start();


    }
}
