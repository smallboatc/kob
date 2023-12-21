package com.kob.matchingsystem.service.impl.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * 用户天梯分
     */
    private Integer rating;

    /**
     * 用户已经等待时间
     */
    private Integer waitedTime;
}
