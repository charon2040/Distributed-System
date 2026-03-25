-- 创建复制用户
CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED WITH mysql_native_password BY 'replpass';
-- 授权复制权限
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'repl'@'%';
-- 刷新权限
FLUSH PRIVILEGES;
