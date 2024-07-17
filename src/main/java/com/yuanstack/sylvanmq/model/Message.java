package com.yuanstack.sylvanmq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Sylvan Message Model
 *
 * @author Sylvan
 * @date 2024/07/14  16:50
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message<T> implements Serializable {

    //private String topic;
    static AtomicLong idgen = new AtomicLong(0);
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
    private Map<String, String> headers = new HashMap<>();

    public static long nextId() {
        return idgen.getAndIncrement();
    }

    public static Message<String> create(String body, Map<String, String> headers) {
        return new Message<>(nextId(), body, headers);
    }
    ///**
    // * 消息标签
    // */
    //private Map<String, String> properties;
}
