package com.yuanstack.sylvanmq.client;

import com.yuanstack.sylvanmq.model.SylvanMessage;

/**
 * message listener.
 *
 * @author Sylvan
 * @date 2024/07/14  21:09
 */
public interface SylvanMessageListener<T> {
    void onMessage(SylvanMessage<T> message);
}
