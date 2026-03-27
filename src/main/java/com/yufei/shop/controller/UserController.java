package com.yufei.shop.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yufei.shop.annotation.RequirePermission;
import com.yufei.shop.annotation.TotalTime;
import com.yufei.shop.entity.Result;
import com.yufei.shop.entity.User;
import com.yufei.shop.entity.UserAddress;
import com.yufei.shop.exception.UserException;
import com.yufei.shop.service.UserAddressService;
import com.yufei.shop.service.UserService;
import com.yufei.shop.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Api(tags = "用户相关接口",description = "默认操作包含用户增删改查")
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserAddressService userAddressService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String EMAIL_CODE_PREFIX = "email:code:";//验证码key
    private static final String TOKEN = "user:login:";//用户token key

    /**
     * 登录接口：生成Token返回给前端
     */
    @PostMapping("/login")
    public Result<String> login(@RequestParam String username, @RequestParam String password) {

        // 1. 模拟登录校验（实际业务中替换为数据库查询）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(StringUtils.isNotBlank(username),User::getUsername,username);
        User user = userService.getOne(wrapper);
        log.info("user的信息{}",user.toString());
        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
            Long userId = user.getId(); // 模拟查询到的用户ID
            // 2. 调用工具类生成Token
            String token = JwtUtil.generateToken(userId);
            //3 把用户token存入redis为后续校验登陆状态提供数据支持
            stringRedisTemplate.opsForValue().set(TOKEN,token,30, TimeUnit.MINUTES);
            // 4. 返回Token给前端（前端存储在localStorage/cookie中）
            return Result.success("登录成功",token);
        }
        return Result.fail("登陆失败");
    }


    @RequirePermission(value = "user:info:update",requireAll = false)
    @ApiOperation(value = "添加指定用户")
    @PostMapping("add")
    public Result<String> addUser(){
        User user = new User();
        user.setUsername("章若楠");
        user.setPassword("123456");
        user.setPhone("13022345678");
        user.setEmail("1994981559@qq.com");
        user.setStatus(1);
        user.setAvatar("https://file-yufei.oss-cn-beijing.aliyuncs.com/shop/202603/20260316_c5ec18c221a345fbac075c98e1598f79.jpg");
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                // 只有 username 不为 null 时，才拼接 eq 条件（自动处理 null 为 IS NULL）
                .eq(StringUtils.isNotBlank(user.getUsername()), User::getUsername, user.getUsername());
        //QueryWrapper<User> wrapper2 = new QueryWrapper<User>().eq(StringUtils.isNotBlank(user.getUsername()),"username",user.getUsername());// 不推荐硬编码
        if(userService.getOne(wrapper) != null){  // 判断是否有同名的的用户 如果有就抛异常
            throw new UserException("该用户名已存在，请更换");
        }
        userService.save(user);
        return Result.success("用户信息更新成功");
    }

    @TotalTime
    @ApiOperation(value = "获取所有用户")
    @GetMapping("/getUsers")
    public Result<List<User>> getUsers(){
        return Result.success(userService.list());
    }

    @ApiOperation(value = "根据id更新指定用户信息")
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user){
        log.info("id,{}",user.toString());
        userService.updateById(user);
        return Result.success("用户信息更新成功");
    }

    @ApiOperation(value = "验证邮箱验证码")
    @PostMapping("/verifyCode")
    public Result<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        String key = EMAIL_CODE_PREFIX + email;
        String storedCode = stringRedisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return Result.fail("验证码已过期或不存在");
        }

        if (!storedCode.equals(code)) {
            return Result.fail("验证码错误");
        }

        stringRedisTemplate.delete(key);
        return Result.success("验证码验证成功");
    }

    @ApiOperation(value = "根据用户id查询所有的地址信息")
    @GetMapping("/get/userAddress")
    public Result<List<UserAddress>> getUserAddress(@RequestParam Long userId){

        List<UserAddress> addressList = userAddressService.getUserAddressList(userId);
        return Result.success(addressList);
    }
}
