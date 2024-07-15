package com.yuanstack.sylvanmq.client;

import com.yuanstack.sylvanmq.model.Message;
import lombok.AllArgsConstructor;

/**
 * @author Sylvan
 * @date 2024/07/14  16:57
 */
@AllArgsConstructor
public class Producer {
    private Broker broker;

    public void send(String topic, Message<?> message) {
        broker.send(topic, message);
    }
}
