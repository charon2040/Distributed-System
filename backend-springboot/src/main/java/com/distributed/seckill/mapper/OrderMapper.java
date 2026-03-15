package com.distributed.seckill.mapper;

import com.distributed.seckill.model.Order;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
  @Insert("INSERT INTO orders (user_id, product_id, status) VALUES (#{userId}, #{productId}, #{status})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Order order);

  @Select("SELECT id, user_id, product_id, status, created_at FROM orders WHERE user_id = #{userId} ORDER BY id DESC")
  List<Order> findByUserId(Long userId);
}
