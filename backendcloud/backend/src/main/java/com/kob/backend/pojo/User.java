package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * 用户 id
     */
    @TableId
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户头像
     */
    private String photo;

    /**
     * 用户天梯分
     */
    private Integer rating;

    /**
     * 用户的 AcWing openid（仅AcWing三方登录的用户）
     */
    private String openid;

    /**
     * 用户的 QQ openid（仅QQ三方登录的用户）
     */
    @TableField(value = "openid_qq")
    private String openidQQ;

    /**
     * 逻辑删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer deleted;
}
