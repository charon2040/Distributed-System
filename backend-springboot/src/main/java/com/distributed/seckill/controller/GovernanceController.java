package com.distributed.seckill.controller;

import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/api/governance")
/**
 * 服务治理演示接口：
 * - /config：演示 Nacos 动态配置刷新
 * - /unstable：演示网关限流、熔断与降级
 */
public class GovernanceController {
  private final String instanceName;

  @Value("${governance.dynamic-message:default message}")
  private String dynamicMessage;

  public GovernanceController(@Value("${INSTANCE_NAME:backend}") String instanceName) {
    this.instanceName = instanceName;
  }

  @GetMapping("/config")
  public Map<String, Object> config() {
    return Map.of("instance", instanceName, "dynamicMessage", dynamicMessage);
  }

  @GetMapping("/unstable")
  public ResponseEntity<?> unstable(@RequestParam(defaultValue = "ok") String mode)
      throws InterruptedException {
    if ("error".equalsIgnoreCase(mode)) {
      return ResponseEntity.internalServerError()
          .body(Map.of("instance", instanceName, "status", "error"));
    }
    if ("slow".equalsIgnoreCase(mode)) {
      // 故意制造慢调用，便于触发网关熔断器超时与降级逻辑
      Thread.sleep(Duration.ofSeconds(5).toMillis());
      return ResponseEntity.ok(Map.of("instance", instanceName, "status", "slow"));
    }
    return ResponseEntity.ok(Map.of("instance", instanceName, "status", "ok"));
  }
}
