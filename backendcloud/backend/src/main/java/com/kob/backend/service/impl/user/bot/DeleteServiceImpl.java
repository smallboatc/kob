package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.DeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeleteServiceImpl implements DeleteService {
    private final BotMapper botMapper;

    @Autowired
    public DeleteServiceImpl(BotMapper botMapper) {
        this.botMapper = botMapper;
    }

    @Override
    public Map<String, String> delete(Map<String, String> botData) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String botIdStr = botData.get("botId");
        Map<String, String> map = new HashMap<>();
        if (null == botIdStr) {
            map.put("error_message", "BotId不能为空！");
            return map;
        }
        int botId = Integer.parseInt(botIdStr);
        Bot bot = botMapper.selectById(botId);

        if (null == bot) {
            map.put("error_message", "Bot不存在或已被删除！");
            return map;
        }

        if (!bot.getUserId().equals(user.getId())) {
            map.put("error_message", "你没有权限删除该Bot！");
            return map;
        }

        // 逻辑删除
        LambdaUpdateWrapper<Bot> lambdaUpdateChainWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateChainWrapper.eq(Bot::getId, botId).set(Bot::getDeleted, 1);
        botMapper.update(null, lambdaUpdateChainWrapper);

        map.put("error_message", "success");
        return map;
    }
}
