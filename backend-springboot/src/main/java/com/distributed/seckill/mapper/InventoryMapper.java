package com.distributed.seckill.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
/**
 * 库存表访问接口。
 */
public interface InventoryMapper {
  // 条件更新，防止并发下库存扣成负数
  @Update("UPDATE inventory SET stock = stock - 1 WHERE product_id = #{productId} AND stock > 0")
  int decreaseStock(Long productId);
}
