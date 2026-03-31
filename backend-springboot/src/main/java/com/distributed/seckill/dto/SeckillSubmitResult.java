package com.distributed.seckill.dto;

public class SeckillSubmitResult {
  private boolean success;
  private String message;
  private Long orderId;

  public static SeckillSubmitResult success(Long orderId) {
    SeckillSubmitResult result = new SeckillSubmitResult();
    result.success = true;
    result.message = "QUEUED";
    result.orderId = orderId;
    return result;
  }

  public static SeckillSubmitResult fail(String message) {
    SeckillSubmitResult result = new SeckillSubmitResult();
    result.success = false;
    result.message = message;
    return result;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }

  public Long getOrderId() {
    return orderId;
  }
}
