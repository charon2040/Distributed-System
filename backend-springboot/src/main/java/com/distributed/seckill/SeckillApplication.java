
package com.distributed.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * Spring Boot 启动入口。
 */
public class SeckillApplication {
  public static void main(String[] args) {
    // 启动容器并加载全部 Bean
    SpringApplication.run(SeckillApplication.class, args);
  }
}
