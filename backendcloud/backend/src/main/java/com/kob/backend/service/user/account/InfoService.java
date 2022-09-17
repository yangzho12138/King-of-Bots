package com.kob.backend.service.user.account;

import java.util.Map;

public interface InfoService {
    // get token info and put into context
    Map<String, String> getInfo();
}
