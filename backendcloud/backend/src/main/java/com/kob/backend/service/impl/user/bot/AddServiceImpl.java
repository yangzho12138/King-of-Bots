package com.kob.backend.service.impl.user.bot;

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

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        // get username from token
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if(title == null || title.length() == 0){
            map.put("message","the title can not be null or blank");
            return map;
        }

        if(title.length() > 100){
            map.put("message","the title can not be longer than 100 characters");
            return map;
        }

        if(description == null || description.length() == 0){
            description = "BOT's owner left nothing";
        }

        if(description.length() > 300){
            map.put("message","the description can not be longer than 300 characters");
            return map;
        }

        if(content == null || content.length() == 0){
            map.put("message","the content can not be null or blank");
            return map;
        }

        if(content.length() > 10000){
            map.put("message","the content can not be longer than 10000 characters");
            return map;
        }

        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), title, description, content, 0, now, now);

        botMapper.insert(bot);
        map.put("message", "success");

        return map;
    }
}
