package com.distributed.seckill.controller;

import com.distributed.seckill.model.Order;
import com.distributed.seckill.service.OrderPaymentService;
import com.distributed.seckill.service.OrderService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
/**
 * 订单查询接口。
 */
public class OrderController {
  private final OrderService orderService;
  private final OrderPaymentService orderPaymentService;

  public OrderController(OrderService orderService, OrderPaymentService orderPaymentService) {
    this.orderService = orderService;
    this.orderPaymentService = orderPaymentService;
  }

  @GetMapping
  public ResponseEntity<?> list(@RequestParam(required = false) Long userId) {
    if (userId == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "INVALID_USER_ID"));
    }
    List<Order> orders = orderService.listByUser(userId);
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<?> detail(@PathVariable Long orderId) {
    Order order = orderService.findById(orderId);
    if (order != null) {
      return ResponseEntity.ok(order);
    }
    String status = orderService.getOrderStatus(orderId);
    if (status == null) {
      return ResponseEntity.status(404).body(Map.of("message", "NOT_FOUND"));
    }
    return ResponseEntity.ok(Map.of("orderId", orderId, "status", status));
  }

  @PostMapping("/{orderId}/pay")
  public ResponseEntity<?> pay(@PathVariable Long orderId) {
    String result = orderPaymentService.submitPay(orderId);
    if ("NOT_FOUND".equals(result)) {
      return ResponseEntity.status(404).body(Map.of("message", "NOT_FOUND"));
    }
    if ("PAID".equals(result)) {
      return ResponseEntity.ok(Map.of("orderId", orderId, "status", "PAID"));
    }
    if ("PAY_FAILED".equals(result)) {
      return ResponseEntity.status(503).body(Map.of("message", "PAY_FAILED"));
    }
    return ResponseEntity.accepted().body(Map.of("orderId", orderId, "status", result));
  }
}
