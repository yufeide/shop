package com.yufei.shop.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yufei.shop.entity.Product;
import com.yufei.shop.mapper.ProductMapper;
import com.yufei.shop.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {


}
