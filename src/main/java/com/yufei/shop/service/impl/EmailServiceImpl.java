package com.yufei.shop.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.yufei.shop.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String EMAIL_CODE_PREFIX = "email:code:";
    private static final long EMAIL_CODE_EXPIRE_TIME = 5;

    @Override
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("验证码");
        message.setText("您的验证码是：" + code + "，5分钟内有效，请勿泄露给他人。");

        javaMailSender.send(message);

        String key = EMAIL_CODE_PREFIX + to;
        stringRedisTemplate.opsForValue().set(key, code, EMAIL_CODE_EXPIRE_TIME, TimeUnit.MINUTES);

        log.info("验证码已发送到邮箱：{}，验证码：{}", to, code);
    }

    public String generateCode() {
        return RandomUtil.randomNumbers(6);
    }
}
