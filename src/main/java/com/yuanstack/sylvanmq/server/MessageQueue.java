package com.yuanstack.sylvanmq.server;

import com.yuanstack.sylvanmq.model.Message;
import com.yuanstack.sylvanmq.model.Stat;
import com.yuanstack.sylvanmq.model.Subscription;
import com.yuanstack.sylvanmq.store.Indexer;
import com.yuanstack.sylvanmq.store.Store;
import lombok.Getter;

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
        //queues.put("a", new MessageQueue("a"));
    }

    private Map<String, Subscription> subscriptions = new HashMap<>();
    @Getter
    private final String topic;
    //private Message<?>[] queue = new Message[1024 * 10];
    //private int index = 0;
    @Getter
    private Store store = null;

    public MessageQueue(String topic) {
        this.topic = topic;
        store = new Store(topic);
        store.init();
    }

    public static Stat stat(String topic, String consumerId) {
        MessageQueue queue = queues.get(topic);
        Subscription subscription = queue.subscriptions.get(consumerId);
        return new Stat(subscription, queue.getStore().total(), queue.getStore().pos());
    }

    public int send(Message<String> message) {
        int offset = store.pos();
        message.getHeaders().put("X-offset", String.valueOf(offset));
        store.write(message);
        return offset;
    }

    public static List<Message<?>> batch(String topic, String consumerId, int size) {
        MessageQueue messageQueue = queues.get(topic);
        if (messageQueue == null) throw new RuntimeException("topic not found");
        if (messageQueue.subscriptions.containsKey(consumerId)) {
            int offset = messageQueue.subscriptions.get(consumerId).getOffset();
            int next_offset = 0;
            if (offset > -1) {
                Indexer.Entry entry = Indexer.getEntry(topic, offset);
                next_offset = offset + entry.getLength();
            }
            List<Message<?>> result = new ArrayList<>();
            Message<?> receive = messageQueue.receive(next_offset);
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

    public Message<?> receive(int offset) {
        return store.read(offset);
    }

    public void subscribe(Subscription subscription) {
        String consumerId = subscription.getConsumerId();
        subscriptions.putIfAbsent(consumerId, subscription);
    }

    public void unsubscribe(Subscription subscription) {
        String consumerId = subscription.getConsumerId();
        subscriptions.remove(consumerId);
    }

    public static void sub(Subscription subscription) {
        MessageQueue messageQueue = queues.get(subscription.getTopic());
        System.out.println(" ==>> sub: " + subscription);
        if (messageQueue == null) throw new RuntimeException("topic not found");
        messageQueue.subscribe(subscription);
    }

    public static void unsub(Subscription subscription) {
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

    public static Message<?> receive(String topic, String consumerId, int offset) {
        MessageQueue messageQueue = queues.get(topic);
        if (messageQueue == null) {
            throw new RuntimeException("topic not found");
        }
        if (!messageQueue.subscriptions.containsKey(consumerId)) {
            throw new RuntimeException("subscriptions not found for topic/consumerId = "
                    + topic + "/" + consumerId);
        }
        return messageQueue.receive(offset);
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

        int offset = messageQueue.subscriptions.get(consumerId).getOffset();
        int next_offset = 0;
        if (offset > -1) {
            Indexer.Entry entry = Indexer.getEntry(topic, offset);
            next_offset = offset + entry.getLength();
        }
        Message<?> receive = messageQueue.receive(next_offset);
        System.out.println(" ===>> recv: topic/cid/ind = " + topic + "/" + consumerId + "/" + next_offset);
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

        Subscription subscription = messageQueue.subscriptions.get(consumerId);
        if (offset > subscription.getOffset() && offset < Store.LEN) {
            System.out.println(" ===>> ack: topic/cid/offset = "
                    + topic + "/" + consumerId + "/" + offset);
            subscription.setOffset(offset);
            return offset;
        }
        return -1;
    }
}