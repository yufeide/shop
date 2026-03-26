package com.yufei.shop.controller;

import com.yufei.shop.entity.Result;
import com.yufei.shop.service.EmailService;
import com.yufei.shop.service.impl.EmailServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Api(tags = "邮箱验证码接口", description = "用于发送和验证邮箱验证码")
@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;
    private final EmailServiceImpl emailServiceImpl;

    @ApiOperation("发送邮箱验证码")
    @PostMapping("/sendCode")
    public Result<String> sendVerificationCode(@ApiParam("邮箱地址") @RequestParam String email) {
        try {
            String code = emailServiceImpl.generateCode();
            emailService.sendVerificationCode(email, code);
            return Result.success("验证码发送成功");
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return Result.fail("发送验证码失败：" + e.getMessage());
        }
    }
}
