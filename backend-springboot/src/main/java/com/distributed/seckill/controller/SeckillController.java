package com.distributed.seckill.controller;

import com.distributed.seckill.dto.SeckillRequest;
import com.distributed.seckill.model.Order;
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
    // 校验请求参数
    if (request == null || request.getUserId() == null || request.getProductId() == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "INVALID_INPUT"));
    }
    Order order = seckillService.seckill(request.getUserId(), request.getProductId());
    if (order == null) {
      return ResponseEntity.status(409).body(Map.of("message", "OUT_OF_STOCK"));
    }
    return ResponseEntity.status(201).body(Map.of("orderId", order.getId()));
  }
}
