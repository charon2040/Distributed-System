-- 初始化数据库
CREATE DATABASE IF NOT EXISTS ds DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ds;
SET NAMES utf8mb4;

-- 用户表
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,
  password_hash CHAR(64) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 商品表
CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(512) NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  image_url VARCHAR(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 库存表
CREATE TABLE inventory (
  product_id BIGINT PRIMARY KEY,
  stock INT NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 订单表
CREATE TABLE orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_orders_user (user_id),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 初始化演示用户（demo / pass123）
INSERT INTO users (username, password_hash) VALUES
('demo', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c');

-- 初始化商品
INSERT INTO products (name, description, price, image_url) VALUES
('限量蓝牙耳机', '主动降噪，限量发售', 399.00, 'https://picsum.photos/seed/earbuds/480/320'),
('机械键盘', '热插拔轴体', 499.00, 'https://picsum.photos/seed/keyboard/480/320'),
('智能手表', '全天心率监测', 799.00, 'https://picsum.photos/seed/watch/480/320');

-- 初始化库存
INSERT INTO inventory (product_id, stock) VALUES
(1, 20),
(2, 15),
(3, 10);
