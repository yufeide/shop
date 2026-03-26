package com.yufei.shop.entity.dto;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @TableField(fill = FieldFill.INSERT)
//    private LocalDateTime createTime;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @TableField(fill = FieldFill.UPDATE)
//    private LocalDateTime updateTime;
}
