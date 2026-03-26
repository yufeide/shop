package com.yufei.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("seckill_record")
public class Seckill_Record implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long productId;
    private Long orderId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
