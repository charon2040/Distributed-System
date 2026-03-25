package com.distributed.seckill.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 根据当前事务属性决定数据源路由：
 * - readOnly=true 走读库
 * - 其他情况走写库
 */
public class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {
  @Override
  protected Object determineCurrentLookupKey() {
    // Spring 在进入事务时会把只读标记绑定到线程上下文
    if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
      return "read";
    }
    // 默认写库，保证写入一致性
    return "write";
  }
}
