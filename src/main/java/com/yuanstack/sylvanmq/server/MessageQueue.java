package com.yuanstack.sylvanmq.server;

import com.yuanstack.sylvanmq.model.SylvanMessage;

import java.util.HashMap;
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
    }

    private Map<String, MessageSubscription> subscriptions = new HashMap<>();
    private String topic;
    private SylvanMessage<?>[] queue = new SylvanMessage[1024 * 10];
    private int index = 0;

    public MessageQueue(String topic) {
        this.topic = topic;
    }

    public int send(SylvanMessage<?> message) {
        if (index >= queue.length) {
            return -1;
        }
        queue[index++] = message;
        return index;
    }

    public SylvanMessage<?> receive(int ind) {
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
        if (messageQueue == null) throw new RuntimeException("topic not found");
        messageQueue.subscribe(subscription);
    }

    public static void unsub(MessageSubscription subscription) {
        MessageQueue messageQueue = queues.get(subscription.getTopic());
        if (messageQueue == null) return;
        messageQueue.unsubscribe(subscription);
    }

    public static int send(String topic, String consumerId, SylvanMessage<String> message) {
        MessageQueue messageQueue = queues.get(topic);
        if (messageQueue == null) throw new RuntimeException("topic not found");
        return messageQueue.send(message);
    }

    public static SylvanMessage<?> receive(String topic, String consumerId, int ind) {
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
    public static SylvanMessage<?> receive(String topic, String consumerId) {
        MessageQueue messageQueue = queues.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        if (!messageQueue.subscriptions.containsKey(consumerId)) {
            throw new RuntimeException("subscriptions not found for topic/consumerId = "
                    + topic + "/" + consumerId);
        }

        int ind = messageQueue.subscriptions.get(consumerId).getOffset();
        return messageQueue.receive(ind);
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
            subscription.setOffset(offset);
            return offset;
        }
        return -1;
    }
}