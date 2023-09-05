package com.bilibil.service;

import com.bilibil.config.ElasticSearchConfig;
import com.bilibil.entity.UserInfo;
import com.bilibil.entity.Video;
import com.bilibil.mapper.repository.UserInfoRepository;
import com.bilibil.mapper.repository.VideoRepository;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Date:  2023/9/4
 */
@Service
public class ElasticsearchService {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    // 添加用户数据
    public void addUserInfo(UserInfo userInfo){
        userInfoRepository.save(userInfo);
    }
    // 全文搜索   keyword 搜索的关键词
    public List<Map<String,Object>> getContents(String keyword , Integer pageNo , Integer pageSize) throws IOException {
        String[] indices = {"videos","user-infos"};
        // 搜索请求
        SearchRequest searchRequest = new SearchRequest(indices);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo - 1);
        sourceBuilder.size(pageSize);
        // 多条件下查询   穿关键词和索引
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword
                , "title", "nick", "description");
        // 查询
        sourceBuilder.query(multiMatchQueryBuilder);
        searchRequest.source(sourceBuilder);
        // 设置超时
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 高亮显示
        String[] array = {"title", "nick", "description"};
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String key: array
             ) {
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
        // 如果要多个字段进行高亮，要为false
        highlightBuilder.requireFieldMatch(false);
        // 高亮样式
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        // 设置高亮的配置
        sourceBuilder.highlighter(highlightBuilder);
        // 执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 结果处理
        List<Map<String,Object>> arrayList = new ArrayList<>();
        // 遍历匹配到的条目
        for (SearchHit hit: searchResponse.getHits()) {
            // 获取到高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            // 存放处理好的内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            // 高亮字段进行替换
            for(String key  : array){
                // 突出显示字段
                HighlightField field = highlightFields.get(key);
                if(field != null){
                    Text[] fragments = field.fragments();
                    String str = Arrays.toString(fragments);
                    // 去掉头和尾的括号
                    str = str.substring(1,str.length()-1);
                    // 把查询到的内容替换
                    sourceAsMap.put(key, str);
                }
            }
            arrayList.add(sourceAsMap);
        }
        return arrayList;
    }



    // 添加视频数据
    public void addVideo(Video video){
        videoRepository.save(video);
    }

    // 添查询视频数据
    public Video getVideo(String keyWord){
    // 按标题查找
         return videoRepository.findByTitleLike(keyWord);
    }

    public void deleteVideo(){
        videoRepository.deleteAll();
    }
}
