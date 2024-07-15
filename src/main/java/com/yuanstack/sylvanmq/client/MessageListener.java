package com.yuanstack.sylvanmq.client;

import com.yuanstack.sylvanmq.model.Message;

/**
 * message listener.
 *
 * @author Sylvan
 * @date 2024/07/14  21:09
 */
public interface MessageListener<T> {
    void onMessage(Message<T> message);
}
