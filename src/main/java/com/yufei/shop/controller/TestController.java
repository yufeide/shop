package com.yufei.shop.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yufei.shop.annotation.RequirePermission;
import com.yufei.shop.entity.Result;
import com.yufei.shop.entity.User;
import com.yufei.shop.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@Api(tags = "TestController(测试接口)",description = "用来测试连通性的接口")
@AllArgsConstructor
@RestController
public class TestController {

    private final TestService testService;

    @GetMapping("/hello")
    @ApiOperation(value = "测试连通方法接口")
    public String test(){
        return testService.testMethod();
    }


    @GetMapping("/getusers")
    @ApiOperation(value = "测试获取全部用户数据接口")
    public Result<List<User>> getUsers(){
        return Result.success(testService.list());
    }



}
