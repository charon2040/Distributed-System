package com.distributed.seckill.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
/**
 * 读写分离数据源配置：
 * 1) 创建 writeDataSource 与 readDataSource 两个真实数据源
 * 2) 通过路由数据源按事务属性动态选择读库/写库
 * 3) 将路由数据源声明为主数据源，供 MyBatis/JDBC 统一使用
 */
public class DataSourceConfig {
  @Bean(name = "writeDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.write")
  public DataSource writeDataSource() {
    // 绑定 spring.datasource.write.* 到 HikariDataSource
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean(name = "readDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.read")
  public DataSource readDataSource() {
    // 绑定 spring.datasource.read.* 到 HikariDataSource
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  @Primary
  public DataSource dataSource(DataSource writeDataSource, DataSource readDataSource) {
    // 路由数据源内部维护 key -> 实际数据源 映射
    ReadWriteRoutingDataSource routingDataSource = new ReadWriteRoutingDataSource();
    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put("write", writeDataSource);
    targetDataSources.put("read", readDataSource);
    routingDataSource.setTargetDataSources(targetDataSources);

    // 默认走写库，防止在无事务或无法识别场景下误路由到只读库
    routingDataSource.setDefaultTargetDataSource(writeDataSource);
    return routingDataSource;
  }
}
