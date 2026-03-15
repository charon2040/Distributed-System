package com.distributed.seckill.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
  private final String instanceName;

  public HealthController(@Value("${INSTANCE_NAME:backend}") String instanceName) {
    this.instanceName = instanceName;
  }

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of("status", "ok", "instance", instanceName);
  }
}
