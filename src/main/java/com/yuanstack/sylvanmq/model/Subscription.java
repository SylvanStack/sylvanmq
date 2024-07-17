package com.yuanstack.sylvanmq.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Sylvan
 * @date 2024/07/14  22:58
 */
@Data
@AllArgsConstructor
public class Subscription {
    private String topic;
    private String consumerId;
    private int offset = -1;
}

