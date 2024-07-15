//package com.yuanstack.sylvanmq.client;
//
//import com.yuanstack.sylvanmq.model.Message;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.SneakyThrows;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author Sylvan
// * @date 2024/07/14  17:00
// */
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class MQ {
//    private String topic;
//    private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>(1024);
//    private List<MessageListener> listeners = new ArrayList<>();
//
//    public MQ(String topic) {
//        this.topic = topic;
//    }
//
//    public boolean send(Message sylvanMessage) {
//        boolean offered = queue.offer(sylvanMessage);
//        listeners.forEach(listener -> listener.onMessage(sylvanMessage));
//        return offered;
//    }
//
//    /**
//     * 拉模式获取消息
//     */
//    @SneakyThrows
//    public <T> Message<T> poll(long timeout) {
//        return queue.poll(timeout, TimeUnit.MILLISECONDS);
//    }
//
//
//    public <T> void addListener(MessageListener<T> listener) {
//        listeners.add(listener);
//    }
//}
