package com.kob.backend.service.user.bot;

import com.kob.backend.pojo.Bot;

import java.util.List;

public interface GetListService {
    List<Bot> getList(); // need username —— stored in token —— there is no need for parameter
}
