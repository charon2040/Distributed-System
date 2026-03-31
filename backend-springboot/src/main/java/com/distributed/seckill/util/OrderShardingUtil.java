package com.distributed.seckill.util;

public final class OrderShardingUtil {
  private OrderShardingUtil() {}

  public static int userShard(Long userId) {
    return Math.floorMod(userId.intValue(), 2);
  }

  public static int tableShard(Long orderId) {
    return Math.floorMod(orderId.intValue(), 2);
  }

  public static String tableForInsert(Long userId, Long orderId) {
    return "orders_u" + userShard(userId) + "_" + tableShard(orderId);
  }

  public static String table0ForUser(Long userId) {
    return "orders_u" + userShard(userId) + "_0";
  }

  public static String table1ForUser(Long userId) {
    return "orders_u" + userShard(userId) + "_1";
  }
}
