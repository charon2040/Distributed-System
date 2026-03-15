package com.distributed.seckill.service;

import com.distributed.seckill.mapper.OrderMapper;
import com.distributed.seckill.model.Order;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  private final OrderMapper orderMapper;

  public OrderService(OrderMapper orderMapper) {
    this.orderMapper = orderMapper;
  }

  public List<Order> listByUser(Long userId) {
    return orderMapper.findByUserId(userId);
  }
}
