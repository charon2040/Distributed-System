#!/bin/sh
# 从库初始化脚本：自动连接主库并启动复制
set -eu

# 等待主库就绪
until mysql -h mysql-master -uroot -prootpass -e "SELECT 1" >/dev/null 2>&1; do
  sleep 2
done

# 读取主库当前 binlog 位点
status=$(mysql -h mysql-master -uroot -prootpass -AN -e "SHOW MASTER STATUS")
log_file=$(echo "$status" | awk '{print $1}')
log_pos=$(echo "$status" | awk '{print $2}')

# 配置复制源并启动复制线程
mysql -uroot -prootpass <<EOSQL
CHANGE REPLICATION SOURCE TO
  SOURCE_HOST='mysql-master',
  SOURCE_PORT=3306,
  SOURCE_USER='repl',
  SOURCE_PASSWORD='replpass',
  SOURCE_LOG_FILE='${log_file}',
  SOURCE_LOG_POS=${log_pos},
  SOURCE_CONNECT_RETRY=5;
START REPLICA;
EOSQL
