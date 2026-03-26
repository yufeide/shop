package com.yufei.shop.controller;


import com.yufei.shop.entity.Result;
import com.yufei.shop.service.OllamaService;
import com.yufei.shop.service.SkillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "调用skills的接口",description = "这个路径下是使用已经定义好的skills功能")
@AllArgsConstructor
@RestController
@RequestMapping("/api/ai/skills")
public class SkillsController {


    private final OllamaService ollamaService;


    @ApiOperation(value = "调用计算简单表达式的skill")
    @GetMapping("/calculate")
    public Result<String> calculate(@RequestParam String expression) throws Exception{
        return Result.success(ollamaService.sendToOllama(expression));
    }
}
