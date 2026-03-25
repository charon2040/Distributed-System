package com.distributed.seckill.dto;

/**
 * 秒杀下单请求参数。
 */
public class SeckillRequest {
  private Long userId;
  private Long productId;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }
}
