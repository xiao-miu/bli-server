package com.bilibil.mapper.repository;

import com.bilibil.entity.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Date:  2023/9/4
 *ElasticsearchRepository 是 Spring Data Elasticsearch 模块提供的接口之一。它是 Spring Data
 * 框架在与 Elasticsearch 进行数据交互时的一部分，用于简化 Elasticsearch 数据库的访问和操作。
 */
public interface VideoRepository extends ElasticsearchRepository<Video,Long> {

    // 按标题查找
    Video findByTitleLike(String keyWord);
}
