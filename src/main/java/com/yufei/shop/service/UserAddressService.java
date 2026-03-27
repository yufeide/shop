package com.yufei.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yufei.shop.entity.UserAddress;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserAddressService extends IService<UserAddress> {

    List<UserAddress> getUserAddressList(Long userId);
}
