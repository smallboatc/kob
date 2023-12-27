package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record {
    /**
     * 对局id
     */
    @TableId
    private Integer id;

    /**
     * 玩家 A 的 id
     */
    private Integer aId;

    /**
     * 玩家 A 的起点横坐标
     */
    private Integer aSx;

    /**
     * 玩家 A 的起点纵坐标
     */
    private Integer aSy;

    /**
     * 玩家 A 的每一步操作（0, 1, 2, 3 四个方向）
     */
    private String aSteps;

    /**
     * 玩家 B 的 id
     */
    private Integer bId;

    /**
     * 玩家 B 的起点横坐标
     */
    private Integer bSx;

    /**
     * 玩家 B 的起点纵坐标
     */
    private Integer bSy;

    /**
     * 玩家 B 的每一步操作（0, 1, 2, 3 四个方向）
     */
    private String bSteps;

    /**
     * 对局的地图
     */
    private String map;

    /**
     * 赢家
     */
    private String winner;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

}
