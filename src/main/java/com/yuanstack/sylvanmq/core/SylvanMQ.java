package com.yuanstack.sylvanmq.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Sylvan
 * @date 2024/07/14  17:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SylvanMQ {
    private String topic;
    private LinkedBlockingQueue<SylvanMessage> queue = new LinkedBlockingQueue<>(1024);
    private List<SylvanMessageListener> listeners = new ArrayList<>();

    public SylvanMQ(String topic) {
        this.topic = topic;
    }

    public boolean send(SylvanMessage sylvanMessage) {
        boolean offered = queue.offer(sylvanMessage);
        listeners.forEach(listener -> listener.onMessage(sylvanMessage));
        return offered;
    }

    /**
     * 拉模式获取消息
     */
    @SneakyThrows
    public <T> SylvanMessage<T> poll(long timeout) {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }


    public <T> void addListener(SylvanMessageListener<T> listener) {
        listeners.add(listener);
    }
}
