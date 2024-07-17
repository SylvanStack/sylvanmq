package com.yuanstack.sylvanmq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * stats for mq.
 *
 * @author Sylvan
 * @date 2024/07/17  23:21
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Stat {
    private Subscription subscription;
    //    private int remaining;
    private int total;
    private int position;
}
