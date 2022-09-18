package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot){
        this.bot = bot;
        this.start();

        try {
            this.join(timeout); // 设置线程运行时间（上限），防止用户代码死循环
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.interrupt(); // 中断当前线程
        }
    }

    private String addUid(String code, String uid){ // 在类名后添加uid
        int k = code.indexOf(" implements com.kob.botrunningsystem.utils.BotInterface");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);
        // compile the code(String) from client （动态编译）
        BotInterface botInterface = Reflect.compile(
                "com.kob.botrunningsystem.utils.Bot" + uid, // 同样的类名只会编译一次，因此加上一个随机id
                addUid(bot.getBotCode(), uid)
        ).create().get();

        System.out.println(botInterface.nextMove(bot.getInput()));

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", botInterface.nextMove(bot.getInput()).toString());

        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}
