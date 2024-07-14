package com.yuanstack.sylvanmq.core;

/**
 * @author Sylvan
 * @date 2024/07/14  16:57
 */
public class SylvanProducer {
    SylvanBroker broker;

    public boolean send(String topic, SylvanMessage sylvanMessage) {
        SylvanMQ sylvanMQ = broker.findTopic(topic);
        if (sylvanMQ == null) {
            throw new RuntimeException("Topic not found");
        }
        return sylvanMQ.send(sylvanMessage);
    }
}
