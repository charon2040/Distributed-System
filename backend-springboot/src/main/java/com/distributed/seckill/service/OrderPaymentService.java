package com.distributed.seckill.service;

import com.distributed.seckill.dto.OrderPayMessage;
import com.distributed.seckill.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderPaymentService {
  private static final String PAY_TOPIC = "order-pay";
  private final OrderService orderService;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final StringRedisTemplate redisTemplate;

  public OrderPaymentService(
      OrderService orderService,
      KafkaTemplate<String, String> kafkaTemplate,
      ObjectMapper objectMapper,
      StringRedisTemplate redisTemplate) {
    this.orderService = orderService;
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.redisTemplate = redisTemplate;
  }

  public String submitPay(Long orderId) {
    Order order = orderService.findById(orderId);
    if (order == null) {
      return "NOT_FOUND";
    }
    if ("PAID".equals(order.getStatus())) {
      return "PAID";
    }
    redisTemplate.opsForValue().set(statusKey(orderId), "PAYING", Duration.ofHours(1));
    OrderPayMessage message = new OrderPayMessage();
    message.setOrderId(orderId);
    try {
      kafkaTemplate.send(PAY_TOPIC, String.valueOf(orderId), objectMapper.writeValueAsString(message));
      return "PAYING";
    } catch (JsonProcessingException | RuntimeException ex) {
      redisTemplate.opsForValue().set(statusKey(orderId), "PAY_FAILED", Duration.ofHours(1));
      return "PAY_FAILED";
    }
  }

  public String getPayStatus(Long orderId) {
    return redisTemplate.opsForValue().get(statusKey(orderId));
  }

  public void markPayStatus(Long orderId, String status) {
    redisTemplate.opsForValue().set(statusKey(orderId), status, Duration.ofHours(24));
  }

  private String statusKey(Long orderId) {
    return "order:pay:status:" + orderId;
  }
}
