package com.yuanstack.sylvanmq.server;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Sylvan
 * @date 2024/07/14  22:58
 */
@Data
@AllArgsConstructor
public class MessageSubscription {

    private String topic;
    private String consumerId;
    private int offset = -1;
}

