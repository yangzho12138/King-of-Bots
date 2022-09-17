package com.kob.backend.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerificationCodeUtil {
    public static String CAPTCHA(int n) {
        Random r = new Random();
        String code = new String();
        for (int i = 0; i < n; i++) {
            int type = r.nextInt(3);
            switch (type) {
                case 0://大写字母
                    char c0 = (char) (r.nextInt(26) + 65);//ASCII Uppercase
                    code += c0;
                    break;
                case 1://小写字母
                    char c1 = (char) (r.nextInt(26) + 97);//ASCII Lowercase
                    code += c1;
                    break;
                case 2://数字
                    int m = r.nextInt(10);// 0-9
                    code += m;
                    break;

            }
        }
        return code;
    }
}
