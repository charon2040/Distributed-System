package com.distributed.seckill.service;

import com.distributed.seckill.mapper.OrderMapper;
import com.distributed.seckill.model.Order;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * 订单服务，负责订单查询。
 */
public class OrderService {
  private final OrderMapper orderMapper;

  public OrderService(OrderMapper orderMapper) {
    this.orderMapper = orderMapper;
  }

  @Transactional(readOnly = true)
  public List<Order> listByUser(Long userId) {
    // 只读事务，路由到读库
    return orderMapper.findByUserId(userId);
  }
}
