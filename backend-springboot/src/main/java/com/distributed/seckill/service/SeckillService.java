package com.distributed.seckill.service;

import com.distributed.seckill.mapper.InventoryMapper;
import com.distributed.seckill.mapper.OrderMapper;
import com.distributed.seckill.model.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * 秒杀业务服务：
 * 1) 扣减库存
 * 2) 创建订单
 * 3) 删除商品详情缓存，保证后续读取一致
 */
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
    // 原子扣减：仅在库存大于 0 时更新成功
    int updated = inventoryMapper.decreaseStock(productId);
    if (updated == 0) {
      return null;
    }

    // 创建订单记录
    Order order = new Order();
    order.setUserId(userId);
    order.setProductId(productId);
    order.setStatus("CREATED");
    orderMapper.insert(order);

    // 清理详情缓存，避免读到旧库存
    redisTemplate.delete("product:detail:" + productId);
    return order;
  }
}
