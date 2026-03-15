package com.distributed.seckill.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface InventoryMapper {
  @Update("UPDATE inventory SET stock = stock - 1 WHERE product_id = #{productId} AND stock > 0")
  int decreaseStock(Long productId);
}
