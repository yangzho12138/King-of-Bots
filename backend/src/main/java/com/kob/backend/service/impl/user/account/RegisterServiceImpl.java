package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // the bean in SecurityConfig

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if(username == null){
            map.put("message","the username can not be null");
            return map;
        }
        if(password == null || confirmedPassword == null){
            map.put("message","the password can not be null");
            return map;
        }

        username = username.trim();
        if(username.length() == 0){
            map.put("message","the username can not be blank");
            return map;
        }

        if(password.length() == 0 || confirmedPassword.length() == 0){
            map.put("message","the password can not be blank");
            return map;
        }

        if(username.length() > 100){
            map.put("message","the length of username can not be longer than 100 characters");
            return map;
        }

        if(password.length() > 100 || confirmedPassword.length() > 100){
            map.put("message","the length of password can not be longer than 100 characters");
            return map;
        }

        if(!password.equals(confirmedPassword)){
            map.put("message","the confirmed password must be the same to the password");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user_name_exist = userMapper.selectOne(queryWrapper);
        if(user_name_exist != null){
            map.put("message","the username is already exist");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        // photo

        User user = new User(null, username, password, null);
        userMapper.insert(user);

        map.put("message","success");
        return map;
    }
}
