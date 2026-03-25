package com.distributed.seckill.mapper;

import com.distributed.seckill.model.Product;
import com.distributed.seckill.model.ProductDetail;
import com.distributed.seckill.model.ProductSearch;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
/**
 * 商品数据访问层：
 * - 提供商品列表/详情查询
 * - 提供 ES 建索引数据源
 * - 提供 ES 不可用时的 MySQL 搜索兜底
 */
public interface ProductMapper {
  // 商品列表（轻量字段）
  @Select("SELECT id, name, price, image_url FROM products ORDER BY id")
  List<Product> findAll();

  // 商品详情（联表库存）
  @Select("SELECT p.id, p.name, p.description, p.price, p.image_url, i.stock FROM products p JOIN inventory i ON p.id = i.product_id WHERE p.id = #{id}")
  ProductDetail findDetail(Long id);

  // 全量商品，用于 ES 索引构建
  @Select("SELECT id, name, description, price, image_url FROM products ORDER BY id")
  List<ProductSearch> findAllForSearchIndex();

  // 兜底搜索：数据库 LIKE 查询
  @Select("SELECT id, name, description, price, image_url FROM products WHERE name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%') ORDER BY id")
  List<ProductSearch> searchByKeyword(String keyword);
}
