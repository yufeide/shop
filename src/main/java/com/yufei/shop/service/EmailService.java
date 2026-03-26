package com.yufei.shop.service;

public interface EmailService {
    void sendVerificationCode(String to, String code);
}
