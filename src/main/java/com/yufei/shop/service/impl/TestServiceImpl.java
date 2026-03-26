package com.yufei.shop.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yufei.shop.entity.User;
import com.yufei.shop.mapper.TestMapper;
import org.springframework.stereotype.Service;
import com.yufei.shop.service.*;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, User> implements TestService {

    @Override
    public String testMethod() {
        return "你好";
    }
}
