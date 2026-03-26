package com.yufei.shop.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yufei.shop.annotation.TotalTime;
import com.yufei.shop.entity.Product;
import com.yufei.shop.entity.Result;
import com.yufei.shop.mapper.ProductMapper;
import com.yufei.shop.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "商品相关接口",description = "默认对商品增删改查")
@RequestMapping("/api/product")
@AllArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;


    @TotalTime
    @ApiOperation(value = "获取所有商品信息")
    @GetMapping("/items")
    public Result<List<Product>> getItems(){
        List<Product> items = new ArrayList<>();
        items = productService.list();
        return Result.success(items);
    }

    @PutMapping("/update")
    @ApiOperation(value = "根据商品id更新商品数据")
    public Result<String> updateItemById(@RequestBody Product product) {
        // 1. 校验核心参数：id不能为空
        if (product.getId() == null) {
            log.error("商品更新失败：商品ID不能为空");
            return Result.fail("商品ID不能为空");
        }

        // 2. 构建更新Wrapper：只指定WHERE条件，精准更新非null字段
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, product.getId()); // 唯一WHERE条件：只根据id更新

        // 3. 逐个指定要更新的字段（非null才更新，避免SET子句为空）
        updateWrapper
                .set(product.getName() != null, Product::getName, product.getName())
                .set(product.getCategoryId() != null, Product::getCategoryId, product.getCategoryId())
                .set(product.getPrice() != null, Product::getPrice, product.getPrice())
                .set(product.getStock() != null, Product::getStock, product.getStock())
                .set(product.getSeckillStock() != null, Product::getSeckillStock, product.getSeckillStock())
                .set(product.getSeckill_price() != null, Product::getSeckill_price, product.getSeckill_price())
                .set(product.getDescription() != null, Product::getDescription, product.getDescription())
                .set(product.getCoverImg() != null, Product::getCoverImg, product.getCoverImg())
                .set(product.getStatus() != null, Product::getStatus, product.getStatus());

        // 4. 执行更新：第一个参数传null（字段已在wrapper中指定）
        int affectedRows = productMapper.update(null, updateWrapper);

        // 5. 校验更新结果
        if (affectedRows == 0) {
            log.warn("商品更新失败：商品ID={} 不存在或无字段需要更新", product.getId());
            return Result.fail("商品更新失败：商品不存在或无有效更新字段");
        }

        log.info("商品更新成功：商品ID={}，受影响行数={}", product.getId(), affectedRows);
        return Result.success("商品信息更新成功");
    }

}
