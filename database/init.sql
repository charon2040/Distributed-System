-- 创建数据库
CREATE DATABASE IF NOT EXISTS seckill_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE seckill_system;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    userId BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码 (加密)',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    productId BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品 ID',
    productName VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    categoryId INT COMMENT '分类 ID',
    imageUrl VARCHAR(500) COMMENT '商品图片 URL',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (categoryId),
    INDEX idx_price (price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 库存表
CREATE TABLE IF NOT EXISTS inventory (
    inventoryId BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存 ID',
    productId BIGINT NOT NULL UNIQUE COMMENT '商品 ID',
    stock INT NOT NULL DEFAULT 0 COMMENT '总库存',
    lockedStock INT NOT NULL DEFAULT 0 COMMENT '锁定库存',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product (productId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    orderId VARCHAR(64) PRIMARY KEY COMMENT '订单 ID',
    userId BIGINT NOT NULL COMMENT '用户 ID',
    productId BIGINT NOT NULL COMMENT '商品 ID',
    quantity INT NOT NULL COMMENT '购买数量',
    totalPrice DECIMAL(10, 2) NOT NULL COMMENT '订单总价',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (userId),
    INDEX idx_product (productId),
    INDEX idx_status (status),
    INDEX idx_create_time (createTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入测试数据
INSERT INTO users (username, password, email, phone) VALUES 
('testuser', 'test123', 'test@example.com', '13800138000');

INSERT INTO products (productName, description, price, categoryId, imageUrl) VALUES
('iPhone 15 Pro', 'Apple 最新旗舰手机', 8999.00, 1, 'https://example.com/iphone15pro.jpg'),
('MacBook Pro 14', 'M3 芯片，性能怪兽', 12999.00, 2, 'https://example.com/macbookpro14.jpg'),
('AirPods Pro 2', '主动降噪无线耳机', 1899.00, 3, 'https://example.com/airpodspro2.jpg');

INSERT INTO inventory (productId, stock, lockedStock) VALUES
(1, 100, 0),
(2, 50, 0),
(3, 200, 0);
