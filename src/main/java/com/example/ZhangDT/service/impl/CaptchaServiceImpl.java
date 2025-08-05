package com.example.ZhangDT.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service

public class CaptchaServiceImpl {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 生成并存储验证码
     */
    public String generateCaptcha(){
        //4
        String captcha = generateRandomCode();

        // 生成唯一标识（可以用UUID或时间戳）
        String captchaId = UUID.randomUUID().toString();

        // 存储到Redis，5分钟过期
        stringRedisTemplate.opsForValue().set("captcha:" + captchaId, captcha, 10, TimeUnit.MINUTES);

        return captchaId;
    }

    public boolean validateCaptcha(String captchaId, String userInput) {
        String storedCaptcha = stringRedisTemplate.opsForValue().get("captcha:" + captchaId);
        if (storedCaptcha != null && storedCaptcha.equals(userInput)) {
            stringRedisTemplate.delete("captcha:" + captchaId);
            return true;
        }
        return false;
    }

    public String generateRandomCode(){
        //4位
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }


}
