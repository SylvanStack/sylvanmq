package com.yuanstack.sylvanmq.core;

/**
 * message consumer
 *
 * @author Sylvan
 * @date 2024/07/14  16:57
 */
public class SylvanConsumer<T> {
    SylvanBroker sylvanBroker;
    String topic;
    SylvanMQ mq;

    public SylvanConsumer(SylvanBroker sylvanBroker) {
        this.sylvanBroker = sylvanBroker;
    }

    public void subscribe(String topic) {
        this.topic = topic;
        mq = sylvanBroker.findTopic(topic);
        if (mq == null) throw new RuntimeException("topic not found");
    }

    public SylvanMessage<T> poll(long timeout) {
        return mq.poll(timeout);
    }
}
