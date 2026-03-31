package com.distributed.seckill.service;

import com.distributed.seckill.dto.SeckillOrderMessage;
import com.distributed.seckill.mapper.InventoryMapper;
import com.distributed.seckill.mapper.OrderMapper;
import com.distributed.seckill.model.Order;
import com.distributed.seckill.util.OrderShardingUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillOrderConsumer {
  private final OrderMapper orderMapper;
  private final InventoryMapper inventoryMapper;
  private final SeckillService seckillService;
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public SeckillOrderConsumer(
      OrderMapper orderMapper,
      InventoryMapper inventoryMapper,
      SeckillService seckillService,
      StringRedisTemplate redisTemplate,
      ObjectMapper objectMapper) {
    this.orderMapper = orderMapper;
    this.inventoryMapper = inventoryMapper;
    this.seckillService = seckillService;
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "seckill-order-create", groupId = "seckill-order-consumer")
  public void consume(String payload) {
    SeckillOrderMessage message;
    try {
      message = objectMapper.readValue(payload, SeckillOrderMessage.class);
    } catch (JsonProcessingException ex) {
      return;
    }
    try {
      createOrder(message);
      seckillService.markOrderStatus(message.getOrderId(), "CREATED");
    } catch (DuplicateKeyException ex) {
      seckillService.markOrderStatus(message.getOrderId(), "CREATED");
    } catch (RuntimeException ex) {
      seckillService.rollbackSubmit(message.getUserId(), message.getProductId(), message.getOrderId());
    }
  }

  @Transactional
  protected void createOrder(SeckillOrderMessage message) {
    if (orderMapper.findById(message.getOrderId()) != null) {
      return;
    }
    if (orderMapper.countByUserIdAndProductId(
            OrderShardingUtil.table0ForUser(message.getUserId()),
            OrderShardingUtil.table1ForUser(message.getUserId()),
            message.getUserId(),
            message.getProductId())
        > 0) {
      return;
    }
    int updated = inventoryMapper.decreaseStock(message.getProductId());
    if (updated == 0) {
      throw new IllegalStateException("OUT_OF_STOCK");
    }
    Order order = new Order();
    order.setId(message.getOrderId());
    order.setUserId(message.getUserId());
    order.setProductId(message.getProductId());
    order.setStatus("CREATED");
    orderMapper.insert(OrderShardingUtil.tableForInsert(message.getUserId(), message.getOrderId()), order);
    redisTemplate.delete("product:detail:" + message.getProductId());
  }
}
