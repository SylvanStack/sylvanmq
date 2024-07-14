package com.yuanstack.sylvanmq.core;

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

}
