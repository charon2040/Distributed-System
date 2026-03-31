package com.distributed.seckill.controller;

import com.distributed.seckill.dto.SeckillRequest;
import com.distributed.seckill.dto.SeckillSubmitResult;
import com.distributed.seckill.service.SeckillService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
/**
 * 秒杀下单接口。
 */
public class SeckillController {
  private final SeckillService seckillService;

  public SeckillController(SeckillService seckillService) {
    this.seckillService = seckillService;
  }

  @PostMapping("/seckill")
  public ResponseEntity<?> seckill(@RequestBody SeckillRequest request) {
    if (request == null || request.getUserId() == null || request.getProductId() == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "INVALID_INPUT"));
    }
    SeckillSubmitResult result = seckillService.submit(request.getUserId(), request.getProductId());
    if (!result.isSuccess()) {
      if ("OUT_OF_STOCK".equals(result.getMessage())) {
        return ResponseEntity.status(409).body(Map.of("message", "OUT_OF_STOCK"));
      }
      if ("DUPLICATE_ORDER".equals(result.getMessage())) {
        return ResponseEntity.status(409).body(Map.of("message", "DUPLICATE_ORDER"));
      }
      return ResponseEntity.status(503).body(Map.of("message", result.getMessage()));
    }
    return ResponseEntity.status(202).body(Map.of("orderId", result.getOrderId(), "status", "QUEUED"));
  }
}
