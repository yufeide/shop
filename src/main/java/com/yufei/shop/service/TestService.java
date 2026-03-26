package com.yufei.shop.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yufei.shop.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface TestService extends IService<User> {

    String testMethod();
}
