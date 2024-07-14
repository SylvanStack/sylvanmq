package com.yuanstack.sylvanmq.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * topics 管理
 *
 * @author Sylvan
 * @date 2024/07/14  16:58
 */
public class SylvanBroker {
    Map<String, SylvanMQ> mqMapping = new ConcurrentHashMap<>(64);

    public SylvanMQ findTopic(String topic) {
        return mqMapping.get(topic);
    }

    public SylvanMQ createTopic(String topic) {
        return mqMapping.putIfAbsent(topic, new SylvanMQ(topic));
    }

    public SylvanProducer createProducer() {
        return new SylvanProducer(this);
    }

    public SylvanConsumer<?> createConsumer(String topic) {
        SylvanConsumer<?> sylvanConsumer = new SylvanConsumer<>(this);
        sylvanConsumer.subscribe(topic);
        return sylvanConsumer;
    }

}
