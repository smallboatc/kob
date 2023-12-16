package com.kob.backend.comsumer.utils;

import java.util.Arrays;
import java.util.Random;

public class Game {
    /**
     * 游戏地图行数
     */
    private final Integer rows;
    /**
     * 游戏地图列数
     */
    private final Integer cols;
    /**
     * 地图内部墙体障碍物数量
     */
    private final Integer innerWallsCount;
    /**
     * 游戏地图（0 表示草地，1 表示墙体障碍物）
     */
    private final int[][] g;

    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    public Game(Integer rows, Integer cols, Integer innerWallsCount) {
        this.rows = rows;
        this.cols = cols;
        this.innerWallsCount = innerWallsCount;
        this.g = new int[rows][cols];
    }

    public int[][] getG() {
        return g;
    }

    // Flood Fill 校验地图连通性
    public boolean checkConnectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        // 表示已经走过
        g[sx][sy] = 1;
        for (int d = 0; d < 4; d ++) {
            int a = sx + dx[d], b = sy + dy[d];
            if (a >= 0 && a < this.rows && b >= 0 && b < this.cols && g[a][b] == 0) {
                if (checkConnectivity(a, b, tx, ty)) {
                    // 恢复现场
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        // 即使校验失败也要恢复现场
        g[sx][sy] = 0;
        return false;

    }

    // 画地图
    private boolean draw() {
        // 初始化地图
        for (int i = 0; i < g.length; i ++) {
            Arrays.fill(g[i], 0);
        }
        // 生成四周墙体障碍物
        for (int r = 0; r < this.rows; r ++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c ++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }
        // 生成地图内部随机墙体障碍物
        Random random = new Random();
        for (int i = 0; i < this.innerWallsCount >> 1; i ++) {
            for (int j = 0; j < 1000; j ++) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) {
                    continue;
                }
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) {
                    continue;
                }
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        // 校验连通性
        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        // 循环 1000 次，直到成功生成合法地图
        for (int i = 0; i < 1000; i ++) {
            if (draw()) {
                break;
            }
        }
    }
}
