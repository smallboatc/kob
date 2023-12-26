package com.kob.botrunningsystem.service.impl.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bot {
    /**
     * bot 所属的 user 的 id
     */
    private Integer userId;

    /**
     * bot 代码
     */
    private String botCode;

    /**
     * 当前的局面
     */
    private String input;
}
