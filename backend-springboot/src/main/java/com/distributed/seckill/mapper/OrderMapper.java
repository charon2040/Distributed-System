package com.distributed.seckill.mapper;

import com.distributed.seckill.model.Order;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

@Mapper
/**
 * 订单表访问接口。
 */
public interface OrderMapper {
  @InsertProvider(type = SqlProvider.class, method = "insert")
  int insert(@Param("table") String table, @Param("order") Order order);

  @SelectProvider(type = SqlProvider.class, method = "findByUserId")
  List<Order> findByUserId(@Param("table0") String table0, @Param("table1") String table1, @Param("userId") Long userId);

  @SelectProvider(type = SqlProvider.class, method = "findById")
  Order findById(Long id);

  @SelectProvider(type = SqlProvider.class, method = "countByUserAndProduct")
  int countByUserIdAndProductId(
      @Param("table0") String table0,
      @Param("table1") String table1,
      @Param("userId") Long userId,
      @Param("productId") Long productId);

  @UpdateProvider(type = SqlProvider.class, method = "updateStatus")
  int updateStatus(
      @Param("table") String table,
      @Param("id") Long id,
      @Param("expectedStatus") String expectedStatus,
      @Param("newStatus") String newStatus);

  class SqlProvider {
    public String insert(@Param("table") String table) {
      return "INSERT INTO "
          + safeTable(table)
          + " (id, user_id, product_id, status) VALUES (#{order.id}, #{order.userId}, #{order.productId}, #{order.status})";
    }

    public String findByUserId(@Param("table0") String table0, @Param("table1") String table1) {
      return "SELECT id, user_id, product_id, status, created_at FROM "
          + safeTable(table0)
          + " WHERE user_id = #{userId} UNION ALL SELECT id, user_id, product_id, status, created_at FROM "
          + safeTable(table1)
          + " WHERE user_id = #{userId} ORDER BY id DESC";
    }

    public String findById() {
      return "SELECT id, user_id, product_id, status, created_at FROM orders_u0_0 WHERE id = #{id} UNION ALL "
          + "SELECT id, user_id, product_id, status, created_at FROM orders_u0_1 WHERE id = #{id} UNION ALL "
          + "SELECT id, user_id, product_id, status, created_at FROM orders_u1_0 WHERE id = #{id} UNION ALL "
          + "SELECT id, user_id, product_id, status, created_at FROM orders_u1_1 WHERE id = #{id} LIMIT 1";
    }

    public String countByUserAndProduct(@Param("table0") String table0, @Param("table1") String table1) {
      return "SELECT (SELECT COUNT(1) FROM "
          + safeTable(table0)
          + " WHERE user_id = #{userId} AND product_id = #{productId}) + "
          + "(SELECT COUNT(1) FROM "
          + safeTable(table1)
          + " WHERE user_id = #{userId} AND product_id = #{productId})";
    }

    public String updateStatus(@Param("table") String table) {
      return "UPDATE "
          + safeTable(table)
          + " SET status = #{newStatus} WHERE id = #{id} AND status = #{expectedStatus}";
    }

    private static String safeTable(String table) {
      return switch (table) {
        case "orders_u0_0", "orders_u0_1", "orders_u1_0", "orders_u1_1" -> table;
        default -> throw new IllegalArgumentException("INVALID_TABLE");
      };
    }
  }
}
