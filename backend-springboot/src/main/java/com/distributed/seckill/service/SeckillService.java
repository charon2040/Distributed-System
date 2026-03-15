package com.distributed.seckill.service;

import com.distributed.seckill.mapper.InventoryMapper;
import com.distributed.seckill.mapper.OrderMapper;
import com.distributed.seckill.model.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {
  private final InventoryMapper inventoryMapper;
  private final OrderMapper orderMapper;
  private final StringRedisTemplate redisTemplate;

  public SeckillService(
      InventoryMapper inventoryMapper,
      OrderMapper orderMapper,
      StringRedisTemplate redisTemplate) {
    this.inventoryMapper = inventoryMapper;
    this.orderMapper = orderMapper;
    this.redisTemplate = redisTemplate;
  }

  @Transactional
  public Order seckill(Long userId, Long productId) {
    int updated = inventoryMapper.decreaseStock(productId);
    if (updated == 0) {
      return null;
    }
    Order order = new Order();
    order.setUserId(userId);
    order.setProductId(productId);
    order.setStatus("CREATED");
    orderMapper.insert(order);
    redisTemplate.delete("product:detail:" + productId);
    return order;
  }
}
