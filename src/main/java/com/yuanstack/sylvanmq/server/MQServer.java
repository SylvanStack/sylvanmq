package com.yuanstack.sylvanmq.server;

import com.yuanstack.sylvanmq.model.Result;
import com.yuanstack.sylvanmq.model.SylvanMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Sylvan
 * @date 2024/07/14  22:58
 */
@Controller
@RequestMapping("/kkmq")
public class MQServer {

    // send
    @RequestMapping("/send")
    public Result<String> send(@RequestParam("t") String topic,
                               @RequestParam("cid") String consumerId,
                               @RequestBody SylvanMessage<String> message) {
        return Result.ok("" + MessageQueue.send(topic, consumerId, message));
    }

    // recv
    @RequestMapping("/recv")
    public Result<SylvanMessage<?>> recv(@RequestParam("t") String topic,
                                         @RequestParam("cid") String consumerId) {
        return Result.msg(MessageQueue.receive(topic, consumerId));
    }

    // ack
    @RequestMapping("/ack")
    public Result<String> ack(@RequestParam("t") String topic,
                              @RequestParam("cid") String consumerId,
                              @RequestParam("offset") Integer offset) {
        return Result.ok("" + MessageQueue.ack(topic, consumerId, offset));
    }

    // 1.sub
    @RequestMapping("/sub")
    public Result<String> subscribe(@RequestParam("t") String topic,
                                    @RequestParam("cid") String consumerId) {
        MessageQueue.sub(new MessageSubscription(topic, consumerId, -1));
        return Result.ok();
    }

    // unsub
    @RequestMapping("/unsub")
    public Result<String> unsubscribe(@RequestParam("t") String topic,
                                      @RequestParam("cid") String consumerId) {
        MessageQueue.unsub(new MessageSubscription(topic, consumerId, -1));
        return Result.ok();
    }
}
