package com.znv.fssrqs.controller.face.compare.n.n;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.util.Result;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午3:37
 */

@RestController
public class SaveRemarkController {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @RequestMapping(value = "/site/FSSAPP/pc/nvsm/saveremark.ds", method = RequestMethod.POST)
    public ResponseVo saveRemark(@RequestBody SavaRemarkParams savaRemarkParams) {
        StringBuffer sb = new StringBuffer();
        sb.append("http://").append(elasticSearchClient.getHost()).append(":").append(elasticSearchClient.getPort())
                .append("/n2m_face_result_n_project_v1.20")
                .append("/n2m_face_result")
                .append("/")
                .append(savaRemarkParams.getId())
                .append("/_update");
        JSONObject jsonObject = new JSONObject();
        savaRemarkParams.setId(null);
        jsonObject.put("doc", (JSONObject) JSON.toJSON(savaRemarkParams));
        Result<JSONObject, String> result = elasticSearchClient.postRequest(sb.toString(), jsonObject);
        if (result.value().getString("result").equals("updated")) {
            return ResponseVo.success(null);
        } else {
            return ResponseVo.error("更新失败");
        }

    }
}
