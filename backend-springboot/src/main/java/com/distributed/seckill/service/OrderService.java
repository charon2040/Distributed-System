package com.distributed.seckill.service;

import com.distributed.seckill.mapper.OrderMapper;
import com.distributed.seckill.model.Order;
import com.distributed.seckill.util.OrderShardingUtil;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * 订单服务，负责订单查询。
 */
public class OrderService {
  private final OrderMapper orderMapper;
  private final SeckillService seckillService;

  public OrderService(OrderMapper orderMapper, SeckillService seckillService) {
    this.orderMapper = orderMapper;
    this.seckillService = seckillService;
  }

  @Transactional(readOnly = true)
  public List<Order> listByUser(Long userId) {
    return orderMapper.findByUserId(
        OrderShardingUtil.table0ForUser(userId), OrderShardingUtil.table1ForUser(userId), userId);
  }

  @Transactional(readOnly = true)
  public Order findById(Long orderId) {
    return orderMapper.findById(orderId);
  }

  @Transactional(readOnly = true)
  public String getOrderStatus(Long orderId) {
    return seckillService.getOrderStatus(orderId);
  }
}
