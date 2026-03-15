package com.distributed.seckill.service;

import com.distributed.seckill.mapper.ProductMapper;
import com.distributed.seckill.model.Product;
import com.distributed.seckill.model.ProductDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
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

  public List<Product> listProducts() {
    return productMapper.findAll();
  }

  public ProductDetail getDetail(Long id) {
    String key = "product:detail:" + id;
    String cached = redisTemplate.opsForValue().get(key);
    if (cached != null) {
      if ("NULL".equals(cached)) {
        return null;
      }
      try {
        return objectMapper.readValue(cached, ProductDetail.class);
      } catch (JsonProcessingException ex) {
        redisTemplate.delete(key);
      }
    }
    ProductDetail detail = productMapper.findDetail(id);
    if (detail == null) {
      redisTemplate.opsForValue().set(key, "NULL", nullTtl);
      return null;
    }
    try {
      redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(detail), cacheTtl);
    } catch (JsonProcessingException ex) {
      redisTemplate.delete(key);
    }
    return detail;
  }
}
