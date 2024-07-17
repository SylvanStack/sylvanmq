package com.yuanstack.sylvanmq.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yuanstack.sylvanmq.model.Message;
import com.yuanstack.sylvanmq.model.Result;
import com.yuanstack.sylvanmq.utils.HttpUtils;
import com.yuanstack.sylvanmq.utils.ThreadUtils;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * topics 管理
 *
 * @author Sylvan
 * @date 2024/07/14  16:58
 */
public class Broker {
    public static String brokerUrl = "http://localhost:8765/sylvanmq";
    @Getter
    public static Broker Default = new Broker();
    @Getter
    private final MultiValueMap<String, Consumer<?>> consumers = new LinkedMultiValueMap<>();


    static {
        init();
    }

    public static void init() {
        ThreadUtils.getDefault().init(1);
        ThreadUtils.getDefault().schedule(() -> {
            MultiValueMap<String, Consumer<?>> consumers = getDefault().getConsumers();
            consumers.forEach((topic, consumers1) -> {
                consumers1.forEach(consumer -> {
                    Message<?> receive = consumer.receive(topic);
                    if (receive == null) return;
                    try {
                        consumer.getListener().onMessage(receive);
                        consumer.ack(topic, receive);
                    } catch (Exception ex) {
                        // TODO
                    }
                });
            });

        }, 100, 100);
    }

    public Producer createProducer() {
        return new Producer(this);
    }

    public Consumer<?> createConsumer(String topic) {
        Consumer<?> consumer = new Consumer<>(this);
        consumer.subscribe(topic);
        return consumer;
    }

    public void send(String topic, Message<?> message) {
        System.out.println(" ==>> send topic/message: " + topic + "/" + message);
        System.out.println(JSON.toJSONString(message));
        Result<String> result = HttpUtils.httpPost(
                JSON.toJSONString(message),
                brokerUrl + "/send?t=" + topic, new TypeReference<Result<String>>() {
                });
        System.out.println(" ==>> send result: " + result);
    }

    public void subscribe(String topic, String cid) {
        System.out.println(" ==>> subscribe topic/cid: " + topic + "/" + cid);
        Result<String> result = HttpUtils.httpGet(brokerUrl + "/subscribe?t=" + topic + "&cid=" + cid, new TypeReference<Result<String>>() {
        });
        System.out.println(" ==>> subscribe result: " + result);
    }

    public <T> Message<T> receive(String topic, String id) {
        System.out.println(" ==>> receive topic/id: " + topic + "/" + id);
        Result<Message<String>> result = HttpUtils.httpGet(
                brokerUrl + "/receive?t=" + topic + "&cid=" + id,
                new TypeReference<Result<Message<String>>>() {
                });
        System.out.println(" ==>> receive result: " + result);
        return (Message<T>) result.getData();
    }

    public void unsubscribe(String topic, String cid) {
        System.out.println(" ==>> unsubscribe topic/cid: " + topic + "/" + cid);
        Result<String> result = HttpUtils.httpGet(brokerUrl + "/unsubscribe?t=" + topic + "&cid=" + cid,
                new TypeReference<Result<String>>() {
                });
        System.out.println(" ==>> unsubscribe result: " + result);
    }

    public boolean ack(String topic, String cid, int offset) {
        System.out.println(" ==>> ack topic/cid/offset: " + topic + "/" + cid + "/" + offset);
        Result<String> result = HttpUtils.httpGet(
                brokerUrl + "/ack?t=" + topic + "&cid=" + cid + "&offset=" + offset,
                new TypeReference<Result<String>>() {
                });
        System.out.println(" ==>> ack result: " + result);
        return result.getCode() == 1;
    }

    public void addConsumer(String topic, Consumer<?> consumer) {
        consumers.add(topic, consumer);
    }
}
