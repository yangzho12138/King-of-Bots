package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import com.kob.backend.utils.RedisUtil;
import com.kob.backend.utils.VerificationCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // the bean in SecurityConfig

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sendMailer;


    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword, String verificationCode) {
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

        // check verification code
        String code = redisUtil.get(username);
        if(code.equals(verificationCode) == false){
            map.put("message","the verification code is wrong");
            return map;
        }

        redisUtil.delete(username);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user_name_exist = userMapper.selectOne(queryWrapper);
        if(user_name_exist != null){
            map.put("message","the username is already exist");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        // photo

        User user = new User(null, username, encodedPassword, null);
        userMapper.insert(user);

        map.put("message","success");
        return map;
    }

    @Override
    public Map<String, String> getVC(String username) {
        Map<String, String> map = new HashMap<>();

        // check username
        if(username.contains("@") == false){
            log.info(username);
            map.put("message", "please check your email address");
            return map;
        }


        String VC = VerificationCodeUtil.CAPTCHA(4);
        if(redisUtil.get(username) != null){
            redisUtil.delete(username);
        }
        redisUtil.setForTimeMS(username, VC, 1000*60*5); // 5min

        // send email
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom(sendMailer);
            message.setTo(username);
            message.setSubject("Verification Code of 'King of Bots'");
            message.setText("Your verification code is: " + VC +", please use it within 5 mins.");
            message.setSentDate(new Date());
            javaMailSender.send(message);
            map.put("message", "success");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("message","the email can not be sent to the given address");
        }

        return map;
    }
}
