package com.distributed.seckill.service;

import com.distributed.seckill.dto.OrderPayMessage;
import com.distributed.seckill.mapper.OrderMapper;
import com.distributed.seckill.model.Order;
import com.distributed.seckill.util.OrderShardingUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderPaymentConsumer {
  private final OrderService orderService;
  private final OrderMapper orderMapper;
  private final SeckillService seckillService;
  private final OrderPaymentService orderPaymentService;
  private final ObjectMapper objectMapper;

  public OrderPaymentConsumer(
      OrderService orderService,
      OrderMapper orderMapper,
      SeckillService seckillService,
      OrderPaymentService orderPaymentService,
      ObjectMapper objectMapper) {
    this.orderService = orderService;
    this.orderMapper = orderMapper;
    this.seckillService = seckillService;
    this.orderPaymentService = orderPaymentService;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "order-pay", groupId = "order-pay-consumer")
  public void consume(String payload) {
    OrderPayMessage message;
    try {
      message = objectMapper.readValue(payload, OrderPayMessage.class);
    } catch (JsonProcessingException ex) {
      return;
    }
    boolean success = markPaid(message.getOrderId());
    if (success) {
      seckillService.markOrderStatus(message.getOrderId(), "PAID");
      orderPaymentService.markPayStatus(message.getOrderId(), "PAID");
    } else {
      orderPaymentService.markPayStatus(message.getOrderId(), "PAY_FAILED");
    }
  }

  @Transactional
  protected boolean markPaid(Long orderId) {
    Order order = orderService.findById(orderId);
    if (order == null) {
      return false;
    }
    if ("PAID".equals(order.getStatus())) {
      return true;
    }
    String table = OrderShardingUtil.tableForInsert(order.getUserId(), order.getId());
    int updated = orderMapper.updateStatus(table, orderId, "CREATED", "PAID");
    return updated > 0;
  }
}
