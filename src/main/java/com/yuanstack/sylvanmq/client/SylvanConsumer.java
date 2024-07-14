package com.yuanstack.sylvanmq.client;

import com.yuanstack.sylvanmq.model.SylvanMessage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * message consumer
 *
 * @author Sylvan
 * @date 2024/07/14  16:57
 */
public class SylvanConsumer<T> {

    private String id;
    SylvanBroker sylvanBroker;
    String topic;
    SylvanMQ mq;
    static AtomicInteger idgen = new AtomicInteger(0);

    public SylvanConsumer(SylvanBroker sylvanBroker) {
        this.sylvanBroker = sylvanBroker;
        this.id = "CID" + idgen.getAndIncrement();
    }

    public void subscribe(String topic) {
        this.topic = topic;
        mq = sylvanBroker.findTopic(topic);
        if (mq == null) throw new RuntimeException("topic not found");
    }

    public SylvanMessage<T> poll(long timeout) {
        return mq.poll(timeout);
    }

    public void listen(SylvanMessageListener<T> listener) {
        mq.addListener(listener);
    }
}
