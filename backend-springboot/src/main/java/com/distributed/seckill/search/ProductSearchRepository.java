package com.distributed.seckill.search;

import java.util.List;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ES 仓储接口，封装商品索引的基础 CRUD 与自定义查询。
 */
public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearchDocument, Long> {
  // 通过 multi_match 同时匹配商品名与描述字段
  @Query("{\"multi_match\":{\"query\":\"?0\",\"fields\":[\"name\",\"description\"]}}")
  List<ProductSearchDocument> searchByKeyword(String keyword);
}
