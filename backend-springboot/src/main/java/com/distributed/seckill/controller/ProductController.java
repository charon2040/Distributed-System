package com.distributed.seckill.controller;

import com.distributed.seckill.model.Product;
import com.distributed.seckill.model.ProductDetail;
import com.distributed.seckill.service.ProductService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public List<Product> list() {
    return productService.listProducts();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> detail(@PathVariable Long id) {
    ProductDetail detail = productService.getDetail(id);
    if (detail == null) {
      return ResponseEntity.status(404).body(Map.of("message", "NOT_FOUND"));
    }
    return ResponseEntity.ok(detail);
  }
}
