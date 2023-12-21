package com.kob.backend.comsumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cell {
    /**
     * 表示蛇的身体中的一个点的横坐标
     */
    private Integer x;

    /**
     * 表示蛇的身体中的一个点的纵坐标
     */
    private Integer y;
}
