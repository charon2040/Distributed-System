package com.distributed.gateway.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
  @GetMapping("/backend")
  public Map<String, Object> backendFallback() {
    return Map.of(
        "status", "DEGRADED",
        "message", "gateway fallback: backend unavailable or circuit open");
  }
}
