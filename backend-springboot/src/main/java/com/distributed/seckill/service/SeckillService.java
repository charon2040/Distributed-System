package com.distributed.seckill.service;

import com.distributed.seckill.dto.SeckillOrderMessage;
import com.distributed.seckill.dto.SeckillSubmitResult;
import com.distributed.seckill.mapper.InventoryMapper;
import com.distributed.seckill.util.SnowflakeIdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeckillService {
  private static final String SECKILL_TOPIC = "seckill-order-create";
  private final InventoryMapper inventoryMapper;
  private final StringRedisTemplate redisTemplate;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final SnowflakeIdGenerator idGenerator;
  private final ObjectMapper objectMapper;

  public SeckillService(
      InventoryMapper inventoryMapper,
      StringRedisTemplate redisTemplate,
      KafkaTemplate<String, String> kafkaTemplate,
      SnowflakeIdGenerator idGenerator,
      ObjectMapper objectMapper) {
    this.inventoryMapper = inventoryMapper;
    this.redisTemplate = redisTemplate;
    this.kafkaTemplate = kafkaTemplate;
    this.idGenerator = idGenerator;
    this.objectMapper = objectMapper;
  }

  public SeckillSubmitResult submit(Long userId, Long productId) {
    String dedupKey = dedupKey(userId, productId);
    Boolean dedupOk = redisTemplate.opsForValue().setIfAbsent(dedupKey, "1", Duration.ofHours(24));
    if (!Boolean.TRUE.equals(dedupOk)) {
      return SeckillSubmitResult.fail("DUPLICATE_ORDER");
    }

    String stockKey = stockKey(productId);
    Integer currentStock = ensureStockKey(productId, stockKey);
    if (currentStock == null || currentStock <= 0) {
      redisTemplate.delete(dedupKey);
      return SeckillSubmitResult.fail("OUT_OF_STOCK");
    }

    Long remain = redisTemplate.opsForValue().decrement(stockKey);
    if (remain == null || remain < 0) {
      redisTemplate.opsForValue().increment(stockKey);
      redisTemplate.delete(dedupKey);
      return SeckillSubmitResult.fail("OUT_OF_STOCK");
    }

    long orderId = idGenerator.nextId();
    redisTemplate.opsForValue().set(orderStatusKey(orderId), "QUEUED", Duration.ofHours(24));

    SeckillOrderMessage message = new SeckillOrderMessage();
    message.setOrderId(orderId);
    message.setUserId(userId);
    message.setProductId(productId);
    try {
      kafkaTemplate.send(SECKILL_TOPIC, String.valueOf(orderId), objectMapper.writeValueAsString(message));
      return SeckillSubmitResult.success(orderId);
    } catch (JsonProcessingException | RuntimeException ex) {
      redisTemplate.opsForValue().increment(stockKey);
      redisTemplate.delete(dedupKey);
      redisTemplate.opsForValue().set(orderStatusKey(orderId), "FAILED", Duration.ofHours(1));
      return SeckillSubmitResult.fail("QUEUE_ERROR");
    }
  }

  public void markOrderStatus(Long orderId, String status) {
    redisTemplate.opsForValue().set(orderStatusKey(orderId), status, Duration.ofHours(24));
  }

  public String getOrderStatus(Long orderId) {
    return redisTemplate.opsForValue().get(orderStatusKey(orderId));
  }

  public void rollbackSubmit(Long userId, Long productId, Long orderId) {
    redisTemplate.opsForValue().increment(stockKey(productId));
    redisTemplate.delete(dedupKey(userId, productId));
    markOrderStatus(orderId, "FAILED");
  }

  private Integer ensureStockKey(Long productId, String stockKey) {
    String cached = redisTemplate.opsForValue().get(stockKey);
    if (cached != null) {
      try {
        return Integer.parseInt(cached);
      } catch (NumberFormatException ignored) {
      }
    }
    Integer stock = inventoryMapper.findStockByProductId(productId);
    if (stock == null) {
      return null;
    }
    redisTemplate.opsForValue().setIfAbsent(stockKey, String.valueOf(stock), Duration.ofHours(6));
    return stock;
  }

  private String stockKey(Long productId) {
    return "seckill:stock:" + productId;
  }

  private String dedupKey(Long userId, Long productId) {
    return "seckill:dedup:" + userId + ":" + productId;
  }

  private String orderStatusKey(Long orderId) {
    return "seckill:order:status:" + orderId;
  }
}
