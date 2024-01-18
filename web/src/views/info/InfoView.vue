<template>
  <ContentField>
    <div class="text_info">
      <p>
        <strong style="font-size: 1.2em;">游戏规则</strong><br><br>
        确切地说，这并不是贪吃蛇。 与传统单人贪吃蛇不同的是，本贪吃蛇为双人对战，每回合玩家同时做出决策控制自己的蛇。<br><br>

        玩家在 13×14 的网格中操纵一条蛇(蛇是一系列坐标构成的有限不重复有顺序的序列，序列中相邻坐标均相邻，即两坐标的 x 轴坐标或 y 轴坐标相同，
        坐标从0开始，x 轴代表行，y 轴代表列，序列中最后一个坐标代表蛇头)，玩家只能控制蛇头的朝向(上、下、左、右)来控制蛇。蛇以恒定的速度
        前进(前进即为序列末尾插入蛇头指向方向下一格坐标，并删除序列头坐标)。蛇的初始位置在网格中的左下角(地图位置[11, 1])与右上角(地图
        位置[1, 12])，初始长度为 1 格。与传统贪吃蛇不同，本游戏在网格中并没有豆子，但蛇会自动长大(长大即为不删除序列头坐标的前进)，前10回
        合每回合长度增加 1，从第 11 回合开始，每 3 回合长度增加 1。<br><br>

        地图为13×14的网格，由1×1的草地与障碍物构成。<br><br>

        蛇头在网格外、障碍物、自己蛇的身体(即序列重复)、对方蛇的身体(即与对方序列有相同坐标)，或非法操作均判定为死亡。任何一条蛇死亡时，
        游戏结束。若蛇同时死亡，判定为平局，否则先死的一方输，另一方赢。
      </p>
    </div>
    <br>
    <div class="text_info">
      <p>
        <strong style="font-size: 1.2em;">交互方式</strong><br><br>
        若玩家选择亲自出马，玩家可直接通过'w'、'a'、's'、'd'键或↑↓←→方向键控制己方蛇的运动方向。<br><br>

        若玩家选择派出 Bot 出战，则 Bot 代码每回合需从标准输入读入一个字符串，并在标准输出中输出一个数字代表己方蛇运动方向。<br><br>

        输入给定一个字符串，表示当前局势，输出在此局势下您所操纵蛇的最佳运动方向。每回合，我们将运行一遍您的代码，并根据代码输出的运动方向
        作为本回合您的蛇的运动方向。<br><br>

        <strong>输入格式:</strong> 初始地图信息#自己的蛇的初始 x 轴坐标#自己的蛇的初始 y 轴坐标#自己的蛇自开始以来每回合的运动方向#对
        手的蛇的初始 x 轴坐标#对手的蛇的初始 y 轴坐标#对手的蛇自开始以来每回合的运动方向<br><br>

        <strong>输出格式:</strong> 一个整数，代表运动方向。0 代表向上，1 代表向右，2 代表向下，3 代表向左。<br><br><br>

        <strong>输入样例:</strong> 1111111111111110001101000001100100000000011000110000100110000000100001100100000000011
        0000100100001100000000010011000010000000110010000110001100000000010011000001011000111111111111111#11#1#(01110
        1210010)#1#12#
        (233322233033)<br><br>

        <strong>输出样例:</strong><br>
        3<br><br>

        <strong>样例解释:</strong><br>
        对于输入，分别拆解。<br><br>
        地图信息：11111111111111100011010000011001000000000110001100001001100000001000011001000000000110000100100001
        100000000010011000010000000110010000110001100000000010011000001011000111111111111111<br><br>
        二维地图信息被按行压缩成一维，地图位置[i, j](0 ≤ i ≤ 12, 0 ≤ j ≤ 13)信息在第 i * 14 + j 位，1 表示该位置为障碍物，0
        表示为草地。<br><br>
        自己的蛇的初始x轴坐标：11。<br><br>
        自己的蛇的初始y轴坐标：1。<br><br>
        自己的蛇自开始以来每回合的运动方向：011101210010。<br><br>
        对手的蛇的初始x轴坐标：1。<br><br>
        对手的蛇的初始y轴坐标：12。<br><br>
        对手的蛇自开始以来每回合的运动方向：233322233033。<br><br>
        对于这样的局势，输出为 3，即向左运动。
      </p>
    </div>
    <br>
    <div class="text_info" style="border: 2px solid #e02424;">
      <p><strong style="font-size: 1.2em;">Bot样例代码（Java）：</strong></p>
      <VAceEditor
          v-bind:value="example"
          @init="editorInit"
          lang="c_cpp"
          theme="textmate"
          style="height: 1000px"
          readonly="true"
          :options="{
      enableBasicAutocompletion: true, // 启用基本自动完成
      enableSnippets: true, // 启用代码段
      enableLiveAutocompletion: true, // 启用实时自动完成
      fontSize: 16, // 设置字号
      tabSize: 2, // 标签大小
      showPrintMargin: false, // 去除编辑器里的竖线
      highlightActiveLine: true,
    }"/>
    </div>
  </ContentField>
