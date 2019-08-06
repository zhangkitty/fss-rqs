package com.znv.fssrqs.controller.face.compare.n.n;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.service.compareservice.QueryResultService;
import com.znv.fssrqs.util.Result;
import com.znv.fssrqs.util.Template;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:10
 */

@RestController
public class QueryResultController {


    @Autowired
    private QueryResultService queryResultService;

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    /**
     * n:m的分析结果查询
     * @param queryResultParams
     * @return
     */

    @RequestMapping(value = "/site/FSSAPP/pc/nvsm/pairsearch.ds",method = RequestMethod.POST)
    public ResponseVo queryResult(@RequestBody QueryResultParams queryResultParams){

        return  ResponseVo.success(queryResultService.queryResultService(queryResultParams));

    }

    @RequestMapping(value = "/site/FSSAPP/pc/nvsm/pairdel.ds/{taskId}",method = RequestMethod.GET)
    public ResponseVo pairDel(@PathVariable String taskId) {
        // 任务编辑&&没有异常&&重新开始，需要删除历史比对数据


        String url  = "http://10.45.152.230:9200/n2m_face_result_n_project_v1.20/n2m_face_result/_delete_by_query";

        Map<String, String> map = new HashMap<>();
        map.put("taskId", taskId);

        String content = "{\"query\":{\"bool\":{\"filter\":{\"term\":{\"task_id\":\"${taskId}\"}}}}}";

      Result<JSONObject, String>  result = elasticSearchClient.postRequest(url, (JSONObject) JSONObject.parseObject(Template.renderString(content, map)));

      return ResponseVo.success(result.value().get("deleted"));

    }
}
