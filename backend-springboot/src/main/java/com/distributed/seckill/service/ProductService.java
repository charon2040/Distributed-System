package com.distributed.seckill.service;

import com.distributed.seckill.mapper.ProductMapper;
import com.distributed.seckill.model.Product;
import com.distributed.seckill.model.ProductDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * 商品服务：
 * 1) 提供商品列表与详情查询
 * 2) 在详情查询中使用 Redis 缓存
 * 3) 针对缓存穿透、击穿、雪崩做防护
 */
public class ProductService {
  private final ProductMapper productMapper;
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;
  private final Duration cacheTtl = Duration.ofSeconds(60);
  private final Duration nullTtl = Duration.ofSeconds(15);

  public ProductService(
      ProductMapper productMapper, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
    this.productMapper = productMapper;
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  @Transactional(readOnly = true)
  public List<Product> listProducts() {
    return productMapper.findAll();
  }

  @Transactional(readOnly = true)
  public ProductDetail getDetail(Long id) {
    // 详情缓存 key 约定
    String key = "product:detail:" + id;

    // 第一次查缓存：命中则直接返回（包含命中空值占位的场景）
    CacheLookup lookup = lookupCache(key);
    if (lookup.hit) {
      return lookup.value;
    }

    // 缓存未命中时尝试加锁，只允许一个请求回源数据库，避免热点击穿
    String lockKey = "lock:" + key;
    Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(10));
    if (Boolean.TRUE.equals(locked)) {
      try {
        // 双检：拿到锁后再查一次缓存，避免并发窗口内重复回源
        CacheLookup secondLookup = lookupCache(key);
        if (secondLookup.hit) {
          return secondLookup.value;
        }

        // 回源数据库
        ProductDetail detail = productMapper.findDetail(id);
        if (detail == null) {
          // 数据不存在：写入短 TTL 空值，防止缓存穿透
          redisTemplate.opsForValue().set(key, "NULL", nullTtlWithJitter());
          return null;
        }
        try {
          // 正常数据写缓存，并加随机抖动，分散过期时间防雪崩
          redisTemplate
              .opsForValue()
              .set(key, objectMapper.writeValueAsString(detail), cacheTtlWithJitter());
        } catch (JsonProcessingException ex) {
          // 序列化异常时清理 key，避免脏值留存
          redisTemplate.delete(key);
        }
        return detail;
      } finally {
        // 无论业务是否异常，最终释放互斥锁
        redisTemplate.delete(lockKey);
      }
    }

    // 未拿到锁：短暂等待锁持有者回填缓存，减少直接穿透到数据库
    for (int i = 0; i < 3; i++) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        break;
      }
      CacheLookup retryLookup = lookupCache(key);
      if (retryLookup.hit) {
        return retryLookup.value;
      }
    }

    // 多次重试仍未命中，最后兜底回源（保证可用性）
    ProductDetail detail = productMapper.findDetail(id);
    if (detail == null) {
      redisTemplate.opsForValue().set(key, "NULL", nullTtlWithJitter());
      return null;
    }
    try {
      redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(detail), cacheTtlWithJitter());
    } catch (JsonProcessingException ex) {
      redisTemplate.delete(key);
    }
    return detail;
  }

  private CacheLookup lookupCache(String key) {
    // Redis 返回 null 表示 key 不存在，不是空对象命中
    String cached = redisTemplate.opsForValue().get(key);
    if (cached == null) {
      return CacheLookup.miss();
    }

    // 约定字符串 "NULL" 表示空对象缓存命中
    if ("NULL".equals(cached)) {
      return CacheLookup.hit(null);
    }
    try {
      // 正常命中：反序列化为详情对象返回
      return CacheLookup.hit(objectMapper.readValue(cached, ProductDetail.class));
    } catch (JsonProcessingException ex) {
      // 反序列化失败说明缓存格式异常，删掉后按未命中处理
      redisTemplate.delete(key);
      return CacheLookup.miss();
    }
  }

  private Duration cacheTtlWithJitter() {
    // 正常缓存 60s 基础上增加 0~29s 随机抖动
    return cacheTtl.plusSeconds(ThreadLocalRandom.current().nextInt(0, 30));
  }

  private Duration nullTtlWithJitter() {
    // 空值缓存 15s 基础上增加 0~9s 随机抖动
    return nullTtl.plusSeconds(ThreadLocalRandom.current().nextInt(0, 10));
  }

  // 统一缓存查询结果：hit=true 代表有缓存（包括空值缓存），hit=false 代表未命中
  private static class CacheLookup {
    private final boolean hit;
    private final ProductDetail value;

    private CacheLookup(boolean hit, ProductDetail value) {
      this.hit = hit;
      this.value = value;
    }

    private static CacheLookup hit(ProductDetail value) {
      return new CacheLookup(true, value);
    }

    private static CacheLookup miss() {
      return new CacheLookup(false, null);
    }
  }
}