</template>

<script>
import ContentField from '../../components/ContentField.vue'
import { VAceEditor } from "vue3-ace-editor";
import ace from "ace-builds";

export default {
  components: {
    ContentField,
    VAceEditor
  },
  setup() {
    ace.config.set(
        "basePath",
        "https://cdn.jsdelivr.net/npm/ace-builds@" +
        require("ace-builds").version +
        "/src-noconflict/");

    const example = "package com.kob.botrunningsystem.utils;\n" +
        "\n" +
        "import java.io.File;\n" +
        "import java.io.FileNotFoundException;\n" +
        "import java.util.ArrayList;\n" +
        "import java.util.List;\n" +
        "import java.util.Scanner;\n" +
        "\n" +
        "public class Bot implements java.util.function.Supplier<Integer> {\n" +
        "    static class Cell {\n" +
        "        public int x, y;\n" +
        "        public Cell(int x, int y) {\n" +
        "            this.x = x;\n" +
        "            this.y = y;\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    private boolean check_tail_increasing(int step) {  // 检验当前回合，蛇的长度是否增加\n" +
        "        if (step <= 10) return true;\n" +
        "        return step % 3 == 1;\n" +
        "    }\n" +
        "\n" +
        "    public List<Cell> getCells(int sx, int sy, String steps) {\n" +
        "        steps = steps.substring(1, steps.length() - 1);\n" +
        "        List<Cell> res = new ArrayList<>();\n" +
        "\n" +
        "        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};\n" +
        "        int x = sx, y = sy;\n" +
        "        int step = 0;\n" +
        "        res.add(new Cell(x, y));\n" +
        "        for (int i = 0; i < steps.length(); i ++ ) {\n" +
        "            int d = steps.charAt(i) - '0';\n" +
        "            x += dx[d];\n" +
        "            y += dy[d];\n" +
        "            res.add(new Cell(x, y));\n" +
        "            if (!check_tail_increasing( ++ step)) {\n" +
        "                res.remove(0);\n" +
        "            }\n" +
        "        }\n" +
        "        return res;\n" +
        "    }\n" +
        "\n" +
        "    public Integer nextMove(String input) {\n" +
        "        String[] strs = input.split(\"#\");\n" +
        "        int[][] g = new int[13][14];\n" +
        "        for (int i = 0, k = 0; i < 13; i ++ ) {\n" +
        "            for (int j = 0; j < 14; j ++, k ++ ) {\n" +
        "                if (strs[0].charAt(k) == '1') {\n" +
        "                    g[i][j] = 1;\n" +
        "                }\n" +
        "            }\n" +
        "        }\n" +
        "\n" +
        "        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);\n" +
        "        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);\n" +
        "\n" +
        "        List<Cell> aCells = getCells(aSx, aSy, strs[3]);\n" +
        "        List<Cell> bCells = getCells(bSx, bSy, strs[6]);\n" +
        "\n" +
        "        for (Cell c: aCells) g[c.x][c.y] = 1;\n" +
        "        for (Cell c: bCells) g[c.x][c.y] = 1;\n" +
        "\n" +
        "        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};\n" +
        "        for (int i = 0; i < 4; i ++ ) {\n" +
        "            int x = aCells.get(aCells.size() - 1).x + dx[i];\n" +
        "            int y = aCells.get(aCells.size() - 1).y + dy[i];\n" +
        "            if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {\n" +
        "                return i;\n" +
        "            }\n" +
        "        }\n" +
        "\n" +
        "        return 0;\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public Integer get() {\n" +
        "        File file = new File(\"input.txt\");\n" +
        "        try {\n" +
        "            Scanner sc = new Scanner(file);\n" +
        "            return nextMove(sc.next());\n" +
        "        } catch (FileNotFoundException e) {\n" +
        "            throw new RuntimeException(e);\n" +
        "        }\n" +
        "    }\n" +
        "}\n";
    return {
      example
    }
  }
}
</script>

<style scoped>
.text_info {
  border: 2px solid #6eb437;
  border-radius: 10px;
  padding: 10px;
  width: 90%;
  margin: auto;
}

</style>
