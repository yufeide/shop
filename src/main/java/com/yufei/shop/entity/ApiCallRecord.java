package com.yufei.shop.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("api_call_record")
public class ApiCallRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String className;

    private String methodName;

    private String apiDesc;

    private Long costTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime callTime;


    private String userId;


    private String requestParams;


    private String responseStatus;
}
