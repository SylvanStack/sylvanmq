package com.yuanstack.sylvanmq.server;

import com.yuanstack.sylvanmq.model.Message;
import com.yuanstack.sylvanmq.model.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sylvan
 * @date 2024/07/14  22:58
 */
@RestController
@RequestMapping("/sylvanmq")
public class MQServer {

    @RequestMapping("/send")
    public Result<String> send(@RequestParam("t") String topic,
                               @RequestBody Message<String> message) {
        System.out.println("123131321312312312");
        int index = MessageQueue.send(topic, message);
        return Result.ok("" + index);
    }

    @RequestMapping("/receive")
    public Result<Message<?>> receive(@RequestParam("t") String topic,
                                      @RequestParam("cid") String consumerId) {
        return Result.msg(MessageQueue.receive(topic, consumerId));
    }

    //@RequestMapping("/batch")
    //public Result<List<Message<?>>> batch(@RequestParam("t") String topic,
    //                                      @RequestParam("cid") String consumerId,
    //                                      @RequestParam(name = "size", required = false, defaultValue = "1000") int size) {
    //    //return Result.msg(MessageQueue.batch(topic, consumerId, size));
    //    return Result.msg(String.valueOf(Collections.emptyList()));
    //}

    @RequestMapping("/ack")
    public Result<String> ack(@RequestParam("t") String topic,
                              @RequestParam("cid") String consumerId,
                              @RequestParam("offset") Integer offset) {
        return Result.ok("" + MessageQueue.ack(topic, consumerId, offset));
    }

    @RequestMapping("/subscribe")
    public Result<String> subscribe(@RequestParam("t") String topic,
                                    @RequestParam("cid") String consumerId) {
        MessageQueue.sub(new MessageSubscription(topic, consumerId, -1));
        return Result.ok();
    }

    @RequestMapping("/unsubscribe")
    public Result<String> unsubscribe(@RequestParam("t") String topic,
                                      @RequestParam("cid") String consumerId) {
        MessageQueue.unsub(new MessageSubscription(topic, consumerId, -1));
        return Result.ok();
    }
}
