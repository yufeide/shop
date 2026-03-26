-- 创建数据库（指定字符集，避免中文乱码）
CREATE DATABASE IF NOT EXISTS shop_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shop_mall;

-- ----------------------------
-- 1. 用户表（user）
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                        `username` varchar(50) NOT NULL COMMENT '用户名',
                        `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
                        `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
                        `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                        `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
                        `status` tinyint DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`),
                        UNIQUE KEY `uk_phone` (`phone`),
                        KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 商品分类表（product_category）
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                    `name` varchar(50) NOT NULL COMMENT '分类名称',
                                    `parent_id` bigint DEFAULT 0 COMMENT '父分类ID（0为一级分类）',
                                    `sort` int DEFAULT 0 COMMENT '排序值',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_parent_id` (`parent_id`),
                                    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- 3. 商品表（product）
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                           `name` varchar(100) NOT NULL COMMENT '商品名称',
                           `category_id` bigint NOT NULL COMMENT '分类ID',
                           `price` decimal(10,2) NOT NULL COMMENT '商品价格',
                           `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
                           `seckill_stock` int DEFAULT 0 COMMENT '秒杀库存',
                           `seckill_price` decimal(10,2) DEFAULT 0.00 COMMENT '秒杀价格',
                           `description` text COMMENT '商品描述',
                           `cover_img` varchar(255) DEFAULT NULL COMMENT '封面图',
                           `status` tinyint DEFAULT 1 COMMENT '状态：1-上架 0-下架',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_category_id` (`category_id`),
                           KEY `idx_status` (`status`),
                           KEY `idx_name` (`name`) USING BTREE -- 商品名称模糊查询索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 4. 购物车表（shopping_cart）
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `product_id` bigint NOT NULL COMMENT '商品ID',
                                 `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_user_product` (`user_id`,`product_id`), -- 防止重复添加
                                 KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ----------------------------
-- 5. 订单表（order_info）
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                              `order_no` varchar(64) NOT NULL COMMENT '订单编号（UUID）',
                              `user_id` bigint NOT NULL COMMENT '用户ID',
                              `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
                              `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款 1-已付款 2-已发货 3-已完成 4-已取消',
                              `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
                              `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
                              `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
                              `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_order_no` (`order_no`),
                              KEY `idx_user_id` (`user_id`),
                              KEY `idx_status` (`status`),
                              KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 6. 订单明细表（order_item）
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
                              `order_id` bigint NOT NULL COMMENT '订单ID',
                              `product_id` bigint NOT NULL COMMENT '商品ID',
                              `product_name` varchar(100) NOT NULL COMMENT '商品名称（下单时快照）',
                              `product_price` decimal(10,2) NOT NULL COMMENT '商品价格（下单时快照）',
                              `quantity` int NOT NULL COMMENT '购买数量',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_order_id` (`order_id`),
                              KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ----------------------------
-- 7. 秒杀记录表（seckill_record）
-- ----------------------------
DROP TABLE IF EXISTS `seckill_record`;
CREATE TABLE `seckill_record` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                                  `user_id` bigint NOT NULL COMMENT '用户ID',
                                  `product_id` bigint NOT NULL COMMENT '商品ID',
                                  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '秒杀时间',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`), -- 防止重复秒杀
                                  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀记录表';



-- ----------------------------
-- 8. 用户地址表（user_address）
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
                                `user_id` bigint NOT NULL COMMENT '关联用户ID',
                                `receiver` varchar(50) NOT NULL COMMENT '收货人姓名',
                                `phone` varchar(20) NOT NULL COMMENT '收货人手机号',
                                `province` varchar(20) NOT NULL COMMENT '省份',
                                `city` varchar(20) NOT NULL COMMENT '城市',
                                `district` varchar(20) NOT NULL COMMENT '区县',
                                `detail_address` varchar(255) NOT NULL COMMENT '详细地址（街道/小区/门牌号）',
                                `postal_code` varchar(10) DEFAULT NULL COMMENT '邮政编码',
                                `is_default` tinyint DEFAULT 0 COMMENT '是否默认地址：1-是 0-否（一个用户仅能有一个默认地址）',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_user_id` (`user_id`), -- 按用户ID查询地址（高频场景）
                                KEY `idx_is_default` (`is_default`) -- 查询默认地址（高频场景）
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- ----------------------------
-- 初始化测试地址数据（可选）
-- ----------------------------
INSERT INTO `user_address` (`user_id`, `receiver`, `phone`, `province`, `city`, `district`, `detail_address`, `is_default`)
VALUES
(1, '张三', '13800138000', '北京市', '北京市', '朝阳区', '建国路88号SOHO大厦1001室', 1),
(1, '李四', '13900139000', '上海市', '上海市', '浦东新区', '张江高科技园区博云路2号', 0);


-- ----------------------------
-- 初始化测试数据（可选，方便调试）
-- ----------------------------
-- 1. 测试用户（密码：123456，BCrypt加密后）
INSERT INTO `user` (`username`, `password`, `phone`) VALUES
    ('test_user', '$2a$10$7V95R89Q8X7Z6Y5W4V3U2T1S0A9B8N7M6L5K4J3H2G1F0D9S8A7B6C5V4B3N2M1', '13800138000');

-- 2. 测试分类
INSERT INTO `product_category` (`name`, `parent_id`, `sort`) VALUES
                                                                 ('电子产品', 0, 1),
                                                                 ('手机', 1, 1),
                                                                 ('电脑', 1, 2);

-- 3. 测试商品
INSERT INTO `product` (`name`, `category_id`, `price`, `stock`, `seckill_stock`, `seckill_price`, `description`, `cover_img`) VALUES
                                                                                                                                  ('测试手机', 2, 2999.00, 100, 10, 1999.00, '这是一款测试手机', 'https://test.com/phone.jpg'),
                                                                                                                                  ('测试电脑', 3, 5999.00, 50, 5, 4999.00, '这是一款测试电脑', 'https://test.com/computer.jpg');