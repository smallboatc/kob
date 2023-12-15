package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {
    /**
     * 每个 Bot 创建时初始天梯分默认为 1500
     */
    private final Integer DEFAULT_RATING = 1500;
    private final BotMapper botMapper;

    @Autowired
    public AddServiceImpl(BotMapper botMapper) {
        this.botMapper = botMapper;
    }

    @Override
    public Map<String, String> add(Map<String, String> botData) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String title = botData.get("title");
        String description = botData.get("description");
        String content = botData.get("content");

        Map<String, String> map = new HashMap<>();

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

        // 校验一下该 bot的标题和代码，是否已创建过
        QueryWrapper<Bot> botQueryWrapper = new QueryWrapper<>();
        botQueryWrapper.eq("user_id", user.getId());
        botQueryWrapper.and(wrapper -> wrapper.eq("content", content).or().eq("title", title));
        Long count = botMapper.selectCount(botQueryWrapper);
        if (count > 0) {
            map.put("error_message", "该Bot已经被你创建过啦，创建一个新的吧！");
            return map;
        }

        Date now = new Date();
        System.out.println(now);
        Bot bot = new Bot(null, user.getId(), title, description, content, DEFAULT_RATING, null,
                null, null);
        System.out.println(bot);

        int state = botMapper.insert(bot);
        if (state > 0) {
            map.put("error_message", "success");
            return map;
        }

        map.put("error_message", "创建失败！");
        return map;
    }
}
