package com.yufei.shop.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 公共字段自动填充处理器
 */
@Component // 必须交给Spring容器管理，否则不生效
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作时填充字段
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充createTime：插入时设置为当前时间
        this.strictInsertFill(
                metaObject,          // 元对象（封装了实体类数据）
                "createTime",        // 要填充的字段名（必须和实体类字段一致）
                LocalDateTime.class, // 字段类型
                LocalDateTime.now()  // 填充值（当前时间）
        );

        // 填充updateTime：插入时也设置为当前时间
        this.strictInsertFill(
                metaObject,
                "updateTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );

//        // 扩展：填充创建人（可从登录上下文获取，示例用固定值）
//        this.strictInsertFill(
//                metaObject,
//                "createBy",
//                String.class,
//                "system" // 实际项目中可替换为 SecurityUtils.getCurrentUser()
//        );
//
//        // 扩展：填充更新人
//        this.strictInsertFill(
//                metaObject,
//                "updateBy",
//                String.class,
//                "system"
//        );
    }

    /**
     * 更新操作时填充字段
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 填充updateTime：更新时设置为当前时间
        this.strictUpdateFill(
                metaObject,
                "updateTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );

//        // 扩展：填充更新人
//        this.strictUpdateFill(
//                metaObject,
//                "updateBy",
//                String.class,
//                "system" // 实际项目中替换为当前登录用户
//        );
    }
}
