package com.distributed.seckill.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
/**
 * 库存表访问接口。
 */
public interface InventoryMapper {
  @Update("UPDATE inventory SET stock = stock - 1 WHERE product_id = #{productId} AND stock > 0")
  int decreaseStock(Long productId);

  @Select("SELECT stock FROM inventory WHERE product_id = #{productId}")
  Integer findStockByProductId(Long productId);
}
