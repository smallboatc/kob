package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    private final BotMapper botMapper;

    @Autowired
    public UpdateServiceImpl(BotMapper botMapper) {
        this.botMapper = botMapper;
    }

    @Override
    public Map<String, String> update(Map<String, String> botData) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        Integer botId = Integer.parseInt(botData.get("botId"));
        Bot bot = botMapper.selectById(botId);

        Map<String, String> map = new HashMap<>();
        if (null == bot) {
            map.put("error_message", "Bot不存在或已被删除！");
            return map;
        }
        if (!bot.getUserId().equals(user.getId())) {
            map.put("error_message", "你没有权限修改该Bot！");
            return map;
        }

        String title = botData.get("title");
        String description = botData.get("description");
        String content = botData.get("content");

        if (null == title || title.length() == 0) {
            map.put("error_massage", "Bot标题不能为空！");
            return map;
        }
        if (title.length() > 50) {
            map.put("error_message", "Bot标题长度不能超过50！");
            return map;
        }

        // 描述可以为空
        if (null == description || description.length() == 0) {
            description = "这个用户很懒，什么也没写~";
        }
        if (description.length() > 200) {
            map.put("error_message", "Bot描述长度不能超过200！");
            return map;
        }

        if (null == content || content.length() == 0) {
            map.put("error_message", "Bot代码不能为空！");
            return map;
        }
        if (content.length() > 10000) {
            map.put("error_message", "Bot代码长度不能超过10000！");
            return map;
        }

        // 校验一下新 bot 的标题和代码，是否已被该用户创建过
        QueryWrapper<Bot> botQueryWrapper = new QueryWrapper<>();
        botQueryWrapper.eq("user_id", user.getId()).ne("id", botId);
        botQueryWrapper.and(wrapper -> wrapper.eq("content", content).or().eq("title", title));
        Long count = botMapper.selectCount(botQueryWrapper);
        if (count > 0) {
            map.put("error_message", "修改失败，目标Bot已存在！");
            return map;
        }

        bot.setTitle(title);
        bot.setContent(content);
        bot.setDescription(description);
        bot.setGmtModified(new Date());
        botMapper.updateById(bot);

        map.put("error_message", "success");
        return map;
    }
}
