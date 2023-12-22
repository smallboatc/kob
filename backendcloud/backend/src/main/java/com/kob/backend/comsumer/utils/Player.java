package com.kob.backend.comsumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    /**
     * 玩家(蛇蛇) id
     */
    private Integer id;

    /**
     * 起点横坐标
     */
    private Integer sx;

    /**
     * 起点纵坐标
     */
    private Integer sy;

    /**
     * 玩家（蛇蛇）每一步行走的方向(0, 1, 2, 3)
     */
    private List<Integer> steps;

    /**
     * 校验当前回合蛇的长度是否应该增加
     * @return
     */
    private boolean checkSnakeIncreasing(int step) {
        if (step <= 10) return true;
        return step % 3 == 1;
    }


    /**
     * 计算当前蛇蛇的身体
     * @return
     */
    public List<Cell> getCells() {
        List<Cell> res = new LinkedList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int d: steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!checkSnakeIncreasing( ++ step)) {
                res.remove(0);
            }
        }
        return res;
    }

    // 获取玩家每一步的操作的字符串（用于存入数据库）
    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for (int d : steps) {
            res.append(d);
        }
        return res.toString();
    }
}
