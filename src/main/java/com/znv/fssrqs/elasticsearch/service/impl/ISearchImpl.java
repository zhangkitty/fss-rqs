package com.znv.fssrqs.elasticsearch.service.impl;

import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.elasticsearch.service.ISearch;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.lucene.search.function.ScoreFunction;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryShardContext;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        SearchResponse searchResponse = elasticSearchClient.getClient().search(searchRequest);
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
        SearchResponse searchResponse = elasticSearchClient.getClient().search(searchRequest);
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

        MatchAllQueryBuilder matchAllQueryBuilder =   QueryBuilders.matchAllQuery();


        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        HashMap params = new HashMap();
        params.put("featureName","rt_feature.feature_high");
        params.put("featureValue",new String[]{
                "",
                "",
                ""
        });
        params.put("filterType","or");


        Script script = new Script(ScriptType.INLINE,"native","feature-comp-task",params);

        ScriptScoreFunctionBuilder scriptScoreFunctionBuilder =  ScoreFunctionBuilders.scriptFunction(script);

        FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(matchAllQueryBuilder,scriptScoreFunctionBuilder);

        searchSourceBuilder.query(queryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = elasticSearchClient.getClient().search(searchRequest);

        System.out.println("mdzzz");


    }


}
