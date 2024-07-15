package com.yuanstack.sylvanmq.server;

import com.yuanstack.sylvanmq.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sylvan
 * @date 2024/07/14  22:58
 */
public class MessageQueue {

    public static final Map<String, MessageQueue> queues = new HashMap<>();
    private static final String TEST_TOPIC = "com.yuanstack.test";

    static {
        queues.put(TEST_TOPIC, new MessageQueue(TEST_TOPIC));
        queues.put("a", new MessageQueue("a"));
    }

    private Map<String, MessageSubscription> subscriptions = new HashMap<>();
    private String topic;
    private Message<?>[] queue = new Message[1024 * 10];
    private int index = 0;

    public MessageQueue(String topic) {
        this.topic = topic;
    }

    public int send(Message<?> message) {
        if (index >= queue.length) {
            return -1;
        }
        message.getHeaders().put("X-offset", String.valueOf(index));
        queue[index++] = message;
        return index;
    }

    public static List<Message<?>> batch(String topic, String consumerId, int size) {
        MessageQueue messageQueue = queues.get(topic);
        if (messageQueue == null) throw new RuntimeException("topic not found");
        if (messageQueue.subscriptions.containsKey(consumerId)) {
            int ind = messageQueue.subscriptions.get(consumerId).getOffset();
            int offset = ind + 1;
            List<Message<?>> result = new ArrayList<>();
            Message<?> receive = messageQueue.receive(offset);
            while (receive != null) {
                result.add(receive);
                if (result.size() >= size) break;
                receive = messageQueue.receive(++offset);
            }
            System.out.println(" ===>> batch: topic/cid/size = " + topic + "/" + consumerId + "/" + result.size());
            System.out.println(" ===>> last message: " + receive);
        }
        throw new RuntimeException("subscriptions not found for topic/consumerId = "
                + topic + "/" + consumerId);
    }

    public Message<?> receive(int ind) {
        if (ind <= index) return queue[ind];
        return null;
    }

    public void subscribe(MessageSubscription subscription) {
        String consumerId = subscription.getConsumerId();
        subscriptions.putIfAbsent(consumerId, subscription);
    }

    public void unsubscribe(MessageSubscription subscription) {
        String consumerId = subscription.getConsumerId();
        subscriptions.remove(consumerId);
    }

    public static void sub(MessageSubscription subscription) {
        MessageQueue messageQueue = queues.get(subscription.getTopic());
        System.out.println(" ==>> sub: " + subscription);
        if (messageQueue == null) throw new RuntimeException("topic not found");
        messageQueue.subscribe(subscription);
    }

    public static void unsub(MessageSubscription subscription) {
        MessageQueue messageQueue = queues.get(subscription.getTopic());
        System.out.println(" ==>> unsub: " + subscription);
        if (messageQueue == null) return;
        messageQueue.unsubscribe(subscription);
    }

    public static int send(String topic, Message<String> message) {
        MessageQueue messageQueue = queues.get(topic);
        System.out.println(" ==>> send: topic/message = " + topic + "/" + message);
        if (messageQueue == null) throw new RuntimeException("topic not found");
        return messageQueue.send(message);
    }

    public static Message<?> receive(String topic, String consumerId, int ind) {
        MessageQueue messageQueue = queues.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        if (!messageQueue.subscriptions.containsKey(consumerId)) {
            throw new RuntimeException("subscriptions not found for topic/consumerId = "
                    + topic + "/" + consumerId);
        }
        return messageQueue.receive(ind);
    }

    // 使用此方法，需要手工调用ack，更新订阅关系里的offset
    public static Message<?> receive(String topic, String consumerId) {
        MessageQueue messageQueue = queues.get(topic);
        if (messageQueue == null) {
            System.out.println("topic not found");
            throw new RuntimeException("topic not found");
        }
        if (!messageQueue.subscriptions.containsKey(consumerId)) {
            System.out.println("subscriptions not found for topic/consumerId = "
                    + topic + "/" + consumerId);
            throw new RuntimeException("subscriptions not found for topic/consumerId = "
                    + topic + "/" + consumerId);
        }

        int ind = messageQueue.subscriptions.get(consumerId).getOffset();
        Message<?> receive = messageQueue.receive(ind + 1);
        System.out.println(" ===>> receive: topic/cid/ind = " + topic + "/" + consumerId + "/" + ind);
        System.out.println(" ===>> message: " + receive);
        return receive;
    }

    public static int ack(String topic, String consumerId, int offset) {
        MessageQueue messageQueue = queues.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        if (!messageQueue.subscriptions.containsKey(consumerId)) {
            throw new RuntimeException("subscriptions not found for topic/consumerId = "
                    + topic + "/" + consumerId);
        }

        MessageSubscription subscription = messageQueue.subscriptions.get(consumerId);
        if (offset > subscription.getOffset() && offset <= messageQueue.index) {
            System.out.println(" ===>> ack: topic/cid/offset = "
                    + topic + "/" + consumerId + "/" + offset);
            subscription.setOffset(offset);
            return offset;
        }
        return -1;
    }
}