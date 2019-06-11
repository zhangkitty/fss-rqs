package com.znv.fssrqs.elasticsearch.service.impl;

import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.elasticsearch.service.ISearch;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ISearchImpl implements ISearch {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Override
    public List<Map<String, Object>> searchList(Map<String, String> map, String index, String type,int max,Boolean and) throws IOException {
        List list = new ArrayList();
        SearchRequest searchRequest = new SearchRequest(index).types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(max);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for(Map.Entry<String, String> loop:map.entrySet())
        {
            if(and){
                queryBuilder.must(QueryBuilders.termQuery(loop.getKey(),loop.getValue()));
            }else {
                queryBuilder.should(QueryBuilders.termQuery(loop.getKey(),loop.getValue()));
            }
        }
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = elasticSearchClient.getInstance().getClient().search(searchRequest);
        if(searchResponse != null && searchResponse.getHits() != null && searchResponse.getHits().getHits().length > 0)
        {
            for(int num=0;num<searchResponse.getHits().getHits().length;num++)
            {
                SearchHit loop = searchResponse.getHits().getHits()[num];
                list.add(loop.getSourceAsMap());
            }
        }
        return list;
    }

    @Override
    public Map<String, Object> searchListByPage(Map<String, String> map, String index,String type,int pageSize, int pageNum,Boolean and) throws IOException {
        List list = new ArrayList();
        Map<String,Object> result = new HashMap();
        result.put("total",0);
        SearchRequest searchRequest = new SearchRequest(index).types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from((pageNum-1)*pageSize);
        searchSourceBuilder.size(pageSize);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for(Map.Entry<String, String> loop:map.entrySet())
        {
            if(and){
                queryBuilder.must(QueryBuilders.termQuery(loop.getKey(),loop.getValue()));
            }else {
                queryBuilder.should(QueryBuilders.termQuery(loop.getKey(),loop.getValue()));
            }
        }
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = elasticSearchClient.getInstance().getClient().search(searchRequest);
        if(searchResponse != null && searchResponse.getHits() != null && searchResponse.getHits().getHits().length > 0)
        {
            Long totalHits = searchResponse.getHits().totalHits;
            result.put("total",totalHits);
            for(int num=0;num<searchResponse.getHits().getHits().length;num++)
            {
                SearchHit loop = searchResponse.getHits().getHits()[num];
                list.add(loop.getSourceAsMap());
            }
            result.put("data",list);
        }
        return result;
    }

    public void searchPic() throws IOException {
        HttpEntity httpEntity = new NStringEntity("{\"id\":\"template_fast_feature_search\",\"params\":{\"enter_time_start\":\"2017-04-12 11:51:18\",\"enter_time_end\":\"2019-07-16 16:20:00\",\"excludes\":[\"rt_feature\"],\"feature_name\":\"rt_feature.feature_high\",\"size\":1,\"filter_type\":\"or\",\"is_excludes\":true,\"sortOrder\":\"asc\",\"sim_threshold\":0.566,\"sortField\":\"enter_time\",\"from\":0,\"feature_value\":[\"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\"]}}", ContentType.APPLICATION_JSON);
        Response response = elasticSearchClient.getInstance().getRestClient().performRequest("GET", "/history_fss_data_n_project_v1_2/_search/template",Collections.emptyMap(),httpEntity);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }


}
