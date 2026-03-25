package com.distributed.seckill.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
/**
 * 健康检查接口，返回服务状态与实例信息。
 */
public class HealthController {
  private final String instanceName;

  public HealthController(@Value("${INSTANCE_NAME:backend}") String instanceName) {
    this.instanceName = instanceName;
  }

  @GetMapping("/health")
  public Map<String, Object> health() {
    // 前端/网关可通过该接口判断服务是否可用
    return Map.of("status", "ok", "instance", instanceName);
  }
}
