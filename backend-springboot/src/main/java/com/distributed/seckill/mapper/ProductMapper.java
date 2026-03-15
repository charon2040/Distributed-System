package com.distributed.seckill.mapper;

import com.distributed.seckill.model.Product;
import com.distributed.seckill.model.ProductDetail;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper {
  @Select("SELECT id, name, price, image_url FROM products ORDER BY id")
  List<Product> findAll();

  @Select("SELECT p.id, p.name, p.description, p.price, p.image_url, i.stock FROM products p JOIN inventory i ON p.id = i.product_id WHERE p.id = #{id}")
  ProductDetail findDetail(Long id);
}
