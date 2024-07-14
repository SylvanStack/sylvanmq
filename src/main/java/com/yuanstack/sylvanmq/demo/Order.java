package com.yuanstack.sylvanmq.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Sylvan
 * @date 2024/07/14  17:21
 */
@Data
@AllArgsConstructor
public class Order implements Serializable {
    private String id;
    private String item;
    private double price;
}
