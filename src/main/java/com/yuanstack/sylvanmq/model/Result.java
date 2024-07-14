package com.yuanstack.sylvanmq.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Sylvan
 * @date 2024/07/14  23:00
 */
@AllArgsConstructor
@Data
public class Result<T> {
    private int code; // 1==success, 0==fail
    private T data;

    public static Result<String> ok() {
        return new Result<>(1, "OK");
    }

    public static Result<String> ok(String msg) {
        return new Result<>(1, msg);
    }

    public static Result<SylvanMessage<?>> msg(String msg) {
        return new Result<>(1, SylvanMessage.create(msg, null));
    }

    public static Result<SylvanMessage<?>> msg(SylvanMessage<?> msg) {
        return new Result<>(1, msg);
    }
}
