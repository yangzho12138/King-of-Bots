package com.kob.backend.service.impl.record;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {

    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        IPage<Record> recordIPage = new Page<>(page, 10); // 第page页，每页10条记录
        QueryWrapper<Record> qw = new QueryWrapper<>();
        qw.orderByDesc("id"); // 按创建时间顺序倒叙排序
        List<Record> records = recordMapper.selectPage(recordIPage, qw).getRecords(); // 返回第page页的内容
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for(Record record : records){
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo", userA.getPhoto());
            item.put("a_username", userA.getUsername());
            item.put("b_photo", userB.getPhoto());
            item.put("b_username", userB.getUsername());
            String result = "Draw";
            if("A".equals(record.getLoser()))
                result = userB.getUsername() + " Win";
            else if("B".equals(record.getLoser()))
                result = userA.getUsername() + " Win";
            item.put("result", result);
            item.put("record", record);
            items.add(item);
        }
        resp.put("records", items);
        resp.put("record_count", recordMapper.selectCount(null));

        return resp;
    }
}
