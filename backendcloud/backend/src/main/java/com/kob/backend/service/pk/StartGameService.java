package com.kob.backend.service.pk;

// 接受来自matching system的匹配消息
public interface StartGameService {
    String startGame(Integer aId, Integer bId, Integer aBotId, Integer bBotId);
}
