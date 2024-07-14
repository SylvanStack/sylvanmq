package com.yuanstack.sylvanmq.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Sylvan Message Model
 *
 * @author Sylvan
 * @date 2024/07/14  16:50
 */
@Data
@AllArgsConstructor
public class SylvanMessage<T> implements Serializable {

    //private String topic;
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 消息体
     */
    private T body;

    /**
     * 消息头 系统属性 消息属性 消息优先级、消息过期时间 X-version = 1.0
     */
    private Map<String, String> header;
    /**
     * 消息标签
     */
    //private Map<String, String> properties;
}
