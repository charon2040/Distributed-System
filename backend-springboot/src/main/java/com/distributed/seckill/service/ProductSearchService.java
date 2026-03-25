package com.distributed.seckill.service;

import com.distributed.seckill.mapper.ProductMapper;
import com.distributed.seckill.model.ProductSearch;
import com.distributed.seckill.search.ProductSearchDocument;
import com.distributed.seckill.search.ProductSearchRepository;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * 商品搜索服务：
 * 1) 启动时将 MySQL 商品数据写入 ES 索引
 * 2) 搜索优先走 ES，提高模糊检索能力
 * 3) ES 不可用时回退到 MySQL LIKE 查询，保证功能可用
 */
public class ProductSearchService {
  private final ProductMapper productMapper;
  private final ProductSearchRepository productSearchRepository;

  public ProductSearchService(
      ProductMapper productMapper, ProductSearchRepository productSearchRepository) {
    this.productMapper = productMapper;
    this.productSearchRepository = productSearchRepository;
  }

  @PostConstruct
  public void rebuildIndex() {
    try {
      // 从数据库拉取全量商品，构建索引文档
      List<ProductSearch> products = productMapper.findAllForSearchIndex();
      List<ProductSearchDocument> docs = new ArrayList<>();
      for (ProductSearch product : products) {
        docs.add(toDocument(product));
      }

      // 覆盖式写入索引，便于本地演示环境快速初始化
      productSearchRepository.saveAll(docs);
    } catch (RuntimeException ignored) {
      // ES 初始化失败不阻断应用启动，后续查询会自动走 MySQL 兜底
    }
  }

  @Transactional(readOnly = true)
  public List<ProductSearch> search(String keyword) {
    // 统一清洗输入，避免 null 与空白串导致的分支混乱
    String safeKeyword = keyword == null ? "" : keyword.trim();
    if (safeKeyword.isEmpty()) {
      // 无关键词时返回全量商品列表
      return productMapper.findAllForSearchIndex();
    }
    try {
      // 优先 ES 多字段查询（name + description）
      List<ProductSearchDocument> docs = productSearchRepository.searchByKeyword(safeKeyword);
      if (!docs.isEmpty()) {
        List<ProductSearch> result = new ArrayList<>();
        for (ProductSearchDocument doc : docs) {
          result.add(toModel(doc));
        }
        return result;
      }
    } catch (RuntimeException ignored) {
      // ES 请求异常时降级走数据库
    }

    // ES 无结果或异常时，回退 MySQL 模糊查询
    return productMapper.searchByKeyword(safeKeyword);
  }

  private ProductSearchDocument toDocument(ProductSearch product) {
    // 领域模型 -> ES 索引文档
    ProductSearchDocument doc = new ProductSearchDocument();
    doc.setId(product.getId());
    doc.setName(product.getName());
    doc.setDescription(product.getDescription());
    doc.setPrice(product.getPrice());
    doc.setImageUrl(product.getImageUrl());
    return doc;
  }

  private ProductSearch toModel(ProductSearchDocument doc) {
    // ES 索引文档 -> 对外返回模型
    ProductSearch product = new ProductSearch();
    product.setId(doc.getId());
    product.setName(doc.getName());
    product.setDescription(doc.getDescription());
    product.setPrice(doc.getPrice());
    product.setImageUrl(doc.getImageUrl());
    return product;
  }
}
