package com.yuanstack.sylvanmq.client;

import com.yuanstack.sylvanmq.model.Message;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * message consumer
 *
 * @author Sylvan
 * @date 2024/07/14  16:57
 */
public class Consumer<T> {

    private final String id;
    Broker broker;
    @Getter
    private MessageListener listener;
    private static final AtomicInteger idgen = new AtomicInteger(1);

    public Consumer(Broker broker) {
        this.broker = broker;
        this.id = "CID" + idgen.getAndIncrement();
    }

    public void subscribe(String topic) {
        System.out.println("consumer " + id + " subscribe " + topic);
        broker.subscribe(topic, id);
    }

    public void unsubscribe(String topic) {
        broker.unsubscribe(topic, id);
    }

    public Message<T> receive(String topic) {
        return broker.receive(topic, id);
    }

    public boolean ack(String topic, int offset) {
        return broker.ack(topic, id, offset);
    }

    public boolean ack(String topic, Message<?> message) {
        int offset = Integer.parseInt(message.getHeaders().get("X-offset"));
        return ack(topic, offset);
    }


    public void listen(String topic, MessageListener<T> listener) {
        this.listener = listener;
        broker.addConsumer(topic, this);
    }
}
