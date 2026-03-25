package com.distributed.seckill.controller;

import com.distributed.seckill.model.Product;
import com.distributed.seckill.model.ProductDetail;
import com.distributed.seckill.model.ProductSearch;
import com.distributed.seckill.service.ProductSearchService;
import com.distributed.seckill.service.ProductService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
/**
 * 商品相关接口：
 * - /api/products：商品列表
 * - /api/products/{id}：商品详情（含缓存）
 * - /api/products/search?q=：商品搜索（优先 ES）
 */
public class ProductController {
  private final ProductService productService;
  private final ProductSearchService productSearchService;

  public ProductController(ProductService productService, ProductSearchService productSearchService) {
    this.productService = productService;
    this.productSearchService = productSearchService;
  }

  @GetMapping
  public List<Product> list() {
    return productService.listProducts();
  }

  // 详情不存在时返回 404，前端可据此展示“商品不存在”
  @GetMapping("/{id}")
  public ResponseEntity<?> detail(@PathVariable Long id) {
    ProductDetail detail = productService.getDetail(id);
    if (detail == null) {
      return ResponseEntity.status(404).body(Map.of("message", "NOT_FOUND"));
    }
    return ResponseEntity.ok(detail);
  }

  // 关键词为空时返回全量商品，非空时执行模糊搜索
  @GetMapping("/search")
  public List<ProductSearch> search(@RequestParam(name = "q", required = false) String keyword) {
    return productSearchService.search(keyword);
  }
}
