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
public class Bot {
    /**
     * Bot id （自增）
     */
    @TableId
    private Integer id;

    /**
     * Bot 所属 user id
     */
    private Integer userId;

    /**
     * Bot 名称
     */
    private String title;

    /**
     * Bot 描述
     */
    private String description;

    /**
     * Bot 代码
     */
    private String content;

    /**
     * Bot 天梯分
     */
    private Integer rating;

    /**
     * Bot 创建时间，上海时间与 gmt 时间存在 8 小时时差
     */
    @JsonFormat
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * Bot 修改时间 (timezone = "GMT+8"与 timezone = "Asia/Shanghai" 两种写法在此处是等价的)
     */
    @JsonFormat
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /**
     * 逻辑删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer deleted;
}
