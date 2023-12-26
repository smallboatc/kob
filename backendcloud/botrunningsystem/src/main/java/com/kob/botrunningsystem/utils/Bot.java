package com.kob.botrunningsystem.utils;

public class Bot implements com.kob.botrunningsystem.utils.BotInterface{
    static class Cell {
        int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }
    @Override
    public Integer nextMove(String input) {
        return 0;
    }
}
